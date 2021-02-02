package com.foxminded.school.controller.repository;

import java.sql.SQLException;
import java.util.List;

import com.foxminded.school.controller.dao.GroupDao;
import com.foxminded.school.model.Group;

public class GroupRepository {
    private GroupDao groupDao;
    
    public GroupRepository() {
        this.groupDao = new GroupDao();
    }
    
    public GroupRepository(GroupDao groupDao) {
        this.groupDao = groupDao;
    }
    
    public List<Group> getByMaxSize(int groupSize) throws SQLException {
        return groupDao.getByMaxSize(groupSize);
    }
}
