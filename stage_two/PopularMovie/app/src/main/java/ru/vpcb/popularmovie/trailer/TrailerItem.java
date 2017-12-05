package ru.vpcb.popularmovie.trailer;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import static ru.vpcb.popularmovie.utils.Constants.*;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 23-Oct-17
 * Email: vadim.v.voronov@gmail.com
 */


public class TrailerItem implements Parcelable {
    private String id;
    private String iso639;
    private String iso3166;
    private String key;
    private String name;
    private String site;
    private int size;
    private String type;
    private final boolean valid;

    public TrailerItem(JSONObject json) {
        valid = parser(json);
    }

    public TrailerItem() {  // empty
        id = "unknown";
        name = "no trailer";
        valid = true;
    }

    private TrailerItem(Parcel in) {
        id = in.readString();
        iso639 = in.readString();
        iso3166 = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
        size = in.readInt();
        type = in.readString();
        valid = in.readByte() != 0;
    }

    /**
     * Parcel Creator
     */
    public static final Creator<TrailerItem> CREATOR = new Creator<TrailerItem>() {
        @Override
        public TrailerItem createFromParcel(Parcel in) {
            return new TrailerItem(in);
        }

        @Override
        public TrailerItem[] newArray(int size) {
            return new TrailerItem[size];
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
            iso639 = json.getString(KEY_ISO639);
            iso3166 = json.getString(KEY_ISO639);
            key = json.getString(KEY_CODE);
            name = json.getString(KEY_NAME);
            site = json.getString(KEY_SITE);
            size = json.getInt(KEY_SIZE);
            type = json.getString(KEY_TYPE);
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

    public String getIso639() {
        return iso639;
    }

    public String getIso3166() {
        return iso3166;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getWebLink() {
        if (!site.toLowerCase().equals(KEY_YOUTUBE) || key == null || key.isEmpty()) {
            return null;
        }
        return YOUTUBE_MOVIE_BASE + key;
    }
    public String getAppLink() {
        if (!site.toLowerCase().equals(KEY_YOUTUBE) || key == null || key.isEmpty()) {
            return null;
        }
        return YOUTUBE_APP_BASE + key;
    }



    public String getPoster() {
        if (!site.toLowerCase().equals(KEY_YOUTUBE) || key == null || key.isEmpty()) {
            return null;
        }
        return YOUTUBE_POSTER_BASE + key + "/"+ YOUTUBE_DEFAULT_IMAGE;
    }


    public int getSize() {
        return size;
    }

    public String getType() {
        return type;
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
        parcel.writeString(iso639);
        parcel.writeString(iso3166);
        parcel.writeString(key);
        parcel.writeString(name);
        parcel.writeString(site);
        parcel.writeInt(size);
        parcel.writeString(type);
        parcel.writeByte((byte) (valid ? 1 : 0));
    }
}
