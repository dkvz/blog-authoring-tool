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

/**
 *
 * @author william
 */
public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        try {
            AppConfig.getInstance().connectDatabase();
            if (AppConfig.getInstance().getDatabase().isConnected()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Database connected", ButtonType.OK);
                alert.setHeaderText("");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Database not connected", ButtonType.OK);
                alert.setHeaderText("");
                alert.showAndWait();
            }
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "SQLException: " + ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("");
            alert.showAndWait();
            this.exitProgramError();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The database file was not found.", ButtonType.OK);
            alert.setHeaderText("");
            alert.showAndWait();
            this.exitProgramError();
        }
        Parent root = FXMLLoader.load(getClass().getResource("/eu/dkvz/BlogAuthoring/views/MainFrame.fxml"));        
        Scene scene = new Scene(root);
        stage.setTitle("Blog Authoring Thingy");
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
