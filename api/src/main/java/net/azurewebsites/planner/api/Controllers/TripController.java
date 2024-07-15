package net.azurewebsites.planner.api.Controllers;

import jakarta.mail.MessagingException;
import net.azurewebsites.planner.api.Services.EmailService;
import net.azurewebsites.planner.core.Models.*;
import net.azurewebsites.planner.core.Repositories.ActivityRepository;
import net.azurewebsites.planner.core.Repositories.ParticipantRepository;
import net.azurewebsites.planner.core.Repositories.TripRepository;
import net.azurewebsites.planner.api.Services.ParticipantServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/trips")
public class TripController {

    static final Context CONTEXT = new Context(LocaleContextHolder.getLocale());

    private final TripRepository tripRepository;
    private final EmailService emailService;
    private final ParticipantServices participantService;
    private final ParticipantRepository participantRepository;
    private final TemplateEngine templateEngine;
    private final WebClient.Builder webClientBuilder;
    private final ActivityRepository activityRepository;

    @Autowired
    public TripController(TripRepository tripRepository,
                          EmailService emailService,
                          ParticipantServices participantService,
                          ParticipantRepository participantRepository,
                          TemplateEngine templateEngine,
                          WebClient.Builder webClientBuilder,
                          ActivityRepository activityRepository) {
        this.tripRepository = tripRepository;
        this.emailService = emailService;
        this.participantService = participantService;
        this.participantRepository = participantRepository;
        this.templateEngine = templateEngine;
        this.webClientBuilder = webClientBuilder;
        this.activityRepository = activityRepository;
    }

    @PostMapping
    public ResponseEntity<Object> createTrip(@RequestBody TripPayloadModel request) {
        try {
            TripMigrationModel newTrip = new TripMigrationModel(request);

            LocalDateTime startsDate = LocalDateTime.parse(request.starts_at(), DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime endsDate = LocalDateTime.parse(request.ends_at(), DateTimeFormatter.ISO_DATE_TIME);

            if (startsDate.isAfter(endsDate)) {
                return ResponseEntity.badRequest().body("Error: Starts date after ends date!");
            } else if (endsDate.isBefore(startsDate)) {
                return ResponseEntity.badRequest().body("Error: Ends date before starts date!");
            }

            this.tripRepository.save(newTrip);

            this.participantService.registerParticipantsToEvent(request.emails_to_invite(), newTrip);

            return ResponseEntity.status(HttpStatus.OK).body(newTrip.getId());
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripDataModel> getTrip(@PathVariable UUID id) {
        try {
            Optional<TripMigrationModel> tripOptional = this.tripRepository.findById(id);

            if (tripOptional.isPresent()) {
                TripMigrationModel trip = tripOptional.get();

                TripDataModel data = new TripDataModel(trip);

                return ResponseEntity.status(HttpStatus.OK).body(data);
            }

            return ResponseEntity.notFound().build();
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTrip(@PathVariable UUID id, @RequestBody TripPayloadModel request) {
        try {
            Optional<TripMigrationModel> tripOptional = this.tripRepository.findById(id);

            LocalDateTime startsDate = LocalDateTime.parse(request.starts_at(), DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime endsDate = LocalDateTime.parse(request.ends_at(), DateTimeFormatter.ISO_DATE_TIME);

            if (startsDate.isAfter(endsDate)) {
                return ResponseEntity.badRequest().body("Error: Starts date after ends date!");
            } else if (endsDate.isBefore(startsDate)) {
                return ResponseEntity.badRequest().body("Error: Ends date before starts date!");
            }

            if(tripOptional.isPresent()) {
                TripMigrationModel trip = tripOptional.get();

                if (request.destination() != null && !request.destination().isEmpty()) {
                    trip.setDestination(request.destination());
                }

                if (!request.starts_at().isEmpty()) {
                    trip.setStartsAt(LocalDateTime.parse(request.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
                }

                if (!request.ends_at().isEmpty()) {
                    trip.setEndsAt(LocalDateTime.parse(request.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
                }

                this.tripRepository.save(trip);

                return ResponseEntity.ok("Success!");
            }

            return ResponseEntity.notFound().build();
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Object> confirmTrip(@PathVariable UUID id,
                                              @RequestParam Boolean confirmer,
                                              @RequestParam Boolean participantReq,
                                              @Nullable @RequestBody ParticipantDataEmailModel request) {
        try {
            Optional<TripMigrationModel> tripOptional = this.tripRepository.findById(id);

            if (tripOptional.isPresent()) {
                TripMigrationModel trip = tripOptional.get();

                if (confirmer) {
                    trip.setConfirmationToken(null);
                    trip.setIsConfirmed(true);
                    this.tripRepository.save(trip);

                    if (participantReq) {
                        return handleParticipantConfirmation(trip, request, id);
                    }

                    sendOwnerConfirmationEmail(trip);

                    return ResponseEntity.ok("Trip confirmed!");
                } else {
                    sendOwnerConfirmationEmail(trip);

                    return ResponseEntity.ok("To confirm...");
                }
            }

            return ResponseEntity.notFound().build();
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    private void sendOwnerConfirmationEmail(TripMigrationModel trip) throws MessagingException, UnsupportedEncodingException {
        EmailModel ownerEmail = new EmailModel(trip.getOwnerName(), trip.getOwnerEmail());
        String subject = trip.getIsConfirmed() ? "Sua viagem foi confirmada! Plann.er" : "Confirme sua viagem! Plann.er";
        emailService.sendEmail(ownerEmail, trip, trip.getId(), subject, trip.getIsConfirmed() ? "confirmed-email" : "confirm-email");
    }

    private ResponseEntity<Object> handleParticipantConfirmation(TripMigrationModel trip, ParticipantDataEmailModel request, UUID id) throws MessagingException, UnsupportedEncodingException {
        List<ParticipantModel> participants = trip.getParticipants();

        if (request != null) {
            Optional<ParticipantModel> existingParticipantOptional = participants.stream().filter(participant -> request.getEmail().equals(participant.getEmail())).findFirst();

            if (existingParticipantOptional.isPresent()) {
                ParticipantModel existingParticipant = existingParticipantOptional.get();

                existingParticipant.setName(request.getName());
                existingParticipant.setIsConfirmed(true);
                this.participantRepository.save(existingParticipant);

                emailService.sendEmail(new EmailModel(existingParticipant.getName(), existingParticipant.getEmail()), trip, id, "Sua presença foi confirmada! Plann.er", "confirmed-participant-email");

                return ResponseEntity.ok("Participant confirmed!");
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/confirmed-page/{id}")
    public Mono<String> confirmedPage(@PathVariable UUID id) {
        try {
            String url = "http://localhost:8080/trips/" + id + "/confirm?confirmer=true&participantReq=false";
            Mono<String> result = webClientBuilder.build()
                    .method(HttpMethod.POST)
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class);

            CONTEXT.setVariable("checkImage", "templates/images/check.png");
            CONTEXT.setVariable("bgImage", "templates/images/bg.png");

            return result.map(res -> this.templateEngine.process("confirmed-page", CONTEXT));
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantDataModel>> getParticipants(@PathVariable UUID id) {
        try {
            Optional<TripMigrationModel> tripOptional = this.tripRepository.findById(id);

            if (tripOptional.isPresent()) {
                TripMigrationModel trip = tripOptional.get();

                TripDataModel data = new TripDataModel(trip);

                return ResponseEntity.status(HttpStatus.OK).body(data.getParticipants());
            }

            return ResponseEntity.notFound().build();
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<Object> createActivity(@PathVariable UUID id, @RequestBody ActivityPayloadModel request) {
        try {
            Optional<TripMigrationModel> tripOptional = this.tripRepository.findById(id);

            if (tripOptional.isPresent()) {
                TripMigrationModel trip = tripOptional.get();

                LocalDate activityDate = LocalDate.parse(request.activity_date(), DateTimeFormatter.ISO_DATE);

                if (activityDate.isBefore(trip.getStartsAt().toLocalDate())) {
                    return ResponseEntity.badRequest().body("Error: Activity date before trip date!");
                } else if (activityDate.isAfter(trip.getEndsAt().toLocalDate())) {
                    return ResponseEntity.badRequest().body("Error: Activity date after trip date!");
                }

                if (request.name() != null && !request.name().isEmpty()) {
                    ActivityModel newActivity = new ActivityModel(request, trip);
                    newActivity.setActivityName(request.name());

                    this.activityRepository.save(newActivity);

                    return ResponseEntity.ok("Activity created!");
                }

                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.notFound().build();
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityDataModel>> getActivities(@PathVariable UUID id) {
        try {
            Optional<TripMigrationModel> tripOptional = this.tripRepository.findById(id);

            if (tripOptional.isPresent()) {
                TripMigrationModel trip = tripOptional.get();

                TripDataModel data = new TripDataModel(trip);

                return ResponseEntity.ok(data.getActivities());
            }

            return ResponseEntity.notFound().build();
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }
}
