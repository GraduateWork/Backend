package org.graduatework.backend.db;

import java.util.Collection;
import java.util.List;

public interface DBAdaptorInfo {
    DBUser getUser(String username);
    List<DBUser> getUsers();
    boolean insertUser(DBUser user) throws IllegalArgumentException;
    boolean activateUser(String username) throws IllegalArgumentException;
    boolean clearEvents();
    boolean deleteEvents(Collection<Event> events);
    boolean insertEvents(List<Event> events);
    List<Event> getEvents();
    Event getEvent(int eventId);
    List<Event> getEventsByUser(String username);
    List<Event> getFavoritesByUser(String username);
    boolean updateEventForUser(String username, int eventId, boolean setViewed, boolean updateFavorite) throws IllegalArgumentException;
    List<UserEvent> getUserEvents(String username);
}
