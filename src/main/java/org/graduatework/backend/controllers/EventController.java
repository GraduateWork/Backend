package org.graduatework.backend.controllers;

import org.graduatework.backend.dto.Event;
import org.graduatework.backend.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class EventController {

    private EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @RequestMapping(value = "events", method = RequestMethod.GET)
    public List<Event> getEvents(HttpServletResponse response) {
        List<Event> events = eventService.getEvents();
        if (events == null) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot get events");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return events;
    }
}
