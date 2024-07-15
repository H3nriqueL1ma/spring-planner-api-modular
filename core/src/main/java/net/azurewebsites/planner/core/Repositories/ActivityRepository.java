package net.azurewebsites.planner.core.Repositories;

import net.azurewebsites.planner.core.Models.ActivityModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityModel, UUID> {
}
