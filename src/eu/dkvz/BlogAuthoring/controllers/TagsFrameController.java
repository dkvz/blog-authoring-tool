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
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import eu.dkvz.BlogAuthoring.model.*;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void buttonNewTagAction(ActionEvent event) {
    }

    @FXML
    private void buttonAddTagAction(ActionEvent event) {
    }

    @FXML
    private void buttonRemoveTagAction(ActionEvent event) {
    }

    @FXML
    private void buttonSaveAction(ActionEvent event) {
    }

    @FXML
    private void buttonCancelAction(ActionEvent event) {
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

}
