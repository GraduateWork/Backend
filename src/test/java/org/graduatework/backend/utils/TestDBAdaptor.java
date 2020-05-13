package org.graduatework.backend.utils;

import org.graduatework.backend.db.*;

import java.util.*;
import java.util.stream.Collectors;

public class TestDBAdaptor implements DBAdaptorInfo {

    private List<DBUser> users = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private List<UserEvent> userEvents = new ArrayList<>();

    private Map<String, Integer> userMapping = new HashMap<>();

    @Override
    public DBUser getUser(String username) {
        return null;
    }

    @Override
    public List<DBUser> getUsers() {
        return users;
    }

    @Override
    public boolean insertUser(DBUser user) throws IllegalArgumentException {
        return false;
    }

    @Override
    public boolean activateUser(String username) throws IllegalArgumentException {
        return false;
    }

    @Override
    public boolean clearEvents() {
        return false;
    }

    @Override
    public boolean deleteEvents(Collection<Event> events) {
        return false;
    }

    @Override
    public boolean insertEvents(List<Event> events) {
        return false;
    }

    @Override
    public List<Event> getEvents() {
        return events;
    }

    @Override
    public Event getEvent(int eventId) {
        for (Event event : events) {
            if (event.getEventId() == eventId) {
                return event;
            }
        }
        return null;
    }

    @Override
    public List<Event> getEventsByUser(String username) {
        return null;
    }

    @Override
    public List<Event> getFavoritesByUser(String username) {
        return Collections.emptyList();
    }

    @Override
    public boolean updateEventForUser(String username, int eventId, boolean setViewed, boolean updateFavorite) throws IllegalArgumentException {
        return false;
    }

    @Override
    public List<UserEvent> getUserEvents(String username) {
        Integer userId = userMapping.get(username);
        if (userId == null) {
            return Collections.emptyList();
        }
        return userEvents.stream().filter(e -> e.getUserId() == userId).collect(Collectors.toList());
    }

    public void setUsers(List<DBUser> users) {
        this.users = users;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public void setUserEvents(List<UserEvent> userEvents) {
        this.userEvents = userEvents;
    }

    public void setUserMapping(Map<String, Integer> userMapping) {
        this.userMapping = userMapping;
    }
}
