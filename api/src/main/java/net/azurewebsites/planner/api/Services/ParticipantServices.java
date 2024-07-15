package net.azurewebsites.planner.api.Services;

import net.azurewebsites.planner.core.Models.ParticipantModel;
import net.azurewebsites.planner.core.Models.TripMigrationModel;
import net.azurewebsites.planner.core.Repositories.ParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipantServices {

    private final ParticipantRepository participantRepository;

    public ParticipantServices(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public void registerParticipantsToEvent(List<String> participantsToInvite, TripMigrationModel trip) {
        List<ParticipantModel> participants = participantsToInvite.stream().map(email -> new ParticipantModel(email, trip)).toList();

        this.participantRepository.saveAll(participants);
    }
}
