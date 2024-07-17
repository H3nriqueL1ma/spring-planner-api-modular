package net.azurewebsites.planner.core.Models;

import lombok.Data;

import java.util.UUID;

@Data
public class LinkDataModel {

    private UUID id;
    private String titleLink;
    private String url;

    public LinkDataModel(LinkModel linkData) {
        this.id = linkData.getId();
        this.titleLink = linkData.getTitleLink();
        this.url = linkData.getUrl();
    }
}
