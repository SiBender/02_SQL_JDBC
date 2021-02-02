package com.foxminded.school.controller.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import com.foxminded.school.model.Group;
import com.foxminded.school.controller.dao.GroupDao;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GroupRepositoryTest {
    List<Group> groups;
    GroupDao groupDao;
    GroupRepository groupRepository;

    @BeforeAll
    void init() throws SQLException {
        groups = new ArrayList<>();
        groups.add(new Group("group1"));
        groups.add(new Group("group2"));
        groups.add(new Group("group3"));
        groups.add(new Group("group4"));
        groups.add(new Group("group5"));
        
        groupDao = Mockito.mock(GroupDao.class);
        
        groupRepository = new GroupRepository(groupDao);
        
        Mockito.when(groupDao.getByMaxSize(0)).thenReturn(new ArrayList<Group>());
        Mockito.when(groupDao.getByMaxSize(10)).thenReturn(groups.subList(0, 3));
        Mockito.when(groupDao.getByMaxSize(20)).thenReturn(groups);
    }
    
    @ParameterizedTest
    @CsvSource({"0, 0",
                "3, 10",
                "5, 20"})
    void getByMaxSizeShouldReturnDifferentLengthListOfGroupObjectsTest(int expextedListSize, int methodInput) throws SQLException {
        assertEquals(expextedListSize, groupRepository.getByMaxSize(methodInput).size());
        if (methodInput == 20) {
            assertSame(groups, groupRepository.getByMaxSize(methodInput));
        }
    }
}
