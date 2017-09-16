/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dkvz.BlogAuthoring.model;

import java.util.*;

public class Article {
	
	private ArticleSummary articleSummary = null;
	private String content;
	
	public Article() {
            this.articleSummary = new ArticleSummary();
	}
	
	public Article(ArticleSummary articleSummary) {
            this.articleSummary = articleSummary;
	}

	public ArticleSummary getArticleSummary() {
            return articleSummary;
	}

	public void setArticleSummary(ArticleSummary articleSummary) {
            this.articleSummary = articleSummary;
	}

	public String getContent() {
            return content;
	}

	public void setContent(String content) {
            this.content = content;
	}
	
	public Map<String, Object> toMap() {
            Map<String, Object> res = this.getArticleSummary().toMap();
            res.put("content", this.getContent());
            return res;
	}
}