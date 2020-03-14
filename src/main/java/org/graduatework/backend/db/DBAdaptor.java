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

public class DBAdaptor {

    private static final String GET_USER = "SELECT * FROM Users WHERE username = ? OR email = ?;";
    //private static final String UPDATE_USER = "UPDATE Files SET text = ? WHERE name = ?;";
    private static final String INSERT_USER = "INSERT INTO Users(username, email, password, isActivated, creationTime) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
    private static final String ACTIVATE_USER = "UPDATE Users SET isActivated = true WHERE username = ? OR email = ?;";

    private static final String GET_EVENTS = "SELECT * FROM Events;";
    private static final String GET_TAGS_BY_EVENT = "SELECT * FROM Tags WHERE (SELECT COUNT(*) FROM EventTags WHERE eventId = ? AND EventTags.tagId = Tags.tagId);";
    private static final String INSERT_EVENT = "INSERT INTO Events(title, startTime, endTime, imgSrc, description, type) "
            + "VALUES(?, ?, ?, ?, ?, ?)";
    private static final String CLEAR_EVENTS = "TRUNCATE Events;";

    private Connection connection;

    public DBAdaptor(String dbUrl) {
        try {
            connection = DatabaseConfig.getDataSource().getConnection();
        } catch (SQLException e) {
            System.err.println("Cannot connect to DB: " + e.getMessage());
        }
    }

    public DBUser getUser(String username) {
        try {
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
            System.err.println(e.getMessage());
            return null;
        }
    }

    public boolean insertUser(DBUser user) throws IllegalArgumentException {
        try {
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
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean activateUser(String username) throws IllegalArgumentException {
        try {
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
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean clearEvents() {
        try {
            PreparedStatement statement = connection.prepareStatement(CLEAR_EVENTS);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean insertEvents(List<Event> events) {
        try {
            PreparedStatement statement = connection.prepareStatement(INSERT_EVENT);
            int count = 0;

            for (Event event : events) {
                statement.setString(1, event.getTitle());
                statement.setString(2, event.getStartTime());
                statement.setString(3, event.getEndTime());
                statement.setString(4, event.getImgSrc());
                statement.setString(5, event.getDescription());
                statement.setString(6, event.getType());

                statement.addBatch();
                count++;
                // execute every 100 rows or less
                if (count % 100 == 0 || count == events.size()) {
                    statement.executeBatch();
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    public List<Event> getEvents() {
        try {
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
            System.err.println(e.getMessage());
            return null;
        }
    }
}
