package ru.vpcb.popularmovie;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class ReviewItem implements Parcelable{
    private static final String KEY_ID = "id";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_URL = "url";

    private String id;
    private String author;
    private String content;
    private String url;
    private boolean valid;

    public ReviewItem(JSONObject json) {
        valid = parser(json);
    }

    public ReviewItem() {  // empty
        id = "unknown";
        author = "";
        content = "no reviews";
        url = "";
        valid = true;
    }

    private ReviewItem(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
        valid = in.readByte() != 0;
    }

    public static final Creator<ReviewItem> CREATOR = new Creator<ReviewItem>() {
        @Override
        public ReviewItem createFromParcel(Parcel in) {
            return new ReviewItem(in);
        }

        @Override
        public ReviewItem[] newArray(int size) {
            return new ReviewItem[size];
        }
    };

    private boolean parser(JSONObject json) {
        try {
            id = json.getString(KEY_ID);
            author = json.getString(KEY_AUTHOR);
            content = json.getString(KEY_CONTENT);
            url = json.getString(KEY_URL);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public boolean isValid() {
        return valid;
    }

    public String getId() {
        return id;
    }

    String getAuthor() {
        return author;
    }

    String getContent() {
        return content;
    }

    String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(url);
        parcel.writeByte((byte) (valid ? 1 : 0));
    }
}
