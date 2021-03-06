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
import eu.dkvz.BlogAuthoring.utils.*;
import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    @FXML
    private Button buttonDeleteArticle;
    @FXML
    private MenuItem menuItemSave;
    @FXML
    private CheckMenuItem checkMenuItemUpdatePostDate;
    @FXML
    private MenuItem menuItemArticleID;
    @FXML
    private Button buttonLink;
    @FXML
    private MenuItem menuItemInsertP;
    
    private final ObjectProperty<ArticleSummary> selectedArticle = new SimpleObjectProperty();
    private final ObjectProperty<ArticleProperty> displayedArticle = new SimpleObjectProperty<>();
    private final BooleanProperty newArticle = new SimpleBooleanProperty();
    private boolean modifiedBindingsSet = false;
    private final BooleanProperty modified = new SimpleBooleanProperty();
    private boolean ignoreNextListSelection = false;
    private final BooleanProperty mandatoryFieldsNotFilled = new SimpleBooleanProperty();
    private Stage tagsWindow = null;
    private TextArea lastFocusedTextArea = null;
    private SearchFrameController searchController = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.displayedArticle.set(null);
        this.setNewArticle(false);
        this.setModified(false);
        this.setMandatoryFieldsNotFilled(false);
        
        // Hide all the form controls if no article is selected AND new hasn't been clicked
        // (which all means displayedArticle holds a null value in its property)
        this.vBoxArticleForm.visibleProperty().bind(this.displayedArticle.isNotNull());
        this.buttonSave.disableProperty().bind(this.displayedArticle.isNull());
        this.menuItemSave.disableProperty().bind(this.displayedArticle.isNull());
        this.buttonReload.disableProperty().bind(this.displayedArticle.isNull().or(this.newArticleProperty()));
        this.buttonDeleteArticle.disableProperty().bind(this.displayedArticle.isNull().or(this.newArticleProperty()));
        this.toggleButtonPublished.disableProperty().bind(this.displayedArticle.isNull());
        this.buttonImage.disableProperty().bind(this.displayedArticle.isNull());
        this.buttonLink.disableProperty().bind(this.displayedArticle.isNull());
        this.buttonQuote.disableProperty().bind(this.displayedArticle.isNull());
        this.buttonCode.disableProperty().bind(this.displayedArticle.isNull());
        this.menuItemArticleID.disableProperty().bind(this.displayedArticle.isNull().or(this.newArticleProperty()));
        this.selectedArticle.bind(this.listViewArticles.getSelectionModel().selectedItemProperty());
        
        Platform.runLater(() -> {
            // Making the bindings for the save button and the boolean property
            // informing us if the the mandatory fields are filled out.
            // We can run this "later" because save checks the mandatoryFieldsNotFilled thingy, 
            // which is initialized to false.
            this.mandatoryFieldsNotFilledProperty().bind(this.textAreaArticle.textProperty().isEmpty()
                    .or(this.textAreaArticleSummary.textProperty().isEmpty())
                    .or(this.textFieldArticleURL.textProperty().isEmpty())
                    .or(this.textFieldTitle.textProperty().isEmpty())
                    .or(this.textFieldUserID.textProperty().isEmpty()));
            this.buttonSave.disableProperty().bind(this.displayedArticle.isNull()
                    .or(this.mandatoryFieldsNotFilled));
            this.menuItemSave.disableProperty().bind(this.buttonSave.disableProperty());
        });
        
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
                if (this.isNewArticle() && this.isModified()) {
                    if (!UIUtils.confirmDialog("The current article has not been saved. "
                            + "Loading another one will delete what you were writing. Are you sure? \n"
                            + "This operation cannot be undone.", "Cancel modifications?")) {
                        return;
                    }
                } else if ((oldVal != null && newVal != null)
                        && (oldVal.getId() != newVal.getId())
                        && this.isModified()) {
                    if (!UIUtils.confirmDialog("Loading another article will cancel all the "
                            + "modifications you were doing. Are you sure? \n"
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
                this.setNewArticle(false);
            } else {
                this.ignoreNextListSelection = false;
            }
        });
        // Register the focus listener to know to which TextArea we are supposed to add stuff:
        this.textAreaArticle.focusedProperty().addListener((ObservableValue<? extends Boolean> e, 
                Boolean o, Boolean n) -> {
            this.setLastFocusedTextArea(this.textAreaArticle);
        });
        this.textAreaArticleSummary.focusedProperty().addListener((ObservableValue<? extends Boolean> e, 
                Boolean o, Boolean n) -> {
            this.setLastFocusedTextArea(this.textAreaArticleSummary);
        });
        // Now I need some kind of thread to (re)load the article list.
        this.loadArticleList(null);
    }
    
    private synchronized void loadSelectedArticle() {
        if (this.selectedArticle.get() != null) {
            try {
                Article art = AppConfig.getInstance().getDatabase()
                        .getArticleById(this.selectedArticle.get().getId());
                if (art != null) {
                    this.displayedArticle.set(new ArticleProperty(art));
                    // I hope this actually resets the binding that clicking
                    // "new" previously might have set.
                    // Also I'm doing this with bidirectionnal bindings.
                    this.textFieldArticleURL.textProperty().bindBidirectional(this.displayedArticle
                            .get().getArticleSummary().articleURLProperty());
                    this.textFieldThumbImage.textProperty().bindBidirectional(this.displayedArticle
                            .get().getArticleSummary().thumbImageProperty());
                    this.textFieldTitle.textProperty().bindBidirectional(this.displayedArticle
                            .get().getArticleSummary().titleProperty());
                    this.textAreaArticle.textProperty().bindBidirectional(this.displayedArticle
                            .get().contentProperty());
                    this.textAreaArticleSummary.textProperty().bindBidirectional(this.displayedArticle
                            .get().getArticleSummaryProperty().summaryProperty());
                    this.toggleButtonPublished.setSelected(this.displayedArticle
                            .get().getArticleSummary().isPublished());
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
    
    public synchronized void loadArticleList(ArticleSummary selected) {
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
            if (selected != null) {
                // Attempt to select provided article:
                this.listViewArticles.getSelectionModel().select(selected);
            }
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
    private void buttonLinkAction(ActionEvent event) {
        if (this.displayedArticle.get() != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass()
                        .getResource("/eu/dkvz/BlogAuthoring/views/LinkFrame.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initOwner(AppConfig.getInstance().getPrimaryStage());
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Add Link");
                Image icon = AppConfig.getInstance().getApplicationIcon();
                if (icon != null) {
                    stage.getIcons().add(icon);
                }
                LinkFrameController linkController = loader.getController();
                // Check if one of the TextArea has focus.
                linkController.setTarget(this.lastFocusedTextArea);
                stage.show();
            } catch (IOException ex) {
                UIUtils.errorAlert("Unexpected error loading the embed image window", AppConfig.APP_TITLE);
            }
        }
    }
    
    @FXML
    private void menuItemSaveAction(ActionEvent event) {
        this.buttonSaveAction(event);
    }
    
    @FXML
    private synchronized void buttonSaveAction(ActionEvent event) {
        // The user ID, author, User object or whatever won't be present
        // in the bindings for new articles. I have to set the value manually.
        // TODO Also published has to be set manually.
        // We need different behavior if this is a new article:
        if (!this.isMandatoryFieldsNotFilled() && this.getDisplayedArticle().get() != null) {
            this.displayedArticle.get().getArticleSummary().setPublished(this.toggleButtonPublished.isSelected());
            if (this.isNewArticle()) {
                // Time to check for mandatory fields.
                // Actually, going to do that with bindings to the save button
                // And I'm also going to use a property to force the check here
                // as it might prove useful if I want to have a menu or 
                // shortcut calling this method.
                User usr = new User();
                try {
                    usr.setId(Long.parseLong(this.textFieldUserID.getText()));
                } catch (NumberFormatException ex) {
                    usr.setId(1);
                }
                this.displayedArticle.get().getArticleSummary().setUser(usr);
                try {
                    if (AppConfig.getInstance().getDatabase().insertArticle(this.displayedArticle.get())) {
                        this.setNewArticle(false);
                        // We actually need to select the new article now...
                        // This is probably going to be painful.
                        // Also reload the list:
                        this.loadArticleList(this.displayedArticle.get().getArticleSummary());
                        this.setModified(false);
                    } else {
                        // Insert failed for some reason.
                        UIUtils.errorAlert("Could not insert new article - No insert rows reported", "Database error");
                    }
                } catch (SQLException ex) {
                    UIUtils.errorAlert("Could not insert new article - " + ex.getMessage(), "Database error");
                }
            } else if (this.isModified()) {
                try {
                    // Updating existing article:
                    try {
                        this.displayedArticle.get().getArticleSummary().getUser().setId(Long.parseLong(this.textFieldUserID.getText()));
                        if (this.checkMenuItemUpdatePostDate.isSelected()) {
                            this.displayedArticle.get().getArticleSummary().setDate(null);
                        }
                        if (AppConfig.getInstance().getDatabase().updateArticle(this.displayedArticle.get())) {
                            this.labelStatus.setText("Updated article " + Long.toString(this.displayedArticle.get().getArticleSummary().getId()));
                            this.setModified(false);
                        } else {
                            UIUtils.errorAlert("Could not update article - No updated rows reported", "Database error");
                        }
                    } catch (NumberFormatException ex) {
                        UIUtils.infoAlert("Invalid user ID, update aborted.", "Update Error");
                    }
                } catch (SQLException ex) {
                    UIUtils.errorAlert("Could not insert new article - " + ex.getMessage(), "Database error");
                }
            } else {
                UIUtils.infoAlert("No changes to save.", AppConfig.APP_TITLE);
            }
        } else {
            UIUtils.warningAlert("Missing mandatory fields. \nI'm not "
                    + "telling you which though, because laziness.",
                    AppConfig.APP_TITLE);
        }
    }
    
    @FXML
    private void toggleButtonPublishedAction(ActionEvent event) {
        // Changing published won't do anything if we don't save at the moment.
        this.displayedArticle.get().getArticleSummary().setPublished(this.toggleButtonPublished.isSelected());
        this.setModified(true);
    }
    
    @FXML
    private void buttonImageAction(ActionEvent event) {
        // Open the dedicated frame for this.
        // Modally. Much easier that way.
        // No sense in doing this if we're not either
        // writing a new article or editing one.
        if (this.displayedArticle.get() != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass()
                        .getResource("/eu/dkvz/BlogAuthoring/views/EmbedImageFrame.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initOwner(AppConfig.getInstance().getPrimaryStage());
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Add Image");
                Image icon = AppConfig.getInstance().getApplicationIcon();
                if (icon != null) {
                    stage.getIcons().add(icon);
                }
                EmbedImageFrameController imageController = loader.getController();
                // Check if one of the TextArea has focus.
                imageController.setTextAreaTarget(this.lastFocusedTextArea);
                stage.show();
            } catch (IOException ex) {
                UIUtils.errorAlert("Unexpected error loading the embed image window", AppConfig.APP_TITLE);
            }
        }
    }
    
    @FXML
    private void buttonQuoteAction(ActionEvent event) {
        // This is really weird code.
        BlocsGenerator bg = new BlocsGenerator();
        UIUtils.surroundSelectionWithBloc(this.lastFocusedTextArea, 
                bg.generateQuoteBefore(), bg.generateQuoteAfter());
        this.lastFocusedTextArea.requestFocus();
    }
    
    @FXML
    private void buttonCodeAction(ActionEvent event) {
        // This is really weird code.
        BlocsGenerator bg = new BlocsGenerator();
        UIUtils.surroundSelectionWithBloc(this.lastFocusedTextArea, 
                bg.generateCodeBefore(), bg.generateCodeAfter());
        this.lastFocusedTextArea.requestFocus();
    }
    
    @FXML
    private void menuItemQuitAction(ActionEvent event) {
        this.requestExit();
    }
    
    public void requestExit() {
        if (this.isModified()) {
            if (!UIUtils.confirmDialog("You're about to close the application, this "
                    + "will make you lose any pending modifications. Are you sure?"
                    , "Exit application")) {
                return;
            }
        }
        Platform.exit();
    }
    
    @FXML
    private void menuItemSearchAction(ActionEvent event) {
        // We'll keep the search window hidden once it was seen once.
        // What we actually need to keep reference of is the controller.
        if (this.displayedArticle.get() != null && this.lastFocusedTextArea != null) {
            if (this.searchController == null) {
                try {
                    // Create the search window.
                    FXMLLoader loader = new FXMLLoader(getClass()
                            .getResource("/eu/dkvz/BlogAuthoring/views/SearchFrame.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.initOwner(AppConfig.getInstance().getPrimaryStage());
                    stage.initModality(Modality.NONE);
                    stage.setTitle("Search");
                    Image icon = AppConfig.getInstance().getApplicationIcon();
                    if (icon != null) {
                        stage.getIcons().add(icon);
                    }
                    this.searchController = loader.getController();
                    this.searchController.setSearchStage(stage);
                } catch (IOException ex) {
                    UIUtils.errorAlert("Unexpected error loading the search window", AppConfig.APP_TITLE);
                    return;
                }
            }
            this.searchController.setTarget(this.lastFocusedTextArea);
            this.searchController.showFrame();
        }
    }
    
    @FXML
    private void buttonTagsAction(ActionEvent event) {
        // I wanted to make a window that could stay up and have bindings auto-update
        // the tags in it but I think I'm just going to use a modal dialog.
        if (this.displayedArticle.get() != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass()
                        .getResource("/eu/dkvz/BlogAuthoring/views/TagsFrame.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                scene.cursorProperty().set(Cursor.WAIT);
                stage.initOwner(AppConfig.getInstance().getPrimaryStage());
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Edit Article Tags");
                Image icon = AppConfig.getInstance().getApplicationIcon();
                if (icon != null) {
                    stage.getIcons().add(icon);
                }
                TagsFrameController tagsController = loader.getController();
                Platform.runLater(() -> {
                    try {
                        ObservableList<ArticleTag> allTags = 
                                FXCollections.observableList(AppConfig.getInstance()
                                        .getDatabase().getAllTags());
                        // allTags - articleTags is the initial value to the left list
                        this.displayedArticle.get().getArticleSummary().getTags()
                                .stream().forEach((tag) -> {
                            allTags.remove(tag);
                        });
                        // The left list is now allTags.
                        // The right is the tag list from displayedArticle, which requires
                        // bidirectional bindings.
                        tagsController.getListTags().setItems(allTags);
                        tagsController.getListArticleTags().itemsProperty()
                                .bindBidirectional(this.displayedArticle.get()
                                        .getArticleSummaryProperty().tagsProperty());
                        List<ArticleTag> initialTags = new ArrayList<>();
                        // Actually I kinda forgot we needed to keep record of the initial state
                        // somehwere to reload that when the user cancels the tags operations.
                        // We could reload from database but I'd rather limit the risk of getting
                        // exceptions.
                        initialTags.addAll(this.displayedArticle.get().getArticleSummary().getTags());
                        tagsController.setInitialTags(initialTags);
                        this.setModified(true);
                    } catch (SQLException ex) {
                        UIUtils.errorAlert("Error trying to fetch tags", "Database error");
                    } finally {
                        scene.cursorProperty().set(Cursor.DEFAULT);
                    }
                });
                stage.show();
            } catch (IOException ex) {
                UIUtils.errorAlert("Unexpected error loading the tags window", AppConfig.APP_TITLE);
            }
        }
    }
    
    @FXML
    private void buttonDontClickAction(ActionEvent event) {
        double currentSize = this.textAreaArticle.getFont().getSize();
        this.textAreaArticle.setFont(Font.font(currentSize + 1.0));
    }
    
    @FXML
    private void menuItemIncreaseFontSizeAction(ActionEvent event) {
        this.textAreaArticle.setFont(Font.font(this.textAreaArticle.getFont().getSize() + 1.0));
        this.textAreaArticleSummary.setFont(Font.font(this.textAreaArticle.getFont().getSize()));
    }

    @FXML
    private void menuItemDecreaseFontSizeAction(ActionEvent event) {
        this.textAreaArticle.setFont(Font.font(this.textAreaArticle.getFont().getSize() - 1.0));
        this.textAreaArticleSummary.setFont(Font.font(this.textAreaArticle.getFont().getSize()));
    }
    
    @FXML
    private synchronized void buttonReloadAction(ActionEvent event) {
        if (this.isModified()) {
            if (!UIUtils.confirmDialog("Reloading the article will undo all the changes you made. "
                    + "Are you sure? \nTHis operation cannot be undone.", AppConfig.APP_TITLE)) {
                return;
            }
        }
        this.loadSelectedArticle();
    }
    
    @FXML
    private synchronized void buttonNewArticleAction(ActionEvent event) {
        // We need to check if:
        // 1. We weren't already editing a new article
          // In which case newArticle is true
        // 2. We weren't modifying an existing article
          // In which case newArticle is false AND displayedArticle property is not null
        if (this.isNewArticle()) {
            // Already editing a new article, ask for confirmation:
            if (!UIUtils.confirmDialog("This will wipe the current article you are "
                    + "writing, are you sure? \nThis operation cannot be undone.", AppConfig.APP_TITLE)) {
                return;
            }
        } else if (this.displayedArticle.get() != null && !this.isNewArticle() && this.isModified()) {
            // We had an article selected for modification from the list.
            // Ask for confirmation again.
            if (!UIUtils.confirmDialog("All the changes made to the current article "
                    + "you were editing will be lost, are you sure? \nThis operation "
                    + "cannot be undone.", AppConfig.APP_TITLE)) {
                return;
            }
        }
        this.progressBarMain.setProgress(0.2);
        this.setNewArticle(true);
        // Clear everything up:
        this.displayedArticle.set(new ArticleProperty());
        // We need this weird boolean hack because clearSelection will trigger the
        // listener for the value for the list (actually only if there was a selection
        // to begin with).
        if (this.selectedArticle.get() != null) {
            this.ignoreNextListSelection = true;
            this.listViewArticles.getSelectionModel().clearSelection();
        }
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
        //this.textFieldUserID.clear();
        this.textFieldUserID.setText("1");
    }
    
    @FXML
    private synchronized void buttonDeleteArticleAction(ActionEvent event) {
        if (!this.isNewArticle() && this.displayedArticle.get() != null) {
            if (this.displayedArticle.get().getArticleSummary().getId() >= 0) {
                if (UIUtils.confirmDialog("Do you really want to delete the "
                        + "article \"" + this.displayedArticle.get().getArticleSummary().getTitle()
                        + "\" ? \nThis operation cannot be undone.", AppConfig.APP_TITLE)) {
                    try {
                        if (AppConfig.getInstance().getDatabase()
                                .deleteArticleById(this.displayedArticle.get().getArticleSummary().getId())) {
                            // We need a good old clear selection...
                            // I might just make it just as if we had clicked "new".
                            this.setModified(false);
                            this.setNewArticle(false);
                            this.buttonNewArticleAction(new ActionEvent(this.buttonDeleteArticle, this.buttonNewArticle));
                            this.loadArticleList(null);
                        } else {
                            UIUtils.warningAlert("For some reason nothing was deleted. This item might already be deleted.", AppConfig.APP_TITLE);
                        }
                    } catch (SQLException ex) {
                        UIUtils.errorAlert("Could not delete article, database error - " + ex.getMessage(), "Database error");
                    }
                }
            } else {
                UIUtils.warningAlert("There is nothing to delete here (??)", AppConfig.APP_TITLE);
            }
        }
    }
    
    @FXML
    private synchronized void menuItemArticIeIDAction(ActionEvent event) {
        // Changing the article ID is kinda weird but that's how I can reorder my
        // articles to be published, since the website backend uses the ID to order
        // the articles.
        long previousId = this.displayedArticle.get().getArticleSummary().getId();
        if (this.displayedArticle.get() != null && 
                previousId >= 0) {
            String newId = UIUtils.inputDialog("Enter new (unused) ID: \n"
                    + "(This is advanced stuff alright) \n"
                    + "The articles won't be re-ordered \n"
                    + "unless you reload the list from \n"
                    + "the Files menu", "Change article ID", 
                    Long.toString(previousId));
            if (newId != null) {
                try {
                    long newIdL = Long.parseLong(newId);
                    // Check if it already exists:
                    Article art = AppConfig.getInstance().getDatabase().getArticleById(newIdL);
                    if (art == null) {
                        if (AppConfig.getInstance().getDatabase().changeArticleId(previousId, newIdL)) {
                            // If the query succeeded we need to update the item in the article list.
                            // Although modifying displayedArticle could suffice.
                            // No it doesn't I guess that's because it's not a bound property thing.
                            // If I reload the article list, I'm going to have to re-select the
                            // article, which will cancel all changes.
                            //this.loadArticleList(this.displayedArticle.get().getArticleSummary());
                            int index = this.listViewArticles.getItems().indexOf(displayedArticle.get().getArticleSummary());
                            if (index >= 0) {
                                this.listViewArticles.getItems().get(index).setId(newIdL);
                            }
                            this.displayedArticle.get().getArticleSummary().setId(newIdL);
                            this.labelStatus.setText("Article ID changed to " + Long.toString(newIdL));
                        }
                    } else {
                        UIUtils.infoAlert("This article ID already exists.", AppConfig.APP_TITLE);
                    }
                } catch (NumberFormatException ex) {
                    UIUtils.warningAlert("Invalid new ID.", "New Article ID");
                } catch (SQLException ex) {
                    UIUtils.errorAlert("Could not change the id, database error: " + ex.getMessage(), 
                            "Database error");
                }
            }
        }
    }
    
    @FXML
    private synchronized void menuItemReloadArticleListAction(ActionEvent event) {
        this.loadArticleList(null);
    }
    
    @FXML
    private void menuItemInsertPAction(ActionEvent event) {
        UIUtils.surroundSelectionWithBloc(this.lastFocusedTextArea, 
                BlocsGenerator.generateParagraphBefore(), 
                BlocsGenerator.generateParagraphAfter());
        this.lastFocusedTextArea.requestFocus();
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
        return this.newArticle.get();
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

    public boolean isMandatoryFieldsNotFilled() {
        return mandatoryFieldsNotFilled.get();
    }

    public void setMandatoryFieldsNotFilled(boolean mandatoryFieldsNotFilled) {
        this.mandatoryFieldsNotFilled.set(mandatoryFieldsNotFilled);
    }
    
    public BooleanProperty mandatoryFieldsNotFilledProperty() {
        return this.mandatoryFieldsNotFilled;
    }

    /**
     * @return the lastFocusedTextArea
     */
    public TextArea getLastFocusedTextArea() {
        return lastFocusedTextArea;
    }

    /**
     * @param lastFocusedTextArea the lastFocusedTextArea to set
     */
    public void setLastFocusedTextArea(TextArea lastFocusedTextArea) {
        this.lastFocusedTextArea = lastFocusedTextArea;
    }
    
}
