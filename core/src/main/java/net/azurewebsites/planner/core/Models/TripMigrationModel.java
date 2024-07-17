package net.azurewebsites.planner.core.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Data
@Entity(name = "trips")
@NoArgsConstructor
@AllArgsConstructor
public class TripMigrationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private String destination;

    @NotNull
    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    @NotNull
    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @ElementCollection
    @CollectionTable(name = "trip_emails_to_invite", joinColumns = @JoinColumn(name = "trip_id"))
    @Column(name = "emails_to_invite")
    private List<String> emailsToInvite;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "owner_email")
    private String ownerEmail;

    @NotNull
    @Column(name = "is_confirmed")
    private Boolean isConfirmed;

    @Column(name = "confirmation_token", unique = true)
    private UUID confirmationToken;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipantModel> participants;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityModel> activities;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LinkModel> links;

    public TripMigrationModel(TripPayloadModel data) {
        this.destination = data.destination();
        this.startsAt = LocalDateTime.parse(data.starts_at(), DateTimeFormatter.ISO_DATE_TIME);
        this.endsAt = LocalDateTime.parse(data.ends_at(), DateTimeFormatter.ISO_DATE_TIME);
        this.emailsToInvite = data.emails_to_invite();
        this.ownerName = data.owner_name();
        this.ownerEmail = data.owner_email();
        this.isConfirmed = false;
        this.confirmationToken = UUID.randomUUID();
    }
}
