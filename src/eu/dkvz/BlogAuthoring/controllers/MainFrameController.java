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
import javafx.application.Platform;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    @FXML
    private CheckMenuItem checkMenuItemWordWrap;
    
    //private DisplayedArticle displayedArticle;
    private ObjectProperty<ArticleSummary> selectedArticle = new SimpleObjectProperty();
    private ObjectProperty<ArticleProperty> displayedArticle = new SimpleObjectProperty<>();
    private BooleanProperty newArticle = new SimpleBooleanProperty();
    private boolean modifiedBindingsSet = false;
    private BooleanProperty modified = new SimpleBooleanProperty();
    private boolean ignoreNextListSelection = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup some bindings:
        this.displayedArticle.setValue(null);
        this.setNewArticle(false);
        this.setModified(false);
        
        // Hide all the form controls if no article is selected AND new hasn't been clicked
        // (which all means displayedArticle holds a null value in its property)
        this.vBoxArticleForm.visibleProperty().bind(this.displayedArticle.isNotNull());
        this.buttonSave.disableProperty().bind(this.displayedArticle.isNull());
        this.buttonReload.disableProperty().bind(this.displayedArticle.isNull());
        this.toggleButtonPublished.disableProperty().bind(this.displayedArticle.isNull());
        this.buttonImage.disableProperty().bind(this.displayedArticle.isNull());
        this.buttonQuote.disableProperty().bind(this.displayedArticle.isNull());
        this.buttonCode.disableProperty().bind(this.displayedArticle.isNull());
        this.selectedArticle.bind(this.listViewArticles.getSelectionModel().selectedItemProperty());
        
        // Bind the word wrap thingy:
        this.textAreaArticle.wrapTextProperty().bind(this.checkMenuItemWordWrap.selectedProperty());
        this.textAreaArticleSummary.wrapTextProperty().bind(this.checkMenuItemWordWrap.selectedProperty());
        
        this.selectedArticle.addListener((ObservableValue<? extends ArticleSummary> obsValue, 
                ArticleSummary oldVal, ArticleSummary newVal) -> {
            if (!this.ignoreNextListSelection) {
                // We should ask for confirmation if:
                // - oldVal is not null
                // - newVal is different (check the id)
                // - We have used one of the text fields since the last loading of them
                //   -> Which is a problem because the article text and summary
                //   are not in the list anywhere.
                if ((oldVal != null && newVal != null)
                        && (oldVal.getId() != newVal.getId())
                        && this.isModified()) {
                    if (!UIUtils.confirmDialog("Loading another article will cancel all the "
                            + "modifications you were doing. Are you sure?\n"
                            + "This operation cannot be undone.", "Cancel modifications?")) {
                        // Because of this thing I should've used an acutal event listener.
                        Platform.runLater(() -> {
                            // This produces weird exceptions if not run in that thread.
                            this.ignoreNextListSelection = true;
                            //this.listViewArticles.getSelectionModel().clearSelection();
                            this.listViewArticles.getSelectionModel().select(oldVal);
                        });

                        return;
                    }
                }
                // Actually load the article:
                this.loadSelectedArticle();
            } else {
                this.ignoreNextListSelection = false;
            }
        });
        // Now I need some kind of thread to (re)load the article list.
        this.loadArticleList();
    }
    
    private void loadSelectedArticle() {
        if (this.selectedArticle.get() != null) {
            try {
                Article art = AppConfig.getInstance().getDatabase()
                        .getArticleById(this.selectedArticle.get().getId());
                if (art != null) {
                    this.displayedArticle.set(new ArticleProperty(art));
                    // I hope this actually resets the binding that clicking
                    // "new" previously might have set.
                    // Also I'm doing this with bidirectionnal bindings.
                    this.textFieldArticleURL.textProperty().bindBidirectional(this.displayedArticle.get().getArticleSummary().articleURLProperty());
                    this.textFieldThumbImage.textProperty().bindBidirectional(this.displayedArticle.get().getArticleSummary().thumbImageProperty());
                    this.textFieldTitle.textProperty().bindBidirectional(this.displayedArticle.get().getArticleSummary().titleProperty());
                    this.textAreaArticle.textProperty().bindBidirectional(this.displayedArticle.get().contentProperty());
                    this.textAreaArticleSummary.textProperty().bindBidirectional(this.displayedArticle.get().getArticleSummaryProperty().summaryProperty());
                    this.toggleButtonPublished.setSelected(this.displayedArticle.get().getArticleSummary().isPublished());
                    
                    // Set the bindings to the modified state if not done yet.
                    if (!this.isModifiedBindingsSet()) {
                        this.setModifiedBindings();
                    }
                    this.setModified(false);
                    this.labelStatus.setText("Loaded article " + Long.toString(art.getArticleSummary().getId()));
                }
            } catch (SQLException ex) {
                UIUtils.errorAlert("Database error reading article - " + ex.getMessage(), "Database error");
            } catch (Exception ex) {
                UIUtils.errorAlert("Exception loading article - " + ex.getMessage(), "Could not read article");
            }
        }
    }
    
    private void setModifiedBindings() {
        this.textAreaArticle.textProperty().addListener((e, o, n) -> {
            this.setModified(true);
        });
        this.textAreaArticleSummary.textProperty().addListener((e, o, n) -> {
            this.setModified(true);
        });
        this.textFieldArticleURL.textProperty().addListener((e, o, n) -> {
            this.setModified(true);
        });
        this.textFieldThumbImage.textProperty().addListener((e, o, n) -> {
            this.setModified(true);
        });
        this.textFieldTitle.textProperty().addListener((e, o, n) -> {
            this.setModified(true);
        });
        this.textFieldUserID.textProperty().addListener((e, o, n) -> {
            this.setModified(true);
        });
    }
    
    public void loadArticleList() {
//        ArticleProperty art = new ArticleProperty();
//        art.getArticleSummary().setTitle("Article title number one");
//        this.listViewArticles.getItems().add(art);
        this.labelStatus.setText("Loading...");
        Task<ObservableList<ArticleSummary>> regenArticles = new Task<ObservableList<ArticleSummary>>() {
            @Override
            protected ObservableList<ArticleSummary> call() throws Exception {
                this.updateProgress(0.1, 1.0);
                List<ArticleSummary> articles = AppConfig.getInstance().getDatabase()
                        .getArticleSummariesDescFromTo(0, -1, "");
                this.updateProgress(0.2, 1.0);
                ObservableList<ArticleSummary> ret = null;
                if (articles != null) {
                    ret = FXCollections.observableList(articles);
                }
                this.updateProgress(1.0, 1.0);
                return ret;
            }
        };
        regenArticles.setOnSucceeded(e -> {
            this.progressBarMain.progressProperty().unbind();
            this.progressBarMain.setProgress(0.0);
            this.labelStatus.setText("Articles list loaded");
        });
        regenArticles.setOnFailed(e -> {
            this.progressBarMain.progressProperty().unbind();
            this.progressBarMain.setProgress(0.0);
            String message = "Error loading the articles list";
            if (regenArticles.getException() != null) {
                message = message.concat(" - ").concat(regenArticles.getException().getMessage());
            }
            UIUtils.errorAlert(message, "Error");
        });
        this.progressBarMain.progressProperty().bind(regenArticles.progressProperty());
        this.listViewArticles.itemsProperty().bind(regenArticles.valueProperty());
        
        Thread t = new Thread(regenArticles);
        t.start();
        
    }
    
    @FXML
    private void buttonSaveAction(ActionEvent event) {
        // The user ID, author, User object or whatever won't be present
        // in the bindings for new articles. I have to set the value manually.
        UIUtils.infoAlert("Title is: " + this.displayedArticle.getValue().getArticleSummary().getTitle(), "lel");
        
        // We need different behavior if this is a new article:
        if (this.isNewArticle()) {
            
            this.setNewArticle(false);
        } else {
            
        }
        
        this.setModified(false);
    }
    
    @FXML
    private void toggleButtonPublishedAction(ActionEvent event) {
        // Changing published won't do anything if we don't save at the moment.
        this.displayedArticle.get().getArticleSummary().setPublished(this.toggleButtonPublished.isSelected());
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
                UIUtils.infoAlert("We got article " + art.getArticleSummary().getTitle(), AppConfig.APP_TITLE);
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
        if (this.isNewArticle()) {
            // Already editing a new article, ask for confirmation:
            if (!UIUtils.confirmDialog("This will wipe the current article you are writing, are you sure?\nThis operation cannot be undone.", AppConfig.APP_TITLE)) {
                return;
            }
        } else if (this.displayedArticle.get() != null && !this.isNewArticle()) {
            // We had an article selected for modification from the list.
            // Ask for confirmation again.
            if (!UIUtils.confirmDialog("All the changes made to the current article you were editing will be lost, are you sure?\nThis operation cannot be undone.", AppConfig.APP_TITLE)) {
                return;
            }
        }
        this.progressBarMain.setProgress(0.2);
        this.setNewArticle(true);
        // Clear everything up:
        this.displayedArticle.set(new ArticleProperty());
        this.listViewArticles.getSelectionModel().clearSelection();
        this.clearAllFields();
        // If I want to do crazy bindings to an actual Article object I need to inherit the thing but
        // override EVERY SINGLE get/set method.
        // Still I'm probably going to do it.
        // We could use bidirectionnal bindings here. But for the sake of whatever the sake is I'm doing
        // it unidirectionnal.
        this.progressBarMain.setProgress(0.4);
        this.displayedArticle.get().getArticleSummary().articleURLProperty().bind(this.textFieldArticleURL.textProperty());
        this.displayedArticle.get().getArticleSummary().thumbImageProperty().bind(this.textFieldThumbImage.textProperty());
        this.displayedArticle.get().getArticleSummary().titleProperty().bind(this.textFieldTitle.textProperty());
        this.displayedArticle.get().contentProperty().bind(this.textAreaArticle.textProperty());
        this.displayedArticle.get().getArticleSummaryProperty().summaryProperty().bind(this.textAreaArticleSummary.textProperty());
        this.toggleButtonPublished.setSelected(false);
        this.progressBarMain.setProgress(0.8);
        if (!this.isModifiedBindingsSet()) {
            this.setModifiedBindings();
            this.setModifiedBindingsSet(true);
        }
        this.progressBarMain.setProgress(0.9);
        this.setModified(false);
        this.progressBarMain.setProgress(0.0);
        this.labelStatus.setText("Editing new article");
    }
    
    private void clearAllFields() {
        this.textAreaArticle.clear();
        this.textAreaArticleSummary.clear();
        this.textFieldArticleURL.clear();
        this.textFieldThumbImage.clear();
        this.textFieldTitle.clear();
        this.textFieldUserID.clear();
    }

    /**
     * @return the displayedArticle
     */
    public ObjectProperty<ArticleProperty> getDisplayedArticle() {
        return displayedArticle;
    }

    /**
     * @return the newArticle
     */
    public final boolean isNewArticle() {
        return newArticle.get();
    }

    /**
     * @param newArticle the newArticle to set
     */
    public final void setNewArticle(boolean newArticle) {
        this.newArticle.set(newArticle);
    }
    
    public BooleanProperty newArticleProperty() {
        return this.newArticle;
    }
    
    public final boolean isModified() {
        return this.modified.get();
    }
    
    public final void setModified(boolean val) {
        this.modified.setValue(val);
    }

    /**
     * @return the modifiedBindingsSet
     */
    public boolean isModifiedBindingsSet() {
        return modifiedBindingsSet;
    }

    /**
     * @param modifiedBindingsSet the modifiedBindingsSet to set
     */
    public void setModifiedBindingsSet(boolean modifiedBindingsSet) {
        this.modifiedBindingsSet = modifiedBindingsSet;
    }
    
}
