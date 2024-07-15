package net.azurewebsites.planner.core.Models;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ParticipantDataModel {

    private UUID id;
    private Boolean isConfirmedParticipant;
    private String participantName;
    private String participantEmail;

    public ParticipantDataModel(ParticipantModel participantData) {
        this.id = participantData.getId();
        this.isConfirmedParticipant = participantData.getIsConfirmed();
        this.participantName = participantData.getName();
        this.participantEmail = participantData.getEmail();
    }
}
