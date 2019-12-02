package org.graduatework.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class Event {

    private String name;
    private Long startTime;
    private Long endTime;
    private String picUrl;
    private List<String> tags = new ArrayList<>();

    public Event() {
    }

    public Event(String name, Long startTime, Long endTime, String picUrl) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.picUrl = picUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
