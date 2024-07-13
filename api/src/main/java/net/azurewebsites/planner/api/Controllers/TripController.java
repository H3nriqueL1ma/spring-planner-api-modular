package net.azurewebsites.planner.api.Controllers;

import net.azurewebsites.planner.api.Services.EmailService;
import net.azurewebsites.planner.core.Models.EmailModel;
import net.azurewebsites.planner.core.Models.TripMigrationModel;
import net.azurewebsites.planner.core.Models.TripPayloadModel;
import net.azurewebsites.planner.core.Repositories.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private EmailService emailService;

    @PostMapping
    public ResponseEntity<UUID> createTrip(@RequestBody TripPayloadModel request) {
        try {
            TripMigrationModel newTrip = new TripMigrationModel(request);

            this.tripRepository.save(newTrip);

            return ResponseEntity.status(HttpStatus.OK).body(newTrip.getId());
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripMigrationModel> getTrip(@PathVariable UUID id) {
        try {
            Optional<TripMigrationModel> tripOptional = this.tripRepository.findById(id);

            if (tripOptional.isPresent()) {
                TripMigrationModel trip = tripOptional.get();

                return ResponseEntity.status(HttpStatus.OK).body(trip);
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

            if(tripOptional.isPresent()) {
                TripMigrationModel trip = tripOptional.get();

                if (request.destination() != null && !request.destination().isEmpty()) {
                    trip.setDestination(request.destination());
                }

                if (request.starts_at() != null && !request.starts_at().isEmpty()) {
                    trip.setStartsAt(LocalDateTime.parse(request.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
                }

                if (request.ends_at() != null && !request.ends_at().isEmpty()) {
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

    @GetMapping("/{id}/confirm")
    public ResponseEntity<String> confirmTrip(@PathVariable UUID id, @RequestParam Boolean confirmer) {
        try {
            String TEMPLATE_NAME_CONFIRM = "confirm-email";
            String TEMPLATE_NAME_CONFIRMED = "confirmed-email";
            String MAIL_SUBJECT_CONFIRM = "Confirme sua viagem! Plann.er";
            String MAIL_SUBJECT_CONFIRMED = "Sua viagem foi confirmada! Plann.er";
            
            Optional<TripMigrationModel> tripOptional = this.tripRepository.findById(id);
            
            if (tripOptional.isPresent()) {
                TripMigrationModel trip = tripOptional.get();

                EmailModel newEmail = new EmailModel(trip.getOwnerName(), trip.getOwnerEmail());
                Context context = new Context(LocaleContextHolder.getLocale());
                
                if (confirmer) {
                    trip.setConfirmationToken(null);
                    trip.setIsConfirmed(true);
                    
                    this.tripRepository.save(trip);

                    emailService.sendEmail(newEmail, trip, id, MAIL_SUBJECT_CONFIRMED, TEMPLATE_NAME_CONFIRMED);

                    String htmlContent = templateEngine.process("confirmed-page", context);

                    return ResponseEntity.ok(htmlContent);
                } else {
                    emailService.sendEmail(newEmail, trip, id, MAIL_SUBJECT_CONFIRM, TEMPLATE_NAME_CONFIRM);
                    return ResponseEntity.ok("To Confirm...");
                }
            }

            return ResponseEntity.notFound().build();
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }
}
