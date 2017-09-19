/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dkvz.BlogAuthoring.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArticleTag {

    private String name;
    private long id;
    private boolean mainTag;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isMainTag() {
        return mainTag;
    }

    public void setMainTag(boolean mainTag) {
        this.mainTag = mainTag;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof ArticleTag) {
            return (((ArticleTag) other).getId() == this.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> res = new HashMap<String, Object>();
        // My app expects camel case syntax. Not sure if that's standard.
        res.put("id", Long.toString(this.getId()));
        res.put("name", this.getName());
        if (this.isMainTag()) {
            res.put("mainTag", Integer.toString(1));
        } else {
            res.put("mainTag", Integer.toString(0));
        }
        return res;
    }
    
    @Override
    public String toString() {
        return this.getName();
    }

}
