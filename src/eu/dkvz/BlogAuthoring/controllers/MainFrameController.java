/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dkvz.BlogAuthoring.controllers;

import eu.dkvz.BlogAuthoring.main.AppConfig;
import java.net.URL;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import eu.dkvz.BlogAuthoring.model.*;
import eu.dkvz.BlogAuthoring.utils.UIUtils;
import java.sql.SQLException;
import javafx.beans.binding.*;
import javafx.concurrent.*;
import javafx.collections.*;
import javafx.scene.layout.*;

/**
 *
 * @author william
 */
public class MainFrameController implements Initializable {
    
    @FXML
    private Button buttonSave;
    @FXML
    private ToggleButton toggleButtonPublished;
    @FXML
    private Button buttonImage;
    @FXML
    private Button buttonQuote;
    @FXML
    private Button buttonCode;
    @FXML
    private ListView<ArticleSummary> listViewArticles;
    @FXML
    private TextField textFieldTitle;
    @FXML
    private Button buttonTags;
    @FXML
    private TextField textFieldThumbImage;
    @FXML
    private TextField textFieldUserID;
    @FXML
    private TextField textFieldArticleURL;
    @FXML
    private TextArea textAreaArticle;
    @FXML
    private TextArea textAreaArticleSummary;
    @FXML
    private Button buttonReload;
    @FXML
    private Button buttonNewArticle;
    @FXML
    private VBox vBoxArticleForm;
    @FXML
    private Label labelStatus;
    @FXML
    private ProgressBar progressBarMain;
    
    private DisplayedArticle displayedArticle;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup some bindings:
        this.displayedArticle = new DisplayedArticle();
        // Hide all the form controls if no article is selected AND new hasn't been clicked
        // (which all means displayedArticle holds a null value in its property)
        this.vBoxArticleForm.visibleProperty().bind(this.displayedArticle.displayedArticleProperty().isNotNull());
        this.buttonSave.disableProperty().bind(this.displayedArticle.displayedArticleProperty().isNull());
        this.buttonReload.disableProperty().bind(this.displayedArticle.displayedArticleProperty().isNull());
        this.toggleButtonPublished.disableProperty().bind(this.displayedArticle.displayedArticleProperty().isNull());
        this.buttonImage.disableProperty().bind(this.displayedArticle.displayedArticleProperty().isNull());
        this.buttonQuote.disableProperty().bind(this.displayedArticle.displayedArticleProperty().isNull());
        this.buttonCode.disableProperty().bind(this.displayedArticle.displayedArticleProperty().isNull());
        // Now I need some kind of thread to (re)load the article list.
        this.loadArticleList();
    }
    
    public void loadArticleList() {
//        ArticleProperty art = new ArticleProperty();
//        art.getArticleSummary().setTitle("Article title number one");
//        this.listViewArticles.getItems().add(art);
        Task<ObservableList<ArticleSummary>> regenArticle = new Task<ObservableList<ArticleSummary>>() {
            @Override
            protected ObservableList<ArticleSummary> call() throws Exception {
                // I need to bind the progress bar.
                
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        
    }
    
    @FXML
    private void buttonSaveAction(ActionEvent event) {
        // The user ID, author, User object or whatever won't be present
        // in the bindings for new articles. I have to set the value manually.
        UIUtils.infoAlert("Title is: " + this.displayedArticle.getDisplayedArticle().getArticleSummary().getTitle(), "lel");
    }
    
    @FXML
    private void toggleButtonPublishedAction(ActionEvent event) {
        
    }
    
    @FXML
    private void buttonImageAction(ActionEvent event) {
        
    }
    
    @FXML
    private void buttonQuoteAction(ActionEvent event) {
        
    }
    
    @FXML
    private void buttonCodeAction(ActionEvent event) {
        
    }
    
    @FXML
    private void menuItemQuitAction(ActionEvent event) {
        
    }
    
    @FXML
    private void menuItemSearchAction(ActionEvent event) {
        
    }
    
    @FXML
    private void buttonTagsAction(ActionEvent event) {
        
    }
    
    @FXML
    private void buttonDontClickAction(ActionEvent event) {
        try {
            Article art = AppConfig.getInstance().getDatabase().getArticleById(41l);
            if (art != null) {
                UIUtils.infoAlert("We got article " + Long.toString(art.getArticleSummary().getId()), AppConfig.APP_TITLE);
            } else {
                // Not found.
                UIUtils.errorAlert("Article not found.", "Not found error");
            }
        } catch (SQLException ex) {
            UIUtils.errorAlert("SQL error: " + ex.getMessage(), "SQL Error");
        }
    }
    
    @FXML
    private void buttonReloadAction(ActionEvent event) {
        
    }
    
    @FXML
    private void buttonNewArticleAction(ActionEvent event) {
        // We need to check if:
        // 1. We weren't already editing a new article
          // In which case newArticle is true
        // 2. We weren't modifying an existing article
          // In which case newArticle is false AND displayedArticle property is not null
        if (this.displayedArticle.isNewArticle()) {
            // Already editing a new article, ask for confirmation:
            if (!UIUtils.confirmDialog("This will wipe the current article you are writing, are you sure?\nThis operation cannot be undone.", AppConfig.APP_TITLE)) {
                return;
            }
        } else if (this.displayedArticle.getDisplayedArticle() != null && !this.displayedArticle.isNewArticle()) {
            // We had an article selected for modification from the list.
            // Ask for confirmation again.
            if (!UIUtils.confirmDialog("All the changes made to the current article you were editing will be lost, are you sure?\nThis operation cannot be undone.", AppConfig.APP_TITLE)) {
                return;
            }
        }
        // Clear everything up:
        this.displayedArticle.setDisplayedArticle(new ArticleProperty());
        // If I want to do crazy bindings to an actual Article object I need to inherit the thing but
        // override EVERY SINGLE get/set method.
        // Still I'm probably going to do it.
        // We could use bidirectionnal bindings here. But for the sake of whatever the sake is I'm doing
        // it unidirectionnal.
        this.displayedArticle.getDisplayedArticle().getArticleSummary().articleURLProperty().bind(this.textFieldArticleURL.textProperty());
        this.displayedArticle.getDisplayedArticle().getArticleSummary().thumbImageProperty().bind(this.textFieldThumbImage.textProperty());
        this.displayedArticle.getDisplayedArticle().getArticleSummary().titleProperty().bind(this.textFieldTitle.textProperty());
        this.displayedArticle.getDisplayedArticle().contentProperty().bind(this.textAreaArticle.textProperty());
        this.displayedArticle.getDisplayedArticle().getArticleSummaryProperty().summaryProperty().bind(this.textAreaArticleSummary.textProperty());
    }

    /**
     * @return the displayedArticle
     */
    public DisplayedArticle getDisplayedArticle() {
        return displayedArticle;
    }
    
}
