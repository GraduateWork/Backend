package org.graduatework.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {
    private String title;
    private Long startTime;
    private Long endTime;
    private String imgSrc;
    private String description;
    private Map<String, String> details = new HashMap<>();

    public static final Set<String> jsonFieldNames = new HashSet<>(Arrays.asList("title", "startTime", "endTime", "imgSrc", "description"));

    public Event() {
        title = "";
        startTime = 0L;
        endTime = 0L;
        imgSrc = "";
        description = "";
    }

    public Event(String title, Long startTime, Long endTime, String imgSrc) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.imgSrc = imgSrc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
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

    @JsonIgnore
    public Map<String, String> getDetails() {
        return details;
    }
}
