package net.azurewebsites.planner.core.Repositories;

import net.azurewebsites.planner.core.Models.TripMigrationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<TripMigrationModel, UUID> {
}
