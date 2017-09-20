/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dkvz.BlogAuthoring.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import eu.dkvz.BlogAuthoring.utils.*;
import eu.dkvz.BlogAuthoring.main.*;

/**
 * FXML Controller class
 *
 * @author Alain
 */
public class EmbedImageFrameController implements Initializable {

    @FXML
    private ToggleGroup tGRoupAlignment;
    @FXML
    private TextField textFieldImageURL;
    @FXML
    private TextField textFieldMiniatureURL;
    @FXML
    private TextField textFieldAltText;
    @FXML
    private TextField textFieldWidth;
    @FXML
    private TextField textFieldHeight;
    @FXML
    private TextField textFieldLegend;
    @FXML
    private RadioButton radioButtonLeft;
    @FXML
    private RadioButton radioButtonCenter;
    @FXML
    private RadioButton radioButtonRight;
    
    private TextArea textAreaTarget;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Does nothing.
    }    

    @FXML
    private void buttonAddAction(ActionEvent event) {
        // Add the code at the current carret position.
        if (this.textAreaTarget != null) {
            
        }
        this.closeWindow();
    }

    @FXML
    private void buttonCancelAction(ActionEvent event) {
        this.closeWindow();
    }
    
    private void closeWindow() {
        Stage current = (Stage)this.textFieldImageURL.getScene().getWindow();
        current.close();
    }

    /**
     * @return the textAreaTarget
     */
    public TextArea getTextAreaTarget() {
        return textAreaTarget;
    }

    /**
     * @param textAreaTarget the textAreaTarget to set
     */
    public void setTextAreaTarget(TextArea textAreaTarget) {
        this.textAreaTarget = textAreaTarget;
    }
    
}
