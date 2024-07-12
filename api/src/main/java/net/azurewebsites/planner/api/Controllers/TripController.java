package net.azurewebsites.planner.api.Controllers;

import net.azurewebsites.planner.core.Models.TripMigrationModel;
import net.azurewebsites.planner.core.Models.TripPayloadModel;
import net.azurewebsites.planner.core.Repositories.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final TripRepository tripRepository;

    @Autowired
    public TripController(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @PostMapping
    public ResponseEntity<String> createTrip(@RequestBody TripPayloadModel request) {
        try {
            TripMigrationModel newTrip = new TripMigrationModel(request);

            this.tripRepository.save(newTrip);

            return ResponseEntity.ok("Success!");
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }
}
