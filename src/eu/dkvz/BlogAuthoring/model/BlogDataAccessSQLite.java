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
        User usr = null;
        PreparedStatement stmt = connection.prepareStatement("SELECT name FROM users WHERE id = ?");
        stmt.setLong(1, id);
        ResultSet rset = stmt.executeQuery();
        if (rset.next()) {
            usr = new User();
            usr.setId(id);
            usr.setName(rset.getString("name"));
        }
        rset.close();
        return usr;
    }

    @Override
    public List<ArticleSummary> getArticleSummariesDescFromTo(long start, int count, String tags) throws SQLException {
        List<ArticleSummary> res = new ArrayList<ArticleSummary>();
        if (start < 0) {
            start = 0;
        }
        // I could do this in a single statement but going to do it in two.
        // I'm using limit and offset, which are supported by PostgreSQL and MySQL (normally) but
        // not most other databases.
        String sql = "SELECT articles.id, articles.title, "
                + "articles.article_url, articles.thumb_image, articles.date, "
                + "articles.user_id, articles.summary FROM articles ";
        String[] tagsA = null;
        if (tags != null && !tags.isEmpty()) {
            sql = sql.concat(", article_tags, tags WHERE articles.published = '1'");
            tagsA = tags.split(",");
            for (int a = 0; a < tagsA.length; a++) {
                sql = sql.concat(" AND tags.name = ?");
            }
            // Adding the join code:
            sql = sql.concat(" AND (tags.id = article_tags.tag_id AND "
                    + "article_tags.article_id = articles.id) ");
        } else {
            sql = sql.concat("WHERE articles.published = '1' ");
        }
        sql = sql.concat("ORDER BY articles.id DESC LIMIT ? OFFSET ?");

        PreparedStatement stmt = connection.prepareStatement(sql);
        int pos = 0;
        if (tagsA != null && tagsA.length > 0) {
            for (pos = 0; pos < tagsA.length; pos++) {
                stmt.setString(pos + 1, tagsA[pos]);
            }
        }
        stmt.setInt(pos + 1, count); // LIMIT clause value
        stmt.setLong(pos + 2, start); // OFFSET is start
        ResultSet rset = stmt.executeQuery();
        if (rset != null) {
            // For SQLite the date is an integer (or a long I suppose).
            while (rset.next()) {
                ArticleSummary sum = this.getArticleSummaryFromRset(rset, connection);
                res.add(sum);
            }
        }
        stmt.close();
        return res;
    }

    private ArticleSummary getArticleSummaryFromRset(ResultSet rset, Connection conn) throws SQLException {
        ArticleSummary sum = new ArticleSummary();
        // Get the author:
        String author = "Anonymous";
        try {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
            st.setInt(1, rset.getInt("user_id"));
            st.setMaxRows(1);
            ResultSet usrSet = st.executeQuery();
            if (usrSet.next()) {
                author = usrSet.getString("name");
            }
            st.close();
        } catch (SQLException ex) {
            // Do nothing, author already set to Anonymous by default.
            ex.printStackTrace();
        }
        sum.setAuthor(author);
        sum.setId(rset.getLong("id"));
        // Find comment count:
        long comCount = this.getCommentCount(sum.getId());
        if (comCount < 0) {
            comCount = 0;
        }
        sum.setCommentsCount(comCount);
        long dateVal = rset.getLong("date");
        java.util.Date date = new java.util.Date(dateVal * 1000);
        sum.setDate(date);
        sum.setSummary(rset.getString("summary"));
        sum.setArticleURL(rset.getString("article_url"));
        sum.setThumbImage(rset.getString("thumb_image"));
        sum.setTitle(rset.getString("title"));
        // Get the tags.
        List<ArticleTag> artTags = new ArrayList<>();
        try {
            PreparedStatement st = conn.prepareStatement("SELECT tags.name, tags.id, tags.main_tag FROM article_tags, tags "
                    + "WHERE article_tags.article_id = ? AND article_tags.tag_id = tags.id");
            st.setLong(1, sum.getId());
            ResultSet tags = st.executeQuery();
            while (tags.next()) {
                ArticleTag t = new ArticleTag();
                t.setId(tags.getLong("id"));
                t.setName(tags.getString("name"));
                int mainT = tags.getInt("main_tag");
                if (mainT > 0) {
                    t.setMainTag(true);
                } else {
                    t.setMainTag(false);
                }
                artTags.add(t);
            }
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        sum.setTags(artTags);
        return sum;
    }

    @Override
    public List<ArticleTag> getAllTags() throws SQLException {
        List<ArticleTag> ret = null;
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM tags");
        ret = new ArrayList<>();
        while (rs.next()) {
            ArticleTag t = new ArticleTag();
            t.setId(rs.getLong("id"));
            t.setName(rs.getString("name"));
            int mainT = rs.getInt("main_tag");
            if (mainT > 0) {
                t.setMainTag(true);
            } else {
                t.setMainTag(false);
            }
            ret.add(t);
        }
        rs.close();

        return ret;
    }

    @Override
    public long getCommentCount(long articleID) throws SQLException {
        long ret = 0l;
        PreparedStatement stmt = connection.prepareStatement("SELECT count(*) FROM comments WHERE article_id = ?");
        stmt.setLong(1, articleID);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            ret = rs.getLong(1);
        }
        return ret;
    }

    @Override
    public long getArticleCount(boolean published, String tags) throws SQLException {
        long ret = 0l;
        PreparedStatement stmt = null;
        String sql = "SELECT count(*) FROM articles";
        ResultSet rs;
        String[] tagsA = null;
        if (tags != null && !tags.isEmpty()) {
            tagsA = tags.split(",");
            sql = sql.concat(", tags, article_tags");
            if (published) {
                sql = sql.concat(" WHERE articles.published = 1");
            }
            boolean where = false;
            for (int a = 0; a < tagsA.length; a++) {
                if (!where && !published) {
                    sql = sql.concat(" WHERE");
                    where = true;
                } else {
                    sql = sql.concat(" AND");
                }
                sql = sql.concat(" tags.name = ?");
            }
            // Adding the join code:
            sql = sql.concat(" AND (tags.id = article_tags.tag_id AND "
                    + "article_tags.article_id = articles.id)");
        } else {
            if (published) {
                sql = sql.concat(" WHERE articles.published = 1");
            }
        }

        stmt = connection.prepareStatement(sql);
        // Set the parameters of the statement:
        if (tagsA != null) {
            for (int i = 1; i <= tagsA.length; i++) {
                // I originally meant the list to use long as index
                // but arrays use int, so yeah... I'm using int.
                // F*** consistency.
                stmt.setString(i, tagsA[i - 1]);
            }
        }

        // Set rs somewhere around here
        rs = stmt.executeQuery();
        if (rs.next()) {
            ret = rs.getLong(1);
        }

        return ret;
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
