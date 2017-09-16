/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dkvz.BlogAuthoring.model;

import java.text.*;
import java.util.*;

public class Comment {

    private String comment;
    private long id = -1;
    private String author;
    private long articleId;
    private Date date;
    private String clientIP;

    public Map<String, Object> toReducedMap() {
        Map<String, Object> res = new HashMap<String, Object>();
        if (this.id >= 0) {
            res.put("id", Long.toString(this.getId()));
        }
        res.put("author", this.getAuthor());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ssZ");
        String formatted = dateFormat.format(this.getDate());
        res.put("date", formatted);
        res.put("comment", this.getComment());
        return res;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

}
