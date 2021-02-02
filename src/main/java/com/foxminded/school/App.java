package com.foxminded.school;

import java.io.IOException;
import java.sql.SQLException;

import com.foxminded.school.controller.db.DBInitializer;
import com.foxminded.school.view.Menu;

public class App {
    public static void main(String[] args) {        
        try {
            new DBInitializer().init();
            
            new Menu().start();
        } catch (IOException | SQLException e) {
            System.err.println(e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("System error");
        }
    } 
}
