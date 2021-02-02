package com.foxminded.school.model;

public class Group {
    private int id;
    private final String name;
    
    public Group(String name) {
        this.name = name;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
