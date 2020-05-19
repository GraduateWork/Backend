package org.graduatework.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDto {
    private Integer eventId = null;
    private String title;
    private String startTime;
    private String endTime;
    private String imgSrc;
    private String description;
    private String type;
    private String source;
    private boolean isFavorite;
    private double mark;
    private Map<String, String> details = new HashMap<>();

    public static final Set<String> jsonFieldNames = new HashSet<>(Arrays.asList("title", "startTime", "endTime", "imgSrc", "description", "type", "source"));

    public EventDto() {
        eventId = null;
        title = "";
        startTime = "";
        endTime = "";
        imgSrc = "";
        description = "";
        type = "";
        source = "";
        isFavorite = false;
    }

    public EventDto(int eventId, String title, String startTime, String endTime, String imgSrc, String description, String type, String source, boolean isFavorite, Map<String, String> details) {
        this.eventId = eventId;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.imgSrc = imgSrc;
        this.description = description;
        this.type = type;
        this.source = source;
        this.isFavorite = isFavorite;
        this.details = details;
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

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @JsonIgnore
    public double getMark() {
        return mark;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }
}
