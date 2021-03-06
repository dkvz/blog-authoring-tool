/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dkvz.BlogAuthoring.main;

import eu.dkvz.BlogAuthoring.model.*;
import java.io.*;
import java.sql.*;
import javafx.stage.*;
import javafx.scene.image.*;

/**
 *
 * @author william
 */
public class AppConfig {
    
    private static final AppConfig INSTANCE = new AppConfig();
    public static final String DB_PATH = "db.sqlite";
    public static final String APP_TITLE = "Blog Authoring Thingy";
    public static final String IMAGE_BASE_URL = "wp-content/stuff/";
    
    private Stage primaryStage = null;
    private BlogDataAccess database = null;
    private Image applicationIcon = null;
    
    private AppConfig() {
        
    }
    
    public static final AppConfig getInstance() {
        return INSTANCE;
    }
    
    public void connectDatabase() throws SQLException, IOException {
        // If the database file doesn't exist, the data access thingy will create it.
        // Let's check if it exists:
        File f = new File(AppConfig.DB_PATH);
        if (!f.exists()) {
            throw new IOException("Database file does not exist");
        } else {
            this.database = new BlogDataAccessSQLite(AppConfig.DB_PATH);
            this.database.connect();
        }
    }
    
    public void disconnectDatabase() throws SQLException {
        if (this.database != null) {
            this.database.disconnect();
            this.database = null;
        }
    }
    
    public BlogDataAccess getDatabase() throws SQLException {
        if (this.database != null) {
            return this.database;
        } else {
            throw new SQLException("Database connection has not been initialized");
        }
    }

    /**
     * @return the primaryStage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * @param primaryStage the primaryStage to set
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * @return the applicationIcon
     */
    public Image getApplicationIcon() {
        return applicationIcon;
    }

    /**
     * @param applicationIcon the applicationIcon to set
     */
    public void setApplicationIcon(Image applicationIcon) {
        this.applicationIcon = applicationIcon;
    }
    
}
