/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dkvz.BlogAuthoring.controllers;

import java.net.URL;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import eu.dkvz.BlogAuthoring.model.*;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.geometry.Point2D;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author william
 */
public class TagsFrameController implements Initializable {

    @FXML
    private Button buttonNewTag;
    @FXML
    private Button buttonAddTag;
    @FXML
    private Button buttonRemoveTag;
    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonCancel;
    @FXML
    private ListView<ArticleTag> listTags;
    @FXML
    private ListView<ArticleTag> listArticleTags;

    private final ObjectProperty<ArticleTag> selectedTagLeft = new SimpleObjectProperty();
    private final ObjectProperty<ArticleTag> selectedTagRight = new SimpleObjectProperty();
    private ObservableList<ArticleTag> initialTags;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Bind selected item on the two lists.
        // Bind add on there being a selected item on the left list.
        // Bind remove on there being a selected item on the right list.
        // Register listeners for the selected items, when selecting something
        // in one list, deselect the item from the other list.
        this.selectedTagLeft.bind(this.listTags.getSelectionModel().selectedItemProperty());
        this.selectedTagRight.bind(this.listArticleTags.getSelectionModel().selectedItemProperty());
        this.buttonAddTag.disableProperty().bind(this.selectedTagLeft.isNull());
        this.buttonRemoveTag.disableProperty().bind(this.selectedTagRight.isNull());
        this.selectedTagLeft.addListener((ObservableValue<? extends ArticleTag> e, 
                ArticleTag oldVal, ArticleTag newVal) -> {
            if (newVal != null) {
                // Deselect the item from the right list:
                this.listArticleTags.getSelectionModel().clearSelection();
            }
        });
        this.selectedTagRight.addListener((ObservableValue<? extends ArticleTag> e, 
                ArticleTag oldVal, ArticleTag newVal) -> {
            if (newVal != null) {
                // Deselect the item from the left list:
                this.listTags.getSelectionModel().clearSelection();
            }
        });
    }    

    @FXML
    private void buttonNewTagAction(ActionEvent event) {
    }

    @FXML
    private void buttonAddTagAction(ActionEvent event) {
        // Get selected item from the left list, add to the
        // right list.
        if (this.selectedTagLeft.get() != null) {
            this.listArticleTags.getItems().add(this.selectedTagLeft.get());
            // Remove it from the other:
            this.listTags.getItems().remove(this.selectedTagLeft.get());
        }
    }

    @FXML
    private void buttonRemoveTagAction(ActionEvent event) {
        if (this.selectedTagRight.get() != null) {
            this.listArticleTags.getItems().remove(this.selectedTagRight.get());
            // Add it to the other:
            this.listTags.getItems().add(this.selectedTagRight.get());
        }
    }

    @FXML
    private void buttonSaveAction(ActionEvent event) {
        // Because of the bidirectional binding we can
        // probably just close this window.
        this.closeWindow();
    }

    @FXML
    private void buttonCancelAction(ActionEvent event) {
        // We actually need to cancel the changes.
        // If I set it back to the initial state I 
        // guess the bidirectional binding should 
        // still work:
        this.listArticleTags.itemsProperty().set(this.getInitialTagsObservable());
        this.closeWindow();
    }
    
    private void closeWindow() {
        Stage current = (Stage)this.buttonSave.getScene().getWindow();
        current.close();
    }

    /**
     * @return the listTags
     */
    public ListView<ArticleTag> getListTags() {
        return listTags;
    }

    /**
     * @return the listArticleTags
     */
    public ListView<ArticleTag> getListArticleTags() {
        return listArticleTags;
    }

    /**
     * @return the initialTags
     */
    public List<ArticleTag> getInitialTags() {
        return initialTags;
    }

    /**
     * @param initialTags the initialTags to set
     */
    public void setInitialTags(List<ArticleTag> initialTags) {
        this.initialTags = FXCollections.observableList(initialTags);
    }
    
    public ObservableList<ArticleTag> getInitialTagsObservable() {
        return this.initialTags;
    }

}
