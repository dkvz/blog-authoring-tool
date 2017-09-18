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
public class ArticleProperty extends Article {
    
    private final ArticleSummaryProperty articleSummaryProperty;
    private final StringProperty contentProperty = new SimpleStringProperty();

    public ArticleProperty() {
        articleSummaryProperty = new ArticleSummaryProperty();
    }
    
    public ArticleProperty(Article article) {
        articleSummaryProperty = new ArticleSummaryProperty(article.getArticleSummary());
        this.setContent(article.getContent());
    }
    
    @Override
    public ArticleSummaryProperty getArticleSummary() {
        return this.articleSummaryProperty;
    }
    
    /**
     * @return the articleSummaryProperty
     */
    public ArticleSummaryProperty getArticleSummaryProperty() {
        return articleSummaryProperty;
    }

    
    @Override
    public final String getContent() {
        return this.contentProperty.get();
    }
    
    @Override
    public final void setContent(String content) {
        this.contentProperty.set(content);
        super.setContent(content);
    }
    
    /**
     * @return the contentProperty
     */
    public StringProperty contentProperty() {
        return contentProperty;
    }
    
    @Override
    public String toString() {
        if (this.getArticleSummary().getTitle() != null) {
            return this.getArticleSummary().getTitle();
        } else {
            return "Untitled article";
        }
    }
    
    
    
}
