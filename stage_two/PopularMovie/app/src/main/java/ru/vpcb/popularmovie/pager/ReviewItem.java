package ru.vpcb.popularmovie.pager;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import static ru.vpcb.popularmovie.utils.Constants.KEY_AUTHOR;
import static ru.vpcb.popularmovie.utils.Constants.KEY_CONTENT;
import static ru.vpcb.popularmovie.utils.Constants.KEY_ID;
import static ru.vpcb.popularmovie.utils.Constants.KEY_URL;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class ReviewItem implements Parcelable {
    private String id;
    private String author;
    private String content;
    private String url;
    private final boolean valid;

    /**
     * Constructor  from JSON data object
     * Uses parser() method to fill in fields of the object
     *
     * @param json data source JSON object
     */
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

    /**
     * Parcel Creator
     */
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

    /**
     * Parser for ReviewItem object
     * Parses fields of JSON object and fills fields of current ReviewItem object
     * Returns the result of operation
     *
     * @param json input JSON object
     * @return true if completed successfully, false otherwise
     */
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

    /**
     * Returns flag of validity of created object
     *
     * @return true valid field value
     */
    public boolean isValid() {
        return valid;
    }

    // standard getters
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

    /**
     * Parcel method
     *
     * @return 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Parcel writer
     * Store fields of the object to parcel object
     *
     * @param parcel store object
     * @param i      parameters
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(url);
        parcel.writeByte((byte) (valid ? 1 : 0));
    }
}
