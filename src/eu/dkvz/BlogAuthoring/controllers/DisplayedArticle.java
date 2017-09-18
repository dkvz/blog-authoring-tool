/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dkvz.BlogAuthoring.controllers;

import eu.dkvz.BlogAuthoring.model.*;
import javafx.beans.property.*;

/**
 *
 * @author Alain
 */
public class DisplayedArticle {
    
    private final ObjectProperty<ArticleProperty> displayedArticle;
    private final BooleanProperty newArticle;

    public DisplayedArticle() {
        this.displayedArticle = new SimpleObjectProperty<>();
        this.displayedArticle.setValue(null);
        this.newArticle = new SimpleBooleanProperty();
        this.newArticle.setValue(false);
    }
    
    /**
     * @return the displayedArticle
     */
    public final ArticleProperty getDisplayedArticle() {
        return displayedArticle.getValue();
    }

    /**
     * @param displayedArticle the displayedArticle to set
     */
    public final void setDisplayedArticle(ArticleProperty displayedArticle) {
        this.displayedArticle.setValue(displayedArticle);
    }
    
    public ObjectProperty<ArticleProperty> displayedArticleProperty() {
        return this.displayedArticle;
    }
    
    public final boolean isNewArticle() {
        return newArticle.getValue();
    }
    
    public void setNewArticle(boolean newArticle) {
        this.newArticle.setValue(newArticle);
    }
    
    public BooleanProperty newArticleProperty() {
        return this.newArticle;
    }
    
}
