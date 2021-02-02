package com.foxminded.school.controller.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.foxminded.school.controller.db.HikariConnectionPool;
import com.foxminded.school.model.Group;

public class GroupDao {
    private static final HikariConnectionPool connectionPool = HikariConnectionPool.INSTANCE;
    
    public void create(List<Group> groups) throws SQLException {
        String query = "INSERT INTO groups (group_name) VALUES (?)";
        try (Connection connection = connectionPool.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            for (Group group : groups) {
                statement.setString(1, group.getName());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new SQLException("SQL Exception while inserting data into 'groups'" + e.getMessage());
        }
    }

    public List<Group> getByMaxSize(int max) throws SQLException {        
        String query = "SELECT gr.group_name FROM groups gr" + System.lineSeparator() + 
                       "JOIN students st ON gr.group_id = st.group_id" + System.lineSeparator() + 
                       "GROUP BY gr.group_id HAVING count(st.group_id) <= ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setInt(1, max);
            ResultSet result = statement.executeQuery();
            List<Group> output = new ArrayList<>();
            while(result.next()) {
                output.add(new Group(result.getString(1)));
            }
            return output;
        }
    }
}
