package com.foxminded.school.controller.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.foxminded.school.controller.dao.GroupDao;
import com.foxminded.school.controller.db.HikariConnectionPool;
import com.foxminded.school.controller.db.SQLScriptRunner;
import com.foxminded.school.model.Group;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GroupDaoTest {
    private final HikariConnectionPool connectionPool = HikariConnectionPool.INSTANCE;
    private GroupDao groupDao = new GroupDao();
    
    @BeforeAll
    void refresh() throws IOException, SQLException {
        new SQLScriptRunner().init();
    }
    
    @Test
    void createShouldAddNewRawInGroupsTableTest() throws SQLException {
        String groupName = "gr-06";
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(groupName));
        groupDao.create(groups);
        
        String query = "SELECT COUNT(*) FROM groups";
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            int expectedRowsNum = 6;
            assertEquals(expectedRowsNum, result.getInt(1));
        } 
        
        query = "SELECT group_name FROM groups ORDER BY group_id DESC LIMIT 1";
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            assertEquals(groupName, result.getString(1));
        }
    }

    @Test
    void createShouldThrowExceptionWhenGroupNameIsNullTest() {
        List<Group> groups = new ArrayList<>();
        groups.add(new Group(null));
        
        Throwable thrown = assertThrows(SQLException.class, () -> {
            groupDao.create(groups);
        });
        
        String expected = "SQL Exception while inserting data into 'groups'";
        assertTrue(thrown.getMessage().contains(expected));
    }
    
    @ParameterizedTest
    @CsvSource({"0, 0",
                "1, 0",
                "2, 2",
                "3, 3",
                "4, 3",
                "5, 4",
                "6, 4",
                "7, 4",
                "8, 5",
                "9, 5"})
    void getByMaxSizeShouldReturnListOfGroupObjectsTest(int maxSize, int expectedGroupsNum) throws SQLException {
        List<Group> groups = groupDao.getByMaxSize(maxSize);
        assertEquals(expectedGroupsNum, groups.size());
    }
}
