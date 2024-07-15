package net.azurewebsites.planner.core.Models;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class TripDataModel {
    private UUID tripId;
    private String ownerName;
    private String ownerEmail;
    private Boolean isConfirmedTrip;
    private String destination;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private List<ParticipantDataModel> participants;
    private List<ActivityDataModel> activities;

    public TripDataModel(TripMigrationModel dataTrip) {
        this.tripId = dataTrip.getId();
        this.ownerName = dataTrip.getOwnerName();
        this.ownerEmail = dataTrip.getOwnerEmail();
        this.isConfirmedTrip = dataTrip.getIsConfirmed();
        this.destination = dataTrip.getDestination();
        this.startsAt = dataTrip.getStartsAt();
        this.endsAt = dataTrip.getEndsAt();
        this.participants = dataTrip.getParticipants().stream().map(ParticipantDataModel::new).collect(Collectors.toList());
        this.activities = dataTrip.getActivities().stream().map(ActivityDataModel::new).collect(Collectors.toList());
    }
}
