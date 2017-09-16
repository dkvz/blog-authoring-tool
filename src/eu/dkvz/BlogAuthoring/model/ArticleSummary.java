/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dkvz.BlogAuthoring.model;

import java.text.*;
import java.util.*;

public class ArticleSummary {
	
	/**
	 * This is an imaginary ID, ordered from 1 to X
	 * to serve as an abstract ordering mechanism for the articles.
	 */
	private long id = -1;
	private String title;
	private String thumbImage;
	private String articleURL;
	private List<ArticleTag> tags;
	private Date date;
	private String summary;
	private String author;
	private long commentsCount = 0;
	
	public ArticleSummary() {
		this.tags = new ArrayList<ArticleTag>();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getThumbImage() {
		return thumbImage;
	}

	public void setThumbImage(String thumbImage) {
		this.thumbImage = thumbImage;
	}

	public List<ArticleTag> getTags() {
		return tags;
	}

	public void setTags(List<ArticleTag> tags) {
		this.tags = tags;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public long getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(long commentsCount) {
		this.commentsCount = commentsCount;
	}

	public String getArticleURL() {
		return articleURL;
	}

	public void setArticleURL(String articleURL) {
		this.articleURL = articleURL;
	}
	
	public Map<String, Object> toMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		// My app expects camel case syntax. Not sure if that's standard.
		res.put("id", Long.toString(this.getId()));
		res.put("title", this.getTitle());
		res.put("thumbImage", this.getThumbImage());
		// Let's format the date:
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ssZ");
		String formatted = dateFormat.format(this.getDate());
		res.put("date", formatted);
		res.put("summary", this.getSummary());
		res.put("author", this.getAuthor());
		res.put("commentsCount", Long.toString(this.getCommentsCount()));
		res.put("articleURL", this.getArticleURL());
		res.put("tags", this.getTags());
		return res;
	}
	
}
