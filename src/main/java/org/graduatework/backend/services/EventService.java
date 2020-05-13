package org.graduatework.backend.services;

import org.graduatework.backend.config.Configuration;
import org.graduatework.backend.db.DBAdaptorInfo;
import org.graduatework.backend.db.DBUser;
import org.graduatework.backend.db.Event;
import org.graduatework.backend.db.UserEvent;
import org.graduatework.backend.dto.EventDto;
import org.graduatework.backend.recommendation.RecommendationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventService extends BaseService {
    private final String recManagerPath = "org.graduatework.backend.recommendation.";

    private RecommendationManager recommendationManager = null;

    @Autowired
    public EventService(Configuration config) {
        super(config);
        String recManagerClassName = config.getRecommendationManager();
        try {
            recommendationManager = (RecommendationManager) Class.forName(recManagerPath + recManagerClassName)
                    .getConstructor(DBAdaptorInfo.class).newInstance(dbAdaptor);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public EventService(Configuration config, DBAdaptorInfo dbAdaptor) {
        super(config, dbAdaptor);
        String recManagerClassName = config.getRecommendationManager();
        try {
            recommendationManager = (RecommendationManager) Class.forName(recManagerPath + recManagerClassName)
                    .getConstructor(DBAdaptorInfo.class).newInstance(dbAdaptor);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public List<EventDto> getEvents(String username, Integer count) {
        List<Event> dbEvents = dbAdaptor.getEvents();
        List<EventDto> events = new ArrayList<>();
        for (int i = 0; i < dbEvents.size(); i++) {
            Event dbEvent = dbEvents.get(i);
            EventDto event = new EventDto(dbEvent.getEventId(), dbEvent.getTitle(), dbEvent.getStartTime(), dbEvent.getEndTime(),
                    dbEvent.getImgSrc(), dbEvent.getDescription(), dbEvent.getType(), dbEvent.getSource(), false, dbEvent.getDetails());
            events.add(event);
        }
        if (username != null) {
            List<Event> eventsByUser = dbAdaptor.getFavoritesByUser(username);
            Map<Integer, EventDto> eventMap = new HashMap<>();
            for (int i = 0; i < events.size(); i++) {
                eventMap.put(events.get(i).getEventId(), events.get(i));
            }
            for (int i = 0; i < eventsByUser.size(); i++) {
                EventDto event = eventMap.get(eventsByUser.get(i).getEventId());
                if (event != null) {
                    event.setFavorite(true);
                }
            }
            if (recommendationManager != null) {
                events = recommendationManager.sortByPreference(events, username);
            }
        }
        List<EventDto> filteredEvents = events.stream().filter(e -> !e.isFavorite()).collect(Collectors.toList());
        if (count != null) {
            filteredEvents = filteredEvents.subList(0, Math.min(count, filteredEvents.size()));
        }
        return filteredEvents;
    }

    public EventDto getEvent(String username, int eventId) {
        Event dbEvent = dbAdaptor.getEvent(eventId);
        EventDto event = new EventDto(dbEvent.getEventId(), dbEvent.getTitle(), dbEvent.getStartTime(), dbEvent.getEndTime(),
                dbEvent.getImgSrc(), dbEvent.getDescription(), dbEvent.getType(), dbEvent.getSource(), false, dbEvent.getDetails());
        if (username != null) {
            List<Event> eventsByUser = dbAdaptor.getFavoritesByUser(username);
            for (int i = 0; i < eventsByUser.size(); i++) {
                if (eventsByUser.get(i).getEventId() == eventId) {
                    event.setFavorite(true);
                    break;
                }
            }
        }
        return event;
    }

    public List<EventDto> getFavoritesByUser(String username) {
        List<Event> dbEvents = dbAdaptor.getFavoritesByUser(username);
        List<EventDto> events = new ArrayList<>();
        for (int i = 0; i < dbEvents.size(); i++) {
            Event dbEvent = dbEvents.get(i);
            EventDto event = new EventDto(dbEvent.getEventId(), dbEvent.getTitle(), dbEvent.getStartTime(), dbEvent.getEndTime(),
                    dbEvent.getImgSrc(), dbEvent.getDescription(), dbEvent.getType(), dbEvent.getSource(), true, dbEvent.getDetails());
            events.add(event);
        }
        return events;
    }

    public List<EventDto> getSearched(String username, String requestString) {
        requestString = requestString.toLowerCase();
        List<Event> dbEvents = dbAdaptor.getEvents();
        List<EventDto> events = new ArrayList<>();
        for (int i = 0; i < dbEvents.size(); i++) {
            Event dbEvent = dbEvents.get(i);
            String title = dbEvent.getTitle().toLowerCase();
            String description = dbEvent.getDescription().toLowerCase();
            if (title.contains(requestString) || description.contains(requestString)) {
                EventDto event = new EventDto(dbEvent.getEventId(), dbEvent.getTitle(), dbEvent.getStartTime(), dbEvent.getEndTime(),
                        dbEvent.getImgSrc(), dbEvent.getDescription(), dbEvent.getType(), dbEvent.getSource(), false, dbEvent.getDetails());
                events.add(event);
            }
        }
        if (username != null) {
            List<Event> eventsByUser = dbAdaptor.getFavoritesByUser(username);
            Map<Integer, EventDto> eventMap = new HashMap<>();
            for (int i = 0; i < events.size(); i++) {
                eventMap.put(events.get(i).getEventId(), events.get(i));
            }
            for (int i = 0; i < eventsByUser.size(); i++) {
                EventDto event = eventMap.get(eventsByUser.get(i).getEventId());
                if (event != null) {
                    event.setFavorite(true);
                }
            }
        }
        // TODO: add sort by preference.
        return events;
    }

    public List<EventDto> getTopEvents(Integer count) {
        List<Event> dbEvents = dbAdaptor.getEvents();
        List<EventDto> events = new ArrayList<>();
        for (int i = 0; i < dbEvents.size(); i++) {
            Event dbEvent = dbEvents.get(i);
            EventDto event = new EventDto(dbEvent.getEventId(), dbEvent.getTitle(), dbEvent.getStartTime(), dbEvent.getEndTime(),
                    dbEvent.getImgSrc(), dbEvent.getDescription(), dbEvent.getType(), dbEvent.getSource(), false, dbEvent.getDetails());
            events.add(event);
        }

        List<DBUser> users = dbAdaptor.getUsers();
        Map<Integer, Integer> eventIndexes = new HashMap<>();
        for (int i = 0; i < events.size(); i++) {
            eventIndexes.put(events.get(i).getEventId(), i);
        }
        double[][] marks = new double[users.size()][events.size()];
        boolean[][] isMarkPresent = new boolean[users.size()][events.size()];
        for (int i = 0; i < users.size(); i++) {
            DBUser user = users.get(i);
            List<UserEvent> userEvents = dbAdaptor.getUserEvents(user.getUsername());
            double avgMark = userEvents.stream().mapToDouble(UserEvent::getMark).sum();
            avgMark /= userEvents.size();
            for (int j = 0; j < userEvents.size(); j++) {
                int eventIndex = eventIndexes.get(userEvents.get(j).getEventId());
                marks[i][eventIndex] = userEvents.get(j).getMark() - avgMark;
                isMarkPresent[i][eventIndex] = true;
            }
        }
        double[] avgMarks = new double[events.size()];
        for (int i = 0; i < avgMarks.length; i++) {
            int marksCount = 0;
            for (int j = 0; j < users.size(); j++) {
                if (isMarkPresent[j][i]) {
                    avgMarks[i] += marks[j][i];
                    marksCount++;
                }
            }
            if (marksCount != 0) {
                avgMarks[i] /= marksCount;
            }
        }
        events.sort((a, b) -> {
            double markA = avgMarks[eventIndexes.get(a.getEventId())];
            double markB = avgMarks[eventIndexes.get(b.getEventId())];
            return Double.compare(markB, markA);
        });
        if (count != null) {
            events = events.subList(0, Math.min(count, events.size()));
        }
        return events;
    }

    public boolean updateFavoriteForUser(String username, int eventId) {
        return dbAdaptor.updateEventForUser(username, eventId, false, true);
    }

    public boolean setViewed(String username, int eventId) {
        return dbAdaptor.updateEventForUser(username, eventId, true, false);
    }
}
