package net.azurewebsites.planner.api.Controllers;

import net.azurewebsites.planner.core.Models.TripMigrationModel;
import net.azurewebsites.planner.core.Models.TripPayloadModel;
import net.azurewebsites.planner.core.Repositories.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final TripRepository tripRepository;

    @Autowired
    public TripController(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

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
    public ResponseEntity<String> confirmTrip(@PathVariable UUID id, @RequestBody TripPayloadModel request) {
        try {

        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }
}
