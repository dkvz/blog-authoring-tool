/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dkvz.BlogAuthoring.main;

import java.io.IOException;
import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.stage.*;
import javafx.scene.*;
import java.sql.*;
import javafx.scene.control.*;
import eu.dkvz.BlogAuthoring.utils.*;

/**
 *
 * @author william
 */
public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        try {
            AppConfig.getInstance().connectDatabase();
            if (!AppConfig.getInstance().getDatabase().isConnected()) {
                UIUtils.errorAlert("Could not connect the database.", "Database error");
                this.exitProgramError();
            }
        } catch (SQLException ex) {
            UIUtils.errorAlert("SQL error loading the database: " + ex.getMessage(), "Database error");
            this.exitProgramError();
        } catch (IOException ex) {
            UIUtils.errorAlert("The database file could not be found.", "Database error");
            this.exitProgramError();
        }
        Parent root = FXMLLoader.load(getClass().getResource("/eu/dkvz/BlogAuthoring/views/MainFrame.fxml"));        
        Scene scene = new Scene(root);
        stage.setTitle(AppConfig.APP_TITLE);
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }
    
    public void exitProgramError() {
        Platform.exit();
        System.exit(1);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
