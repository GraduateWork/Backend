package org.graduatework.backend.db;

import org.graduatework.backend.dto.DBUser;
import org.graduatework.backend.dto.Event;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DBAdaptor {

    private static final String GET_USER = "SELECT * FROM Users WHERE username = ? OR email = ?;";
    //private static final String UPDATE_USER = "UPDATE Files SET text = ? WHERE name = ?";
    private static final String INSERT_USER = "INSERT INTO Users(username, email, password, isActivated, creationTime) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
    private static final String ACTIVATE_USER = "UPDATE Users SET isActivated = true WHERE username = ? OR email = ?";

    private static final String GET_EVENTS = "SELECT * FROM Events;";
    private static final String GET_TAGS_BY_EVENT = "SELECT * FROM Tags WHERE (SELECT COUNT(*) FROM EventTags WHERE eventId = ? AND EventTags.tagId = Tags.tagId);";

    private Connection connection;

    public DBAdaptor(String dbUrl) {
        try {
            connection = DriverManager.getConnection(dbUrl);
        } catch (SQLException e) {
            System.out.println("Cannot connect to DB: " + e.getMessage());
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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean insertEvent(Event event) {
        return false;
    }

    public List<Event> getEvents() {
        try {
            PreparedStatement statement = connection.prepareStatement(GET_EVENTS);
            ResultSet rs = statement.executeQuery();
            List<Event> events = new ArrayList<>();
            while (rs.next()) {
                long startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("startTime")).getTime();
                String endTimeString = rs.getString("endTime");
                Long endTime = null;
                if (endTimeString != null) {
                    endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTimeString).getTime();
                }
                Event event = new Event(rs.getString("name"), startTime, endTime, rs.getString("picUrl"));
                events.add(event);

                PreparedStatement tagsStatement = connection.prepareStatement(GET_TAGS_BY_EVENT);
                tagsStatement.setInt(1, rs.getInt("eventId"));
            }
            return events;
        } catch (SQLException | ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
