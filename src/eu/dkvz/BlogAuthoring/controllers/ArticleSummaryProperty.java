/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dkvz.BlogAuthoring.controllers;

import eu.dkvz.BlogAuthoring.model.*;
import java.util.List;
import javafx.beans.property.*;
import javafx.collections.*;

/**
 * This class is not a property in the JavaFX sense
 * It has JavaFX properties though
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
    // I need the tags as an observable list:
    private final ListProperty<ArticleTag> tags = new SimpleListProperty();
    
    public ArticleSummaryProperty() {
        super();
        this.idProperty.set(-1);
        this.tags.set(FXCollections.observableArrayList());
    }
    
    public ArticleSummaryProperty(ArticleSummary sum) {
        super();
        this.setArticleURL(sum.getArticleURL());
        super.setAuthor(sum.getAuthor());
        super.setCommentsCount(sum.getCommentsCount());
        super.setDate(sum.getDate());
        this.setId(sum.getId());
        this.setPublished(sum.isPublished());
        this.setSummary(sum.getSummary());
        this.setTags(sum.getTags());
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
    
    @Override
    public final List<ArticleTag> getTags() {
        return this.tags;
    }
    
    @Override
    public final void setTags(List<ArticleTag> tags) {
        this.tags.set(FXCollections.observableList(tags));
    }
    
    public ListProperty<ArticleTag> tagsProperty() {
        return this.tags;
    }
    
}
