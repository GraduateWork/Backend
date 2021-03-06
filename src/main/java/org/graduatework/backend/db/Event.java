package org.graduatework.backend.db;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {
    private Integer eventId = null;
    private String title;
    private String startTime;
    private String endTime;
    private String imgSrc;
    private String description;
    private String type;
    private String source;
    private Map<String, String> details = new HashMap<>();

    public static final Set<String> jsonFieldNames = new HashSet<>(Arrays.asList("title", "startTime", "endTime", "imgSrc", "description", "type", "source"));

    public Event() {
        eventId = null;
        title = "";
        startTime = "";
        endTime = "";
        imgSrc = "";
        description = "";
        type = "";
        source = "";
    }

    public Event(String title, String startTime, String endTime, String imgSrc, String description, String type, String source) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.imgSrc = imgSrc;
        this.description = description;
        this.type = type;
        this.source = source;
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

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
