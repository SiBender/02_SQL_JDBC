package com.foxminded.school.controller.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public enum DatabaseDataSource {
    INSTANCE;
    
    private static final String CONFIG_FILE_NAME = "configDB.properties";
    
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource dataSource;

    static {
        URL resource = DatabaseDataSource.class.getClassLoader().getResource(CONFIG_FILE_NAME);
        File propertyFile = new File(resource.getFile());
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(propertyFile)) {
        properties.load(fileInputStream);
        
        config.setJdbcUrl(properties.getProperty("jdbc.url"));
        config.setUsername(properties.getProperty("db.username"));
        config.setPassword(properties.getProperty("db.password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("setMaximumIdle", "1");
        dataSource = new HikariDataSource(config);
        } catch (FileNotFoundException e) {
            System.err.println("Can't find configuration file " + CONFIG_FILE_NAME);
        }catch (IOException e) {
            System.err.println("IOException while opening file " + CONFIG_FILE_NAME);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
