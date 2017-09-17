/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dkvz.BlogAuthoring.model;

import java.sql.*;
import java.util.*;

/**
 *
 * @author william
 */
public abstract class BlogDataAccess {
    
    public abstract void connect() throws SQLException;
    public abstract void disconnect() throws SQLException;
    public abstract boolean isConnected() throws SQLException;
    public abstract User getUser(long id) throws SQLException;
    public abstract List<ArticleSummary> getArticleSummariesDescFromTo(long start, int count, String tags) throws SQLException;
    public abstract List<ArticleTag> getAllTags() throws SQLException;
    public abstract long getCommentCount(long articleID) throws SQLException;
    public abstract long getArticleCount(boolean published, String tags) throws SQLException;
    public abstract Article getArticleById(long id, boolean tags) throws SQLException;
    public abstract void insertArticle(Article article) throws SQLException;
    public abstract void updateArticle(Article article) throws SQLException;
    public abstract void deleteArticleById(long id) throws SQLException;
    
}
