package net.azurewebsites.planner.core.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity(name = "participants")
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "is_confirmed")
    private Boolean isConfirmed;

    @NotNull
    private String name;

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "trip_id")
    private TripMigrationModel trip;

    public ParticipantModel(String email, TripMigrationModel trip) {
        this.email = email;
        this.trip = trip;
        this.isConfirmed = false;
        this.name = "";
    }
}
