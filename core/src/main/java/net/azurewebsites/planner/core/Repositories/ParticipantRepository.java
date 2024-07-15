package net.azurewebsites.planner.core.Repositories;

import net.azurewebsites.planner.core.Models.ParticipantModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<ParticipantModel, UUID> {
    ParticipantModel findByEmail(String email);
}
