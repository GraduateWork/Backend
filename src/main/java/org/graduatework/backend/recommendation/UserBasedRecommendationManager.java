package org.graduatework.backend.recommendation;

import org.graduatework.backend.db.DBAdaptorInfo;
import org.graduatework.backend.db.DBUser;
import org.graduatework.backend.db.UserEvent;
import org.graduatework.backend.dto.EventDto;

import java.util.*;
import java.util.stream.IntStream;

public class UserBasedRecommendationManager implements RecommendationManager {

    private DBAdaptorInfo dbAdaptor;

    public UserBasedRecommendationManager(DBAdaptorInfo dbAdaptor) {
        this.dbAdaptor = dbAdaptor;
    }

    @Override
    public List<EventDto> sortByPreference(List<EventDto> events, String username, boolean savePrevMarks) {
        List<UserEvent> curUserEvents = dbAdaptor.getUserEvents(username);
        List<DBUser> users = dbAdaptor.getUsers();
        OptionalInt optional = IntStream.range(0, users.size())
                .filter(i -> username.equals(users.get(i).getUsername()))
                .findFirst();
        if (optional.isEmpty()) {
            return events;
        }
        int curUserIndex = optional.getAsInt();
        Map<Integer, Integer> eventIndexes = new HashMap<>();
        for (int i = 0; i < events.size(); i++) {
            eventIndexes.put(events.get(i).getEventId(), i);
        }
        double[][] marks = new double[users.size()][events.size()];
        boolean[][] isMarkPresent = new boolean[users.size()][events.size()];
        double[] avgMarks = new double[users.size()];
        for (int i = 0; i < users.size(); i++) {
            DBUser user = users.get(i);
            List<UserEvent> userEvents;
            if (user.getUsername().equals(username)) {
                userEvents = curUserEvents;
            } else {
                userEvents = dbAdaptor.getUserEvents(user.getUsername());
            }
            double avgMark = userEvents.stream().mapToDouble(UserEvent::getMark).sum();
            avgMark /= userEvents.size();
            avgMarks[i] = 0;
            for (int j = 0; j < userEvents.size(); j++) {
                int eventIndex = eventIndexes.get(userEvents.get(j).getEventId());
                marks[i][eventIndex] = userEvents.get(j).getMark() - avgMark;
                avgMarks[i] += marks[i][eventIndex];
                isMarkPresent[i][eventIndex] = true;
            }
            avgMarks[i] /= avgMark;
        }

        for (int j = 0; j < events.size(); j++) {
            double avgMark = 0;
            int count = 0;
            for (int i = 0; i < users.size(); i++) {
                if (isMarkPresent[i][j]) {
                    avgMark += marks[i][j];
                    count++;
                }
                if (count > 0) {
                    avgMark /= count;
                } else {
                    avgMark = 0;
                }
            }
            for (int i = 0; i < users.size(); i++) {
                if (!isMarkPresent[i][j]) {
                    marks[i][j] = avgMark;
                }
            }
        }
        double[] similarity = new double[users.size()];
        double curNorma = 0;
        for (int i = 0; i < events.size(); i++) {
            curNorma += marks[curUserIndex][i] * marks[curUserIndex][i];
        }
        curNorma = Math.sqrt(curNorma);
        for (int i = 0; i < users.size(); i++) {
            if (i == curUserIndex) {
                similarity[i] = 0;
            } else {
                double product = 0;
                double norma = 0;
                for (int j = 0; j < events.size(); j++) {
                    product += marks[curUserIndex][j] * marks[i][j];
                    norma += marks[i][j] * marks[i][j];
                }
                norma = Math.sqrt(norma);
                if (norma == 0) {
                    similarity[i] = 0;
                } else {
                    similarity[i] = product / (curNorma * norma);
                }
            }
        }
        for (int z = 0; z < events.size(); z++) {
            if (!isMarkPresent[curUserIndex][z] || !savePrevMarks) {
                double nume = 0;
                double deno = 0;
                for (int i = 0; i < users.size(); i++) {
                    if (i != curUserIndex) {
                        nume += marks[i][z] * similarity[i];
                        deno += similarity[i];
                    }
                }
                if (deno != 0) {
                    marks[curUserIndex][z] = avgMarks[curUserIndex] + nume / deno;
                } else {
                    marks[curUserIndex][z] = avgMarks[curUserIndex];
                }
            }
        }
        events.sort((a, b) -> {
            double markA = marks[curUserIndex][eventIndexes.get(a.getEventId())];
            double markB = marks[curUserIndex][eventIndexes.get(b.getEventId())];
            return Double.compare(markB, markA);
        });
        for (EventDto event : events) {
            event.setMark(marks[curUserIndex][eventIndexes.get(event.getEventId())]);
        }
        return events;
    }
}
