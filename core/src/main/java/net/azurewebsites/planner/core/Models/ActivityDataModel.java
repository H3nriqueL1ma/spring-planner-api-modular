package net.azurewebsites.planner.core.Models;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class ActivityDataModel {

    private UUID id;
    private Boolean isCompleted;
    private String activityName;
    private LocalDate activityDate;
    private LocalTime activityHour;

    public ActivityDataModel(ActivityModel activityData) {
        this.id = activityData.getId();
        this.isCompleted = activityData.getIsCompleted();
        this.activityDate = activityData.getActivityDate();
        this.activityHour = activityData.getActivityHour();
        this.activityName = activityData.getActivityName();
    }
}
