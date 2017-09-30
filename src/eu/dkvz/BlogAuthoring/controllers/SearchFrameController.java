/*
 * This program is free software.
 * I don't understand anything about licenses.
 */
package eu.dkvz.BlogAuthoring.controllers;

import eu.dkvz.BlogAuthoring.utils.UIUtils;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.*;
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
    @FXML
    private CheckBox checkBoxCaseSensitive;
    
    private Stage searchStage;
    private TextInputControl target;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.buttonFindNext.disableProperty()
                .bind(this.textFieldSearch.textProperty().isEmpty());
        this.buttonFindPrevious.disableProperty()
                .bind(this.textFieldSearch.textProperty().isEmpty());
    }    

    @FXML
    private void buttonFindNextAction(ActionEvent event) {
        // We just start searching from the caret position.
        // We need to inform the user if nothing was found.
        // I'm not checking for empty text field as the bindings
        // are supposed to prevent that case.
        // If caret is not at the start, and nothing was found,
        // propose searching from the start instead, which will
        // change the caret positions.
        try {
            int[] pos = this.find(this.textFieldSearch.getText(),
                    this.target.getText(this.target.getCaretPosition(), 
                            this.target.getLength()),
                    false);
            if (pos == null) {
                if (this.target.getCaretPosition() != 0 && this.target.getLength() > 0) {
                    if (UIUtils.confirmDialog("No matches. Do you want to search "
                            + "from the start?", "Search")) {
                        this.target.positionCaret(0);
                        // OMG recursion seriously??
                        this.buttonFindNextAction(event);
                    }
                } else {
                    UIUtils.infoAlert("No matches.", "Search");
                }
            } else {
                // Highlight the found text:
                this.highlightInTextArea(this.target.getCaretPosition() + pos[0], 
                        this.target.getCaretPosition() + pos[1]);
            }
        } catch (Exception ex) {
            UIUtils.errorAlert("Syntax error with the search request."
                    + " Do note it has to be a Java regex, so you might"
                    + " need to escape regex characters in your query",
                    "Search parsing error");
        }
    }

    @FXML
    private void buttonFindPreviousAction(ActionEvent event) {
        // Start searching from before the caret position.
        // See comments for the findNext method. I guess.
        try {
            int end = this.target.getCaretPosition() >= this.target.getAnchor() ? 
                    this.target.getAnchor() : this.target.getCaretPosition();
            int[] pos = this.find(this.textFieldSearch.getText(),
                    this.target.getText(0, end), true);
            if (pos == null) {
                if (this.target.getCaretPosition() != this.target.getLength() 
                        && this.target.getLength() > 0) {
                    if (UIUtils.confirmDialog("No matches. Do you want to search "
                            + "from the end?", "Search")) {
                        this.target.positionCaret(this.target.getLength());
                        // OMG recursion seriously??
                        this.buttonFindPreviousAction(event);
                    }
                } else {
                    UIUtils.infoAlert("No matches.", "Search");
                }
            } else {
                // Highlight the found text:
                this.highlightInTextArea(pos[0], pos[1]);
            }
        } catch (Exception ex) {
            UIUtils.errorAlert("Syntax error with the search request."
                    + "\n Do note it has to be a Java regex, so you might"
                    + " need to escape regex characters in your query",
                    "Search parsing error");
        }
    }
    
    private void highlightInTextArea(int start, int end) {
        this.target.selectRange(start, end);
    }
    
    private int[] find(String needle, String source, boolean backwards) throws Exception {
        Pattern p = null;
        if (this.checkBoxCaseSensitive.isSelected()) {
            p = Pattern.compile(needle);
        } else {
            p = Pattern.compile(needle, Pattern.CASE_INSENSITIVE);
        }
        Matcher m = p.matcher(source);
        if (backwards) {
            return this.searchBackward(m);
        } else {
            return this.searchForward(m);
        }
    }
    
    private int[] searchBackward(Matcher m) {
        // Only way I found to do this is to just use the last match.
        int[] ret = null;
        while (m.find()) {
            if (ret == null) {
                ret = new int[2];
            }
            ret[0] = m.start();
            ret[1] = m.end();
        }
        return ret;
    }
    
    private int[] searchForward(Matcher m) {
        int[] ret = null;
        if (m.find()) {
            ret = new int[2];
            ret[0] = m.start();
            ret[1] = m.end();
        }
        return ret;
    }

    @FXML
    private void buttonCloseAction(ActionEvent event) {
        this.hideFrame();
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
