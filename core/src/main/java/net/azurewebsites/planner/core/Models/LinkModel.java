package net.azurewebsites.planner.core.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity(name = "links")
@NoArgsConstructor
@AllArgsConstructor
public class LinkModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "title_link")
    private String titleLink;

    @NotNull
    @Column(name = "url")
    private String url;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "trip_id")
    private TripMigrationModel trip;

    public LinkModel(LinkPayloadModel linkData, TripMigrationModel trip) {
        this.titleLink = linkData.title_link();
        this.url = linkData.url();
        this.trip = trip;
    }
}
