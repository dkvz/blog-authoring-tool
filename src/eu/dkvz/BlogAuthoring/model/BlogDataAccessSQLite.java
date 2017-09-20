/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dkvz.BlogAuthoring.model;

import java.sql.*;
import java.util.*;

/**
 * I'm using arrays to store stuff with Long ids, we could technically
 * get more items than allowed in an array if for some reasons there
 * are millions of articles in the database and you use the getAll* 
 * call.
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
        List<ArticleSummary> res = new ArrayList<>();
        if (start < 0) {
            start = 0;
        }
        // I could do this in a single statement but going to do it in two.
        // I'm using limit and offset, which are supported by PostgreSQL and MySQL (normally) but
        // not most other databases.
        String sql = "SELECT articles.id, articles.title, "
                + "articles.article_url, articles.thumb_image, articles.date, "
                + "articles.user_id, articles.summary, articles.published FROM articles ";
        String[] tagsA = null;
        if (tags != null && !tags.isEmpty()) {
            sql = sql.concat(", article_tags, tags WHERE");
            boolean firstAnd = true;
            tagsA = tags.split(",");
            for (int a = 0; a < tagsA.length; a++) {
                if (firstAnd) {
                    sql = sql.concat(" tags.name = ?");
                    firstAnd = false;
                } else {
                    sql = sql.concat(" AND tags.name = ?");
                }
            }
            // Adding the join code:
            if (!firstAnd) {
                sql = sql.concat(" AND");
            }
            sql = sql.concat(" (tags.id = article_tags.tag_id AND "
                    + "article_tags.article_id = articles.id) ");
        }
        if (count < 1) {
            sql = sql.concat("ORDER BY articles.id DESC");
        } else {
            sql = sql.concat("ORDER BY articles.id DESC LIMIT ? OFFSET ?");
        }

        PreparedStatement stmt = connection.prepareStatement(sql);
        int pos = 0;
        if (tagsA != null && tagsA.length > 0) {
            for (pos = 0; pos < tagsA.length; pos++) {
                stmt.setString(pos + 1, tagsA[pos]);
            }
        }
        if (count >= 1) {
            stmt.setInt(pos + 1, count); // LIMIT clause value
            stmt.setLong(pos + 2, start); // OFFSET is start
        }
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
        User usr = null;
        try {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
            st.setInt(1, rset.getInt("user_id"));
            st.setMaxRows(1);
            ResultSet usrSet = st.executeQuery();
            if (usrSet.next()) {
                author = usrSet.getString("name");
                usr = new User();
                usr.setName(author);
                usr.setId(usrSet.getInt("id"));
            }
            st.close();
        } catch (SQLException ex) {
            // Do nothing, author already set to Anonymous by default.
            System.err.println("Subquery error parsing article: ".concat(ex.getMessage()));
        }
        sum.setAuthor(author);
        sum.setUser(usr);
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
        if (rset.getInt("published") > 0) {
            sum.setPublished(true);
        } else {
            sum.setPublished(false);
        }
        // Get the tags.
        List<ArticleTag> artTags = new ArrayList<>();
        try {
            artTags = this.getTagsForArticle(sum.getId());
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
            // TODO: I should use StringBuilder here instead
            // of recreating a String at every concat.
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
        Article ret = null;
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM articles WHERE id = ?")) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ArticleSummary sum = this.getArticleSummaryFromRset(rs, connection);
                ret = new Article();
                ret.setArticleSummary(sum);
                ret.setContent(rs.getString("content"));
            }
            rs.close();
        }
        return ret;
    }

    @Override
    public boolean insertArticle(Article article) throws SQLException {
        // We need to update the id property to reflect the ID of the newly added article.
        // We're not doing any check to mandatory stuff. Higher level should do that.
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO articles (title, "
                + "article_url, "
                + "thumb_image, "
                + "date, "
                + "user_id, "
                + "summary, "
                + "content, "
                + "published) VALUES(?,?,?,?,?,?,?,?)");
        stmt.setString(1, article.getArticleSummary().getTitle());
        stmt.setString(2, article.getArticleSummary().getArticleURL());
        stmt.setString(3, article.getArticleSummary().getThumbImage());
        // If no date is set, use today.
        if (article.getArticleSummary().getDate() == null) {
            article.getArticleSummary().setDate(new java.util.Date());
        }
        // Date is a timestamp in the database:
        stmt.setLong(4, article.getArticleSummary().getDate().getTime() / 1000);
        stmt.setLong(5, article.getArticleSummary().getUser().getId());
        stmt.setString(6, article.getArticleSummary().getSummary());
        stmt.setString(7, article.getContent());
        // Published is stored as an int.
        if (article.getArticleSummary().isPublished()) {
            stmt.setInt(8, 1);
        } else {
            stmt.setInt(8, 0);
        }
        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            return false;
        }
         try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                article.getArticleSummary().setId(generatedKeys.getLong(1));
            } else {
                return false;
            }
         }
        // Insert the tags if previous query was successful.
        // At this point we return true for success anyway.
        if (article.getArticleSummary().getTags() != null && 
                article.getArticleSummary().getTags().size() > 0) {
            PreparedStatement tagsStmt = connection.prepareStatement("INSERT INTO "
                    + "article_tags(article_id, tag_id) VALUES(?,?)");
            for (ArticleTag tag: article.getArticleSummary().getTags()) {
                tagsStmt.setLong(1, article.getArticleSummary().getId());
                tagsStmt.setLong(2, tag.getId());
                tagsStmt.addBatch();
            }
            tagsStmt.executeBatch();
        }
        return true;
    }

    @Override
    public boolean updateArticle(Article article) throws SQLException {
        // Do note that we are updating date too.
        PreparedStatement stmt = connection.prepareStatement("UPDATE articles SET "
                + "title = ?, "
                + "article_url = ?, "
                + "thumb_image = ?, "
                + "date = ?, "
                + "user_id = ?, "
                + "summary = ?, "
                + "content = ?, "
                + "published = ? "
                + "WHERE id = ?");
        stmt.setString(1, article.getArticleSummary().getTitle());
        stmt.setString(2, article.getArticleSummary().getArticleURL());
        stmt.setString(3, article.getArticleSummary().getThumbImage());
        // If no date is set, use today.
        if (article.getArticleSummary().getDate() == null) {
            article.getArticleSummary().setDate(new java.util.Date());
        }
        // Date is a timestamp in the database:
        stmt.setLong(4, article.getArticleSummary().getDate().getTime() / 1000);
        stmt.setLong(5, article.getArticleSummary().getUser().getId());
        stmt.setString(6, article.getArticleSummary().getSummary());
        stmt.setString(7, article.getContent());
        // Published is stored as an int.
        if (article.getArticleSummary().isPublished()) {
            stmt.setInt(8, 1);
        } else {
            stmt.setInt(8, 0);
        }
        stmt.setLong(9, article.getArticleSummary().getId());
        int rows = stmt.executeUpdate();
        if (rows == 0) {
            // Nothing was updated.
            return false;
        }
        // Update tags:
        // We need to get the current tags first.
        // We're not doing any checks as if these tags actually exist and all that.
        List<ArticleTag> currentTags = this.getTagsForArticle(article.getArticleSummary().getId());
        if (currentTags != null && currentTags.size() > 0) {
            // We had tags before.
            for (ArticleTag tag: currentTags) {
                if (!article.getArticleSummary().getTags().contains(tag)) {
                    // Remove this tag (for this article, not for everything OKAY):
                    PreparedStatement deleteTag = connection.prepareStatement("DELETE FROM "
                            + "article_tags WHERE article_id = ? AND tag_id = ?");
                    deleteTag.setLong(1, article.getArticleSummary().getId());
                    deleteTag.setLong(2, tag.getId());
                    deleteTag.executeUpdate();
                }
            }
            // Insert all the rest that is not already there:
            for (ArticleTag tag: article.getArticleSummary().getTags()) {
                if (!currentTags.contains(tag)) {
                    // Insert.
                    PreparedStatement insertTag = connection.prepareStatement("INSERT INTO "
                            + "article_tags (article_id, tag_id) VALUES(?,?)");
                    insertTag.setLong(1, article.getArticleSummary().getId());
                    insertTag.setLong(2, tag.getId());
                    insertTag.executeUpdate();
                }
            }
        }
        return true;
    }

    @Override
    public boolean deleteArticleById(long id) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM articles WHERE id = ?");
        stmt.setLong(1, id);
        int affected = stmt.executeUpdate();
        return !(affected == 0);
    }

    @Override
    public List<ArticleTag> getTagsForArticle(long id) throws SQLException {
        List<ArticleTag> ret = new ArrayList<>();
        try (PreparedStatement st = connection.prepareStatement("SELECT tags.name, tags.id, "
                + "tags.main_tag FROM article_tags, tags "
                + "WHERE article_tags.article_id = ? AND article_tags.tag_id = tags.id")) {
            st.setLong(1, id);
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
                ret.add(t);
            }
        }
        return ret;
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
