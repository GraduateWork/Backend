package org.graduatework.backend.db;

public class UserEvent {
    private int userId;
    private int eventId;
    private boolean isFavorite;
    private double mark;

    public UserEvent() {
    }

    public UserEvent(int userId, int eventId, boolean isFavorite, double mark) {
        this.userId = userId;
        this.eventId = eventId;
        this.isFavorite = isFavorite;
        this.mark = mark;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public double getMark() {
        return mark;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }
}
