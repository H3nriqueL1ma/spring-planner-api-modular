package net.azurewebsites.planner.core.Repositories;

import net.azurewebsites.planner.core.Models.LinkModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LinkRepository extends JpaRepository<LinkModel, UUID> {
}
