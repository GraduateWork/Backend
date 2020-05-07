package org.graduatework.backend.db;

import org.graduatework.backend.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class DBAdaptor {

    private static final String GET_USER = "SELECT * FROM \"Users\" WHERE username = ? OR email = ?;";
    //private static final String UPDATE_USER = "UPDATE Files SET text = ? WHERE name = ?;";
    private static final String INSERT_USER = "INSERT INTO \"Users\" (username, email, password, \"isActivated\") VALUES (?, ?, ?, ?)";
    private static final String ACTIVATE_USER = "UPDATE \"Users\" SET \"isActivated\" = true WHERE username = ? OR email = ?;";

    private static final String GET_EVENTS = "SELECT * FROM \"Events\";";
    private static final String GET_EVENTS_BY_USER = "SELECT * FROM \"Events\" WHERE " +
            "(SELECT COUNT(*) FROM \"UserEvents\" WHERE \"UserEvents\".\"eventId\" = \"Events\".\"eventId\"" +
            "AND (\"UserEvents\".\"userId\" = ?)) > 0;";
    private static final String GET_FAVORITES_BY_USER = "SELECT * FROM \"Events\" WHERE " +
            "(SELECT COUNT(*) FROM \"UserEvents\" WHERE \"UserEvents\".\"eventId\" = \"Events\".\"eventId\"" +
            "AND \"UserEvents\".\"userId\" = ? AND \"UserEvents\".\"isFavorite\") > 0;";
    //private static final String GET_TAGS_BY_EVENT = "SELECT * FROM Tags WHERE (SELECT COUNT(*) FROM EventTags WHERE eventId = ? AND EventTags.tagId = Tags.tagId);";
    private static final String INSERT_EVENT = "INSERT INTO \"Events\" (\"eventId\", \"title\", \"startTime\", \"endTime\", \"imgSrc\", \"description\", \"type\") "
            + "VALUES(?, ?, ?, ?, ?, ?, ?)";
    private static final String CLEAR_EVENTS = "TRUNCATE \"Events\" CASCADE;";
    private static final String CLEAR_EVENT_DETAILS = "TRUNCATE \"EventDetails\";";
    private static final String INSERT_EVENT_DETAILS = "INSERT INTO \"EventDetails\" (\"propertyKey\", \"propertyValue\", \"eventId\") "
            + "VALUES(?, ?, ?)";
    private static final String GET_EVENT_DETAILS_BY_EVENT = "SELECT * FROM \"EventDetails\" WHERE \"eventId\" = ?;";
    private static final String DELETE_EVENT = "DELETE FROM \"Events\" WHERE \"eventId\" = ?;";
    private static final String DELETE_EVENT_DETAILS = "DELETE FROM \"EventDetails\" WHERE \"eventId\" = ?;";
    private static final String DELETE_USER_EVENTS = "DELETE FROM \"UserEvents\" WHERE \"eventId\" = ?;";

    private static final String UPDATE_EVENT = "UPDATE \"Events\" SET \"title\" = ?, \"startTime\" = ?, \"endTime\" = ?, " +
            "\"imgSrc\" = ?, \"description\" = ?, \"type\" = ? WHERE \"eventId\" = ?;";

    private static final String DELETE_USER_EVENT = "DELETE FROM \"UserEvents\" WHERE \"userId\" = ? AND \"eventId\" = ?;";
    private static final String INSERT_USER_EVENT = "INSERT INTO \"UserEvents\" (\"userId\", \"eventId\", \"isFavorite\", \"mark\") " +
            "VALUES (?, ?, ?, ?);";
    private static final String GET_USER_EVENT = "SELECT * FROM \"UserEvents\" WHERE \"userId\" = ? AND \"eventId\" = ?;";
    private static final String GET_USER_EVENTS = "SELECT * FROM \"UserEvents\" WHERE \"userId\" = ?;";
    private static final String UPDATE_USER_EVENT = "UPDATE \"UserEvents\" SET \"isFavorite\" = ?, \"mark\" = ? " +
            "WHERE \"userId\" = ? AND \"eventId\" = ?;";

    private static final Random rand = new Random(System.currentTimeMillis());

    public DBAdaptor(String dbUrl) {
    }

    public DBUser getUser(String username) {
        Connection connection = null;
        try {
            connection = DatabaseConfig.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_USER);
            statement.setString(1, username);
            statement.setString(2, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next() && !rs.isClosed() && rs.getString("username") != null) {
                return new DBUser(rs.getInt("userId"), rs.getString("username"), rs.getString("email"),
                        rs.getString("password"), rs.getBoolean("isActivated"), 0);
            } else {
                throw new IllegalArgumentException("Such user doesn't exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return null;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean insertUser(DBUser user) throws IllegalArgumentException {
        Connection connection = null;
        try {
            connection = DatabaseConfig.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_USER);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            ResultSet rs = statement.executeQuery();
            if (rs.next() && !rs.isClosed() && rs.getString("username") != null) {
                throw new IllegalArgumentException("Such user already exists.");
            } else {
                PreparedStatement insertStatement = connection.prepareStatement(INSERT_USER);
                insertStatement.setString(1, user.getUsername());
                insertStatement.setString(2, user.getEmail());
                insertStatement.setString(3, user.getPassword());
                insertStatement.setBoolean(4, user.isActivated());
                int insertResult = insertStatement.executeUpdate();
                return insertResult > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean activateUser(String username) throws IllegalArgumentException {
        Connection connection = null;
        try {
            connection = DatabaseConfig.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_USER);
            statement.setString(1, username);
            statement.setString(2, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next() && !rs.isClosed() && rs.getString("username") != null) {
                PreparedStatement updateStatement = connection.prepareStatement(ACTIVATE_USER);
                updateStatement.setString(1, username);
                updateStatement.setString(2, username);
                int updateResult = updateStatement.executeUpdate();
                return updateResult > 0;
            } else {
                throw new IllegalArgumentException("Such user doesn't exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean clearEvents() {
        Connection connection = null;
        try {
            connection = DatabaseConfig.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(CLEAR_EVENTS);
            statement.executeUpdate();
            statement = connection.prepareStatement(CLEAR_EVENT_DETAILS);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public boolean deleteEvents(Collection<Event> events) {
        Connection connection = null;
        try {
            for (Event event : events) {
                connection = DatabaseConfig.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(DELETE_USER_EVENTS);
                statement.setInt(1, event.getEventId());
                statement.executeUpdate();
                statement = connection.prepareStatement(DELETE_EVENT_DETAILS);
                statement.setInt(1, event.getEventId());
                statement.executeUpdate();
                statement = connection.prepareStatement(DELETE_EVENT);
                statement.setInt(1, event.getEventId());
                statement.executeUpdate();
                statement.closeOnCompletion();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                try {
                    System.out.println("Closed.");
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public boolean insertEvents(List<Event> events) {
        Connection connection = null;
        try {
            connection = DatabaseConfig.getDataSource().getConnection();
            PreparedStatement eventStatement = connection.prepareStatement(INSERT_EVENT);
            PreparedStatement detailsStatement = connection.prepareStatement(INSERT_EVENT_DETAILS);
            PreparedStatement updateEventStatement = connection.prepareStatement(UPDATE_EVENT);
            PreparedStatement clearDetailsStatement = connection.prepareStatement(CLEAR_EVENT_DETAILS);
            clearDetailsStatement.executeUpdate();

            int eventCount = 0;
            int detailsCount = 0;
            int updateEventCount = 0;

            int i = 0;
            for (Event event : events) {
                i++;
                if (event.getEventId() == null) {
                    int eventId = rand.nextInt();
                    event.setEventId(eventId);
                    eventStatement.setInt(1, eventId);
                    eventStatement.setString(2, event.getTitle());
                    eventStatement.setString(3, event.getStartTime());
                    eventStatement.setString(4, event.getEndTime());
                    eventStatement.setString(5, event.getImgSrc());
                    eventStatement.setString(6, event.getDescription());
                    eventStatement.setString(7, event.getType());

                    eventStatement.addBatch();
                    eventCount++;
                    // execute every 100 rows or less
                    if (eventCount % 100 == 0 || i == events.size()) {
                        eventStatement.executeBatch();
                    }
                } else {
                    updateEventStatement.setString(1, event.getTitle());
                    updateEventStatement.setString(2, event.getStartTime());
                    updateEventStatement.setString(3, event.getEndTime());
                    updateEventStatement.setString(4, event.getImgSrc());
                    updateEventStatement.setString(5, event.getDescription());
                    updateEventStatement.setString(6, event.getType());
                    updateEventStatement.setInt(7, event.getEventId());

                    updateEventStatement.addBatch();
                    updateEventCount++;
                    // execute every 100 rows or less
                    if (updateEventCount % 100 == 0 || i == events.size()) {
                        eventStatement.executeBatch();
                    }
                }
            }
            for (Event event : events) {
                int eventId = event.getEventId();
                for (String key : event.getDetails().keySet()) {
                    String value = event.getDetails().get(key);
                    detailsStatement.setString(1, key);
                    detailsStatement.setString(2, value);
                    detailsStatement.setInt(3, eventId);

                    detailsStatement.addBatch();
                    detailsCount++;
                    if (detailsCount % 100 == 0 || detailsCount == event.getDetails().size()) {
                        detailsStatement.executeBatch();
                    }
                }
            }
            eventStatement.closeOnCompletion();
            updateEventStatement.closeOnCompletion();
            clearDetailsStatement.closeOnCompletion();
            detailsStatement.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public List<Event> getEvents() {
        Connection connection = null;
        try {
            connection = DatabaseConfig.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_EVENTS);
            ResultSet rs = statement.executeQuery();
            List<Event> events = new ArrayList<>();
            while (rs.next()) {
                int eventId = rs.getInt("eventId");
                Event event = new Event(rs.getString("title"), rs.getString("startTime"), rs.getString("endTime"),
                        rs.getString("imgSrc"), rs.getString("description"), rs.getString("type"));
                event.setEventId(eventId);
                PreparedStatement detailsStatement = connection.prepareStatement(GET_EVENT_DETAILS_BY_EVENT);
                detailsStatement.setInt(1, eventId);
                ResultSet details = detailsStatement.executeQuery();
                while (details.next()) {
                    event.getDetails().put(details.getString("propertyKey"), details.getString("propertyValue"));
                }
                events.add(event);

//                PreparedStatement tagsStatement = connection.prepareStatement(GET_TAGS_BY_EVENT);
//                tagsStatement.setInt(1, rs.getInt("eventId"));
            }
            statement.closeOnCompletion();
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return null;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Event> getEventsByUser(String username) {
        Connection connection = null;
        try {
            DBUser user = getUser(username);
            if (user == null) {
                return null;
            }
            int userId = user.getUserId();
            connection = DatabaseConfig.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_EVENTS_BY_USER);
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            List<Event> events = new ArrayList<>();
            while (rs.next()) {
                int eventId = rs.getInt("eventId");
                Event event = new Event(rs.getString("title"), rs.getString("startTime"), rs.getString("endTime"),
                        rs.getString("imgSrc"), rs.getString("description"), rs.getString("type"));
                event.setEventId(eventId);
                PreparedStatement detailsStatement = connection.prepareStatement(GET_EVENT_DETAILS_BY_EVENT);
                detailsStatement.setInt(1, eventId);
                ResultSet details = detailsStatement.executeQuery();
                while (details.next()) {
                    event.getDetails().put(details.getString("propertyKey"), details.getString("propertyValue"));
                }
                events.add(event);

//                PreparedStatement tagsStatement = connection.prepareStatement(GET_TAGS_BY_EVENT);
//                tagsStatement.setInt(1, rs.getInt("eventId"));
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return null;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Event> getFavoritesByUser(String username) {
        Connection connection = null;
        try {
            DBUser user = getUser(username);
            if (user == null) {
                return null;
            }
            int userId = user.getUserId();
            connection = DatabaseConfig.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_FAVORITES_BY_USER);
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            List<Event> events = new ArrayList<>();
            while (rs.next()) {
                int eventId = rs.getInt("eventId");
                Event event = new Event(rs.getString("title"), rs.getString("startTime"), rs.getString("endTime"),
                        rs.getString("imgSrc"), rs.getString("description"), rs.getString("type"));
                event.setEventId(eventId);
                PreparedStatement detailsStatement = connection.prepareStatement(GET_EVENT_DETAILS_BY_EVENT);
                detailsStatement.setInt(1, eventId);
                ResultSet details = detailsStatement.executeQuery();
                while (details.next()) {
                    event.getDetails().put(details.getString("propertyKey"), details.getString("propertyValue"));
                }
                events.add(event);

//                PreparedStatement tagsStatement = connection.prepareStatement(GET_TAGS_BY_EVENT);
//                tagsStatement.setInt(1, rs.getInt("eventId"));
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return null;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean updateEventForUser(String username, int eventId, boolean setViewed, boolean updateFavorite) throws IllegalArgumentException {
        Connection connection = null;
        try {
            DBUser user = getUser(username);
            if (user == null) {
                return false;
            }
            int userId = user.getUserId();
            connection = DatabaseConfig.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_USER_EVENT);
            statement.setInt(1, userId);
            statement.setInt(2, eventId);
            ResultSet rs = statement.executeQuery();
            if (rs.next() && !rs.isClosed() && rs.getInt("userId") == userId) {
                PreparedStatement updateStatement = connection.prepareStatement(UPDATE_USER_EVENT);
                updateStatement.setInt(3, userId);
                updateStatement.setInt(4, eventId);
                boolean isFavorite = rs.getBoolean("isFavorite");
                double mark = rs.getDouble("mark");
                if (setViewed && mark == 0)
                    mark++;
                if (updateFavorite) {
                    if (isFavorite)
                        mark--;
                    else
                        mark = 3;
                }
                updateStatement.setBoolean(1, !isFavorite);
                updateStatement.setDouble(2, mark);
                int updateResult = updateStatement.executeUpdate();
                return updateResult > 0;
            } else {
                PreparedStatement insertStatement = connection.prepareStatement(INSERT_USER_EVENT);
                insertStatement.setInt(1, userId);
                insertStatement.setInt(2, eventId);
                insertStatement.setBoolean(3, true);
                insertStatement.setDouble(4, 0);
                int insertResult = insertStatement.executeUpdate();
                return insertResult > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<UserEvent> getUserEvents(String username) {
        Connection connection = null;
        try {
            DBUser user = getUser(username);
            if (user == null) {
                return null;
            }
            int userId = user.getUserId();
            connection = DatabaseConfig.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_USER_EVENTS);
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            List<UserEvent> userEvents = new ArrayList<>();
            while (rs.next()) {
                int eventId = rs.getInt("eventId");
                UserEvent userEvent = new UserEvent(rs.getInt("userId"), rs.getInt("eventId"), rs.getBoolean("isFavorite"),
                        rs.getDouble("mark"));
                userEvents.add(userEvent);
            }
            return userEvents;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return null;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
