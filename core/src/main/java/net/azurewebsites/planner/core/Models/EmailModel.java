package net.azurewebsites.planner.core.Models;

import lombok.Getter;

@Getter
public class EmailModel {

    private final String tripCreatorName;
    private final String tripCreatorEmail;

    public EmailModel(String tripCreatorName, String tripCreatorEmail) {
        this.tripCreatorName = tripCreatorName;
        this.tripCreatorEmail = tripCreatorEmail;
    }
}
