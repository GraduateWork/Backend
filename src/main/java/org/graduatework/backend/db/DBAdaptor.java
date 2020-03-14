package org.graduatework.backend.db;

import org.graduatework.backend.config.DatabaseConfig;
import org.graduatework.backend.dto.DBUser;
import org.graduatework.backend.dto.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DBAdaptor {

    private static final String GET_USER = "SELECT * FROM \"Users\" WHERE username = ? OR email = ?;";
    //private static final String UPDATE_USER = "UPDATE Files SET text = ? WHERE name = ?;";
    private static final String INSERT_USER = "INSERT INTO \"Users\" (username, email, password, \"isActivated\") VALUES (?, ?, ?, ?)";
    private static final String ACTIVATE_USER = "UPDATE \"Users\" SET \"isActivated\" = true WHERE username = ? OR email = ?;";

    private static final String GET_EVENTS = "SELECT * FROM \"Events\";";
    //private static final String GET_TAGS_BY_EVENT = "SELECT * FROM Tags WHERE (SELECT COUNT(*) FROM EventTags WHERE eventId = ? AND EventTags.tagId = Tags.tagId);";
    private static final String INSERT_EVENT = "INSERT INTO \"Events\" (\"eventId\", \"title\", \"startTime\", \"endTime\", \"imgSrc\", \"description\", \"type\") "
            + "VALUES(?, ?, ?, ?, ?, ?, ?)";
    private static final String CLEAR_EVENTS = "TRUNCATE \"Events\" CASCADE;";
    private static final String CLEAR_EVENT_DETAILS = "TRUNCATE \"EventDetails\";";
    private static final String INSERT_EVENT_DETAILS = "INSERT INTO \"EventDetails\" (\"propertyKey\", \"propertyValue\", \"eventId\") "
            + "VALUES(?, ?, ?)";

    private static final Random rand = new Random(System.currentTimeMillis());

    public DBAdaptor(String dbUrl) {
    }

    public DBUser getUser(String username) {
        try {
            Connection connection = DatabaseConfig.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_USER);
            statement.setString(1, username);
            statement.setString(2, username);
            ResultSet rs = statement.executeQuery();
            if (!rs.isClosed() && rs.getString("username") != null) {
                return new DBUser(rs.getString("username"), rs.getString("email"),
                        rs.getString("password"), rs.getBoolean("isActivated"), rs.getLong("creationTime"));
            } else {
                throw new IllegalArgumentException("Such user doesn't exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return null;
        }
    }

    public boolean insertUser(DBUser user) throws IllegalArgumentException {
        try {
            Connection connection = DatabaseConfig.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_USER);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            ResultSet rs = statement.executeQuery();
            if (!rs.isClosed() && rs.getString("username") != null) {
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
        }
    }

    public boolean activateUser(String username) throws IllegalArgumentException {
        try {
            Connection connection = DatabaseConfig.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_USER);
            statement.setString(1, username);
            statement.setString(2, username);
            ResultSet rs = statement.executeQuery();
            if (!rs.isClosed() && rs.getString("username") != null) {
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
        }
    }

    public boolean clearEvents() {
        try {
            Connection connection = DatabaseConfig.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(CLEAR_EVENTS);
            statement.executeUpdate();
            statement = connection.prepareStatement(CLEAR_EVENT_DETAILS);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean insertEvents(List<Event> events) {
        try {
            Connection connection = DatabaseConfig.getDataSource().getConnection();
            PreparedStatement eventStatement = connection.prepareStatement(INSERT_EVENT);
            PreparedStatement detailsStatement = connection.prepareStatement(INSERT_EVENT_DETAILS);
            int eventCount = 0;
            int detailsCount = 0;

            for (Event event : events) {
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
                if (eventCount % 100 == 0 || eventCount == events.size()) {
                    eventStatement.executeBatch();
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
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    public List<Event> getEvents() {
        try {
            Connection connection = DatabaseConfig.getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_EVENTS);
            ResultSet rs = statement.executeQuery();
            List<Event> events = new ArrayList<>();
            while (rs.next()) {
                Event event = new Event(rs.getString("title"), rs.getString("startTime"), rs.getString("endTime"),
                        rs.getString("imgSrc"), rs.getString("description"), rs.getString("type"));

                events.add(event);

//                PreparedStatement tagsStatement = connection.prepareStatement(GET_TAGS_BY_EVENT);
//                tagsStatement.setInt(1, rs.getInt("eventId"));
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return null;
        }
    }
}
