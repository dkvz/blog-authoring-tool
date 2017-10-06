/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dkvz.BlogAuthoring.utils;

/**
 *
 * @author William
 */
public class BlocsGenerator {
    
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_RIGHT = 1;
    public static final int ALIGN_CENTER = 2;
    
    private String imageBaseURL = null;
    
    public BlocsGenerator() {
        
    }
    
    public BlocsGenerator(String imageBaseURL) {
        this.imageBaseURL = imageBaseURL;
    }
    
    /**
     * Generates code for an image in the blog
     * @param imageURL the URL of the image
     * @param miniatureURL the URL of the miniature
     * @param altText what to put in the alt property of the img tag
     * @param width image width in pixel without "px" e.g. "600"
     * @param height image height in pixel without "px" e.g. "600"
     * @param legend if not null, adds this legend to the image
     * @param alignment one of the BlogsGenerator.ALIGN_* values
     * @return the code bloc as String
     */
    public String generateImageCode(String imageURL, String miniatureURL, 
            String altText, String width, String height, String legend, 
            int alignment) {
        StringBuilder sb = new StringBuilder("<div class=\"articleImage ");
        switch(alignment) {
            case BlocsGenerator.ALIGN_LEFT:
                sb.append("alignLeft");
                break;
            case BlocsGenerator.ALIGN_CENTER:
                sb.append("centerImage");
                break;
            case BlocsGenerator.ALIGN_RIGHT:
                sb.append("alignRight");
                break;
        }
        sb.append("\" style=\"width: ");
        sb.append(width);
        sb.append("px\">\n<a href=\"");
        sb.append(this.addBaseURL(imageURL));
        sb.append("\" target=\"_blank\"><img");
        if (altText != null && !altText.isEmpty()) {
            sb.append(" alt=\"");
            sb.append(altText);
            sb.append("\"");
        }
        sb.append(" src=\"");
        if (miniatureURL != null && !miniatureURL.isEmpty()) {
            sb.append(this.addBaseURL(miniatureURL));
        } else {
            sb.append(this.addBaseURL(imageURL));
        }
        sb.append("\" width=\"");
        sb.append(width);
        sb.append("\" height=\"");
        sb.append(height);
        sb.append("\" /></a>\n");
        if (legend != null && !legend.isEmpty()) {
            sb.append("<div class=\"articleImageLegend\">");
            sb.append(legend);
            sb.append("</div>\n");
        }
        sb.append("</div>");
        return sb.toString();
    }
    
    // Super cringy methods in here
    public String generateQuoteBefore() {
        return "<blockquote>\n";
    }
    
    public String generateQuoteAfter() {
        return "\n</blockquote>";
    }
    
    public String generateCodeBefore() {
        return "<pre class=\"screen\">\n";
    }
    
    public String generateCodeAfter() {
        return "\n</pre>";
    }
    
    public String generateAnchorBefore(String href, boolean blank) {
        if (href == null) href = "";
        StringBuilder sb = new StringBuilder("<a href=\"");
        sb.append(href);
        sb.append("\"");
        if (blank) {
            sb.append(" target=\"_blank\"");
        }
        sb.append(">");
        return sb.toString();
    }
    
    public String generateAnchorAfter() {
        return "</a>";
    }

    private String addBaseURL(String value) {
        if (this.getImageBaseURL() != null && !this.getImageBaseURL().isEmpty()) {
            return this.getImageBaseURL().concat(value);
        } else {
            return value;
        }
    }
    
    /**
     * @return the imageBaseURL
     */
    public String getImageBaseURL() {
        return imageBaseURL;
    }

    /**
     * @param imageBaseURL the imageBaseURL to set
     */
    public void setImageBaseURL(String imageBaseURL) {
        this.imageBaseURL = imageBaseURL;
    }
    
}
