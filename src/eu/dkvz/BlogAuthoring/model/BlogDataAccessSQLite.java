/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dkvz.BlogAuthoring.model;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 *
 * @author william
 */
public class BlogDataAccessSQLite extends BlogDataAccess {

    private String filename;
    private Connection connection = null;

    public BlogDataAccessSQLite() {
        this.filename = "db.sqlite";
    }
    
    public BlogDataAccessSQLite(String filename) {
        this.filename = filename;
    }
    
    @Override
    public void connect() throws SQLException {
        // The DriverManager will create the file if it doesn't exist.
        // I'm throwing an IOException in that case.
        this.connection = DriverManager.getConnection("jdbc:sqlite:".concat(filename));
    }

    @Override
    public void disconnect() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isConnected() throws SQLException {
        if (this.connection != null) {
            if (!this.connection.isClosed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public User getUser(long id) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ArticleSummary> getArticleSummariesDescFromTo(long start, int count, String tags) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ArticleTag> getAllTags() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getCommentCount(long articleID) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getArticleCount(boolean published, String tags) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Article getArticleById(long id) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void insertArticle(Article article) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateArticle(Article article) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteArticleById(long id) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
}
