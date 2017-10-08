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
import eu.dkvz.BlogAuthoring.utils.*;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author william
 */
public class LinkFrameController implements Initializable {

    @FXML
    private TextField textFieldHref;
    @FXML
    private CheckBox checkBoxOpenInNewTab;
    @FXML
    private Button buttonAdd;
    @FXML
    private Button buttonClose;
    
    private TextInputControl target;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.buttonAdd.disableProperty().bind(this.textFieldHref.textProperty().isEmpty());
    }    

    @FXML
    private void buttonAddAction(ActionEvent event) {
        if (this.target != null) {
            BlocsGenerator bg = new BlocsGenerator();
            UIUtils.surroundSelectionWithBloc(this.target, 
                bg.generateAnchorBefore(this.textFieldHref.getText(), 
                this.checkBoxOpenInNewTab.isSelected()), 
                bg.generateAnchorAfter());
            // Request focus back to target:
            Platform.runLater(() -> {
                this.target.requestFocus();
            });
        }
        this.closeWindow();
    }

    @FXML
    private void buttonCloseAction(ActionEvent event) {
        this.closeWindow();
    }
    
    private void closeWindow() {
        Stage current = (Stage)this.textFieldHref.getScene().getWindow();
        current.close();
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
