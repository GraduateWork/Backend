package org.graduatework.backend.controllers;

import org.graduatework.backend.db.DBUser;
import org.graduatework.backend.db.Event;
import org.graduatework.backend.dto.EventDto;
import org.graduatework.backend.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
    public List<EventDto> getEvents(HttpServletResponse response) {
        DBUser user = null;
        try {
            user = (DBUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Throwable e) {
        }
        List<EventDto> events = eventService.getEvents(user == null ? null : user.getUsername());
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

    @RequestMapping(value = "favorites", method = RequestMethod.GET)
    public List<EventDto> getFavorites(HttpServletResponse response) {
        DBUser user = (DBUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<EventDto> events = eventService.getEventsByUser(user.getUsername());
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

    @RequestMapping(value = "favorites", method = RequestMethod.POST)
    public void addFavorites(HttpServletResponse response,
                                @RequestParam("eventId") int eventId) {
        DBUser user = (DBUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!eventService.addEventForUser(user.getUsername(), eventId)) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot add favorites.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "favorites", method = RequestMethod.DELETE)
    public void removeFavorites(HttpServletResponse response,
                                @RequestParam("eventId") int eventId) {
        DBUser user = (DBUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (eventService.removeEventForUser(user.getUsername(), eventId)) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot remove favorites.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
