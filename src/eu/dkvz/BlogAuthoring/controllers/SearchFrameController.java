/*
 * This program is free software.
 * I don't understand anything about licenses.
 */
package eu.dkvz.BlogAuthoring.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author william
 */
public class SearchFrameController implements Initializable {

    @FXML
    private TextField textFieldSearch;
    @FXML
    private Button buttonFindNext;
    @FXML
    private Button buttonFindPrevious;
    @FXML
    private Button buttonClose;
    
    private Stage searchStage;
    private TextInputControl target;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void buttonFindNextAction(ActionEvent event) {
    }

    @FXML
    private void buttonFindPreviousAction(ActionEvent event) {
    }

    @FXML
    private void buttonCloseAction(ActionEvent event) {
    }
    
    public void showFrame() {
        if (this.searchStage != null) {
            this.searchStage.show();
        }
    }
    
    public void hideFrame() {
        if (this.searchStage != null) {
            this.searchStage.hide();
        }
    }

    /**
     * @return the searchStage
     */
    public Stage getSearchStage() {
        return searchStage;
    }

    /**
     * @param searchStage the searchStage to set
     */
    public void setSearchStage(Stage searchStage) {
        this.searchStage = searchStage;
    }

    /**
     * @return the target
     */
    public TextInputControl getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(TextInputControl target) {
        this.target = target;
    }
    
}
