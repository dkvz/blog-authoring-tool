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
public class ArticleSummaryProperty extends ArticleSummary {
    
    private final LongProperty idProperty = new SimpleLongProperty();
    private final StringProperty titleProperty = new SimpleStringProperty();
    private final StringProperty thumbImageProperty = new SimpleStringProperty();
    private final StringProperty articleURLProperty = new SimpleStringProperty();
    // We're not converting the tags into properties here.
    // Not converting the date either.
    private final StringProperty summaryProperty = new SimpleStringProperty();
    // Author is a cosmetic field, not redefining as well.
    // Leaving User as it is as well.
    // Leaving commentsCount out, not used in our app ATM.
    private final BooleanProperty publishedProperty = new SimpleBooleanProperty();
    
    public ArticleSummaryProperty() {
        super();
        this.idProperty.set(-1);
    }
    
    public ArticleSummaryProperty(ArticleSummary sum) {
        this();
        this.setArticleURL(sum.getArticleURL());
        super.setAuthor(sum.getAuthor());
        super.setCommentsCount(sum.getCommentsCount());
        super.setDate(sum.getDate());
        this.setId(sum.getId());
        this.setPublished(sum.isPublished());
        this.setSummary(sum.getSummary());
        super.setTags(sum.getTags());
        this.setThumbImage(sum.getThumbImage());
        this.setTitle(sum.getTitle());
        super.setUser(sum.getUser());
    }
    
    @Override
    public final long getId() {
        return this.idProperty.get();
    }
    
    @Override
    public final void setId(long id) {
        super.setId(id);
        this.idProperty.set(id);
    }
    
    public LongProperty idProperty() {
        return this.idProperty;
    }
    
    @Override
    public final String getTitle() {
        return this.titleProperty.get();
    }
    
    @Override
    public final void setTitle(String title) {
        super.setTitle(title);
        this.titleProperty.set(title);
    }
    
    public StringProperty titleProperty() {
        return this.titleProperty;
    }
    
    @Override
    public final String getThumbImage() {
        return this.thumbImageProperty.get();
    }
    
    @Override
    public final void setThumbImage(String timage) {
        super.setThumbImage(timage);
        this.thumbImageProperty.set(timage);
    }
    
    public StringProperty thumbImageProperty() {
        return this.thumbImageProperty;
    }

    @Override
    public final String getArticleURL() {
        return this.articleURLProperty.get();
    }
    
    @Override
    public final void setArticleURL(String articleURL) {
        super.setArticleURL(articleURL);
        this.articleURLProperty.set(articleURL);
    }
    
    /**
     * @return the articleURLProperty
     */
    public StringProperty articleURLProperty() {
        return articleURLProperty;
    }

    @Override
    public final String getSummary() {
        return this.summaryProperty.get();
    }
    
    @Override
    public final void setSummary(String summary) {
        super.setSummary(summary);
        this.summaryProperty.set(summary);
    }
    
    /**
     * @return the summaryProperty
     */
    public StringProperty summaryProperty() {
        return summaryProperty;
    }

    @Override
    public final boolean isPublished() {
        return this.publishedProperty.get();
    }
    
    @Override
    public final void setPublished(boolean pub) {
        this.publishedProperty.set(pub);
        super.setPublished(pub);
    }
    
    /**
     * @return the publishedProperty
     */
    public BooleanProperty publishedProperty() {
        return publishedProperty;
    }
    
}
