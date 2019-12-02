package org.graduatework.backend.services;

import org.graduatework.backend.config.Configuration;
import org.graduatework.backend.dto.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService extends BaseService {

    @Autowired
    public EventService(Configuration config) {
        super(config);
    }

    public void addEvent(Event event) {

    }

    public List<Event> getEvents() {
        return dbAdaptor.getEvents();
    }
}