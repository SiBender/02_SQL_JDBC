package com.foxminded.school.controller.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.ibatis.jdbc.ScriptRunner;

public class SQLScriptRunner {
    private static final HikariConnectionPool connectionPool = HikariConnectionPool.INSTANCE;
    private static final String DB_SETUP_FILE_NAME = "createTables.sql";

    public void init() throws IOException, SQLException {
        File scriptFile = getFileFromResources(DB_SETUP_FILE_NAME);
        
        try (Connection connection = connectionPool.getConnection();
                BufferedReader reader = new BufferedReader(new FileReader(scriptFile))) {
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.setLogWriter(null);
            scriptRunner.runScript(reader);
        } catch (IOException e) {
            throw new IOException("Issues while reading " + DB_SETUP_FILE_NAME);
        } catch (SQLException e) {
            throw new SQLException("SQL Exception while building table structure");
        }
    }

    private File getFileFromResources(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("File is not found:" + fileName);
        } else {
            return new File(resource.getFile());
        }
    }
}
