package ru.vpcb.footballassistant.news;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static ru.vpcb.footballassistant.utils.Config.EMPTY_DATE;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_LONG_DASH;
import static ru.vpcb.footballassistant.utils.Config.EMPTY_DASH;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 11-Feb-18
 * Email: vadim.v.voronov@gmail.com
 */
public class NDArticle {

    @SerializedName("source")
    @Expose
    private NDSource source;

    @SerializedName("author")
    @Expose
    private String author;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("urlToImage")
    @Expose
    private String urlToImage;

    @SerializedName("publishedAt")
    @Expose
    private String publishedAt;

    public NDArticle(NDSource source, String author, String title, String description,
                     String url, String urlToImage, String publishedAt) {

        this.source = source;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
    }

    public NDSource getSource() {
        return source;
    }

    public String getAuthor() {
        if(author == null || author.isEmpty()) return EMPTY_LONG_DASH;
        return author;
    }

    public String getTitle() {
        if(title == null || title.isEmpty()) return EMPTY_LONG_DASH;
        return title;
    }

    public String getDescription() {
        if(description == null || description.isEmpty()) return EMPTY_LONG_DASH;
        return description;
    }

    public String getUrl() {
        if(url == null || url.isEmpty()) return EMPTY_DASH;
        return url;
    }

    public String getUrlToImage() {
        if(urlToImage == null || urlToImage.isEmpty()) return EMPTY_DASH;
        return urlToImage;
    }

    public String getPublishedAt() {
        if(publishedAt == null || publishedAt.isEmpty()) return EMPTY_DATE;
        return publishedAt;
    }
}
