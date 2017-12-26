package com.example.xyzreader.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by V1 on 26-Dec-17.
 */

public class ItemModel {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("title")
    @Expose
    private String  title;

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getThumb() {
        return thumb;
    }

    public String getPhoto() {
        return photo;
    }

    public String getAspect_ratio() {
        return aspect_ratio;
    }

    public String getPublished_date() {
        return published_date;
    }

    @SerializedName("body")
    @Expose

    private String  body;
    @SerializedName("thumb")
    @Expose
    private String  thumb;
    @SerializedName("photo")
    @Expose
    private String  photo;
    @SerializedName("aspect_ratio")
    @Expose
    private String  aspect_ratio;
    @SerializedName("published_date")
    @Expose
    private String  published_date;


}
