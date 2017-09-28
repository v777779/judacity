package ru.vpcb.rgdownload;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.vpcb.rgdownload.utils.ParseUtils;

/**
 * Created by V1 on 28-Sep-17.
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

    protected ReviewItem(Parcel in) {
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

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
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
