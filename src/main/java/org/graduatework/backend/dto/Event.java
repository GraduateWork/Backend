package org.graduatework.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class Event {

    private String name;
    private long startTime;
    private long endTime;
    private String picUrl;
    private List<String> tags = new ArrayList<>();

    public Event() {
    }

    public Event(String name, long startTime, long endTime, String picUrl) {
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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
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
