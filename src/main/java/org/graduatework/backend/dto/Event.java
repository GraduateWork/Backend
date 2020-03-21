package org.graduatework.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {
    private int eventId;
    private String title;
    private String startTime;
    private String endTime;
    private String imgSrc;
    private String description;
    private String type;
    private Map<String, String> details = new HashMap<>();

    public static final Set<String> jsonFieldNames = new HashSet<>(Arrays.asList("title", "startTime", "endTime", "imgSrc", "description", "type"));

    public Event() {
        eventId = 0;
        title = "";
        startTime = "";
        endTime = "";
        imgSrc = "";
        description = "";
        type = "";
    }

    public Event(String title, String startTime, String endTime, String imgSrc, String description, String type) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.imgSrc = imgSrc;
        this.description = description;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}
