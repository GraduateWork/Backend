package org.graduatework.backend.services;

import org.graduatework.backend.config.Configuration;
import org.graduatework.backend.db.Event;
import org.graduatework.backend.dto.EventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventService extends BaseService {

    @Autowired
    public EventService(Configuration config) {
        super(config);
    }

    public List<EventDto> getEvents(String username) {
        List<Event> dbEvents = dbAdaptor.getEvents();
        List<EventDto> events = new ArrayList<>();
        for (int i = 0; i < dbEvents.size(); i++) {
            Event dbEvent = dbEvents.get(i);
            EventDto event = new EventDto(dbEvent.getEventId(), dbEvent.getTitle(), dbEvent.getStartTime(), dbEvent.getEndTime(),
                    dbEvent.getImgSrc(), dbEvent.getDescription(), dbEvent.getType(), false, dbEvent.getDetails());
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
        }
        return events;
    }

    public List<EventDto> getFavoritesByUser(String username) {
        List<Event> dbEvents = dbAdaptor.getFavoritesByUser(username);
        List<EventDto> events = new ArrayList<>();
        for (int i = 0; i < dbEvents.size(); i++) {
            Event dbEvent = dbEvents.get(i);
            EventDto event = new EventDto(dbEvent.getEventId(), dbEvent.getTitle(), dbEvent.getStartTime(), dbEvent.getEndTime(),
                    dbEvent.getImgSrc(), dbEvent.getDescription(), dbEvent.getType(), true, dbEvent.getDetails());
            events.add(event);
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
