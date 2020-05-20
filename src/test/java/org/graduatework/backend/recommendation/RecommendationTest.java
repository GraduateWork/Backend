package org.graduatework.backend.recommendation;

import org.graduatework.backend.config.Configuration;
import org.graduatework.backend.db.DBAdaptorInfo;
import org.graduatework.backend.db.DBUser;
import org.graduatework.backend.db.Event;
import org.graduatework.backend.db.UserEvent;
import org.graduatework.backend.dto.EventDto;
import org.graduatework.backend.services.EventService;
import org.graduatework.backend.utils.TestDBAdaptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class RecommendationTest {

    private final String dataPath = "ml-latest-small\\";

    private Configuration config;
    private EventService eventService;

    private void setup(String recManager, DBAdaptorInfo dbAdaptor) {
        config = new Configuration();
        config.setDataSourceUrl("");
        config.setJdbcUrl("");
        config.setRecommendationManager(recManager);
        eventService = new EventService(config, dbAdaptor);
    }

    private void readEvents(Scanner moviesSc, Map<Integer, Event> eventMap) {
        while (moviesSc.hasNext()) {
            String line = moviesSc.nextLine();
            String[] elements = line.split(",");
            if (elements.length < 3)
                continue;
            if (elements.length > 3) {
                String[] newElements = new String[3];
                newElements[0] = elements[0];
                newElements[2] = elements[elements.length - 1];
                newElements[1] = "";
                for (int i = 1; i < elements.length - 1; i++) {
                    newElements[1] += elements[i];
                }
                elements = newElements;
            }
            int eventId;
            try {
                eventId = Integer.parseInt(elements[0]);
            } catch (Throwable e) {
                continue;
            }
            Event event = new Event(elements[1], null, null, null, elements[2], elements[2], "");
            event.setEventId(eventId);
            eventMap.put(eventId, event);
        }
    }

    private void readRatings(Scanner ratingsSc, List<UserEvent> userEvents, Map<Integer, DBUser> userMap) {
        while (ratingsSc.hasNext()) {
            String line = ratingsSc.nextLine();
            String[] elements = line.split(",");
            if (elements.length != 4)
                continue;
            int userId;
            int eventId;
            double mark;
            try {
                userId = Integer.parseInt(elements[0]);
                eventId = Integer.parseInt(elements[1]);
                mark = Double.parseDouble(elements[2]);
            } catch (Throwable e) {
                continue;
            }
            if (!userMap.containsKey(userId)) {
                DBUser user = new DBUser(userId, String.valueOf(userId), String.valueOf(userId), "1111", true, System.currentTimeMillis());
                userMap.put(userId, user);
            }
            UserEvent userEvent = new UserEvent(userId, eventId, false, mark);
            userEvents.add(userEvent);
        }
    }

    private double calculateNDCG(List<EventDto> events, List<UserEvent> userEvents) {
        List<EventDto> filteredEvents = new ArrayList<>();
        for (EventDto event : events) {
            for (UserEvent userEvent : userEvents) {
                if (userEvent.getEventId() == event.getEventId()) {
                    filteredEvents.add(event);
                    break;
                }
            }
        }
        List<UserEvent> copy = new ArrayList<>(userEvents);
        copy.sort((a, b) -> Double.compare(b.getMark(), a.getMark()));
        double dcg = 0;
        int i = 0;
        for (EventDto filteredEvent : filteredEvents) {
            i++;
            for (UserEvent userEvent : copy) {
                if (filteredEvent.getEventId() == userEvent.getEventId()) {
                    dcg += (Math.pow(2, userEvent.getMark()) - 1) / (Math.log(i + 1) / Math.log(2));
                    break;
                }
            }
        }
        double idcg = 0;
        i = 0;
        for (UserEvent userEvent : copy) {
            i++;
            idcg += (Math.pow(2, userEvent.getMark()) - 1) / (Math.log(i + 1) / Math.log(2));
        }
        return dcg / idcg;
    }

    @Test
    public void testUserBasedRecommendation() throws FileNotFoundException {
        Random rand = new Random(System.currentTimeMillis());
        TestDBAdaptor dbAdaptor = new TestDBAdaptor();
        Scanner moviesSc = new Scanner(new FileInputStream(new File(dataPath + "movies.csv")));
        Map<Integer, Event> eventMap = new HashMap<>();
        readEvents(moviesSc, eventMap);

        Map<Integer, DBUser> userMap = new HashMap<>();
        List<UserEvent> userEvents = new ArrayList<>();
        Scanner ratingsSc = new Scanner(new FileInputStream(new File(dataPath + "ratings.csv")));
        readRatings(ratingsSc, userEvents, userMap);

        dbAdaptor.setEvents(new ArrayList<>(eventMap.values()));
        dbAdaptor.setUsers(new ArrayList<>(userMap.values()));
        dbAdaptor.setUserEvents(userEvents);

        setup("UserBasedRecommendationManager", dbAdaptor);
        double avgNDCG = 0;
        for (DBUser user : userMap.values()) {
            List<UserEvent> eventsForUser = dbAdaptor.getUserEvents(user.getUsername());
            List<UserEvent> newEventsForUser = new ArrayList<>();
            for (UserEvent userEvent : eventsForUser) {
                if (rand.nextInt(2) == 0) {
                    newEventsForUser.add(userEvent);
                }
            }
            dbAdaptor.setUserEventsByUserId(user.getUserId(), newEventsForUser);
            List<EventDto> eventsDto = eventService.getEvents(user.getUsername(), null, true);
            dbAdaptor.setUserEventsByUserId(user.getUserId(), eventsForUser);
            double ndcg = calculateNDCG(eventsDto, dbAdaptor.getUserEvents(user.getUsername()));
            avgNDCG += ndcg;
            Assertions.assertNotNull(eventsDto);
        }
        avgNDCG /= userMap.size();
        System.out.println("Average NDCG = " + avgNDCG);
    }
}
