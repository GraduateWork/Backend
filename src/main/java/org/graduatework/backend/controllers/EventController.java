package org.graduatework.backend.controllers;

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
    public List<EventDto> getEvents(HttpServletResponse response,
                                    @RequestParam(value = "count", required = false) Integer count) {
        String username = null;
        try {
            username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Throwable e) {
        }
        List<EventDto> events = eventService.getEvents(username == null || username.equals("anonymousUser") ? null : username, count, true);
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

    @RequestMapping(value = "event", method = RequestMethod.GET)
    public EventDto getEvent(HttpServletResponse response,
                                    @RequestParam("eventId") int eventId) {
        String username = null;
        try {
            username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Throwable e) {
        }
        EventDto event = eventService.getEvent(username == null || username.equals("anonymousUser") ? null : username, eventId);
        if (event == null) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot get event");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return event;
    }

    @RequestMapping(value = "favorites", method = RequestMethod.GET)
    public List<EventDto> getFavorites(HttpServletResponse response) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<EventDto> events = eventService.getFavoritesByUser(username);
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

    @RequestMapping(value = "favorites", method = RequestMethod.PUT)
    public void updateFavorites(HttpServletResponse response,
                                @RequestParam("eventId") int eventId) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!eventService.updateFavoriteForUser(username, eventId)) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot update favorites status.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "view", method = RequestMethod.POST)
    public void setViewed(HttpServletResponse response,
                          @RequestParam("eventId") int eventId) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!eventService.setViewed(username, eventId)) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot set this event viewed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public List<EventDto> getSearched(HttpServletResponse response,
                                      @RequestParam("request") String requestString) {
        String username = null;
        try {
            username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Throwable e) {
        }
        List<EventDto> events = eventService.getSearched(username == null || username.equals("anonymousUser") ? null : username, requestString);
        if (events == null) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot get searched events");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return events;
    }

    @RequestMapping(value = "top", method = RequestMethod.GET)
    public List<EventDto> getTopEvents(HttpServletResponse response,
                                       @RequestParam(value = "count", required = false) Integer count) {
        List<EventDto> events = eventService.getTopEvents(count);
        if (events == null) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot get top events");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return events;
    }
}
