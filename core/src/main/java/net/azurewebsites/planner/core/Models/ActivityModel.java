package net.azurewebsites.planner.core.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Entity(name = "activities")
@AllArgsConstructor
@NoArgsConstructor
public class ActivityModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "activity_name")
    private String activityName;

    @NotNull
    @Column(name = "is_completed")
    private Boolean isCompleted;

    @NotNull
    @Column(name = "activity_date")
    private LocalDate activityDate;

    @NotNull
    @Column(name = "activity_hour")
    private LocalTime activityHour;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "trip_id")
    private TripMigrationModel trip;

    public ActivityModel(ActivityPayloadModel data, TripMigrationModel trip) {
        this.activityDate = LocalDate.parse(data.activity_date(), DateTimeFormatter.ISO_DATE);
        this.activityHour = LocalTime.parse(data.activity_hour(), DateTimeFormatter.ISO_TIME);
        this.isCompleted = false;
        this.trip = trip;
        this.activityName = "";
    }
}
