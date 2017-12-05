package ru.vpcb.popularmovie;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import ru.vpcb.popularmovie.utils.ParseUtils;

import static ru.vpcb.popularmovie.utils.Constants.KEY_ADULT;
import static ru.vpcb.popularmovie.utils.Constants.KEY_BACKDROP_PATH;
import static ru.vpcb.popularmovie.utils.Constants.KEY_GENRE_IDS;
import static ru.vpcb.popularmovie.utils.Constants.KEY_ID;
import static ru.vpcb.popularmovie.utils.Constants.KEY_ORIGIN_LANG;
import static ru.vpcb.popularmovie.utils.Constants.KEY_ORIGIN_TITLE;
import static ru.vpcb.popularmovie.utils.Constants.KEY_OVERVIEW;
import static ru.vpcb.popularmovie.utils.Constants.KEY_PATH;
import static ru.vpcb.popularmovie.utils.Constants.KEY_POP;
import static ru.vpcb.popularmovie.utils.Constants.KEY_POSTER_HIGH;
import static ru.vpcb.popularmovie.utils.Constants.KEY_POSTER_LOW;
import static ru.vpcb.popularmovie.utils.Constants.KEY_POSTER_MID;
import static ru.vpcb.popularmovie.utils.Constants.KEY_POSTER_SUPER;
import static ru.vpcb.popularmovie.utils.Constants.KEY_RELEASE_DATE;
import static ru.vpcb.popularmovie.utils.Constants.KEY_TITLE;
import static ru.vpcb.popularmovie.utils.Constants.KEY_VIDEO;
import static ru.vpcb.popularmovie.utils.Constants.KEY_VOTE;
import static ru.vpcb.popularmovie.utils.Constants.KEY_VOTE_AVG;
import static ru.vpcb.popularmovie.utils.Constants.POSTER_BASE;
import static ru.vpcb.popularmovie.utils.Constants.POSTER_SIZE;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class MovieItem implements Parcelable {

    private int voteCount;
    private int id;
    private boolean video;
    private double voteAverage;
    private String title;
    private double popularity;
    private String posterPath;
    private String originLang;
    private String originTitle;
    private List<Integer> listGenreID;
    private List<String> listGenres;

    private String posterHighRes;
    private String posterLowRes;
    private String backDropPath;
    private boolean adult;
    private String overview;
    private String releaseDate;
    private final boolean valid;
    private List<ReviewItem> listReview;

    /**
     * Constructor  from JSON data object
     * Uses parser() method to fill in fields of the object
     * Fills validity value according to result of parser()
     *
     * @param json data source JSON object
     */
    public MovieItem(JSONObject json) {
        valid = parser(json);
    }

    /**
     * Constructor  from Parcel object
     *
     * @param in parcel data source object
     */
    private MovieItem(Parcel in) {
        voteCount = in.readInt();
        id = in.readInt();
        video = in.readByte() != 0;
        voteAverage = in.readDouble();
        title = in.readString();
        popularity = in.readDouble();
        posterPath = in.readString();
        originLang = in.readString();
        originTitle = in.readString();
        listGenres = in.createStringArrayList();
        posterHighRes = in.readString();
        posterLowRes = in.readString();
        backDropPath = in.readString();
        adult = in.readByte() != 0;
        overview = in.readString();
        releaseDate = in.readString();
        valid = in.readByte() != 0;
        listReview = in.createTypedArrayList(ReviewItem.CREATOR);

    }

    /**
     * Parcel Creator
     */
    public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    /**
     * Parse JSON object and fills fields of the object
     * Returns the result of operation
     *
     * @param json input JSON object
     * @return true if completed successfully, false otherwise
     */
    private boolean parser(JSONObject json) {
        try {
            voteCount = json.getInt(KEY_VOTE);
            id = json.getInt(KEY_ID);
            video = json.getBoolean(KEY_VIDEO);
            voteAverage = json.getDouble(KEY_VOTE_AVG);
            title = json.getString(KEY_TITLE);
            popularity = json.getDouble(KEY_POP);
            posterPath = json.getString(KEY_PATH);
            originLang = json.getString(KEY_ORIGIN_LANG);
            originTitle = json.getString(KEY_ORIGIN_TITLE);
            JSONArray jsonArray = json.getJSONArray(KEY_GENRE_IDS);
            listGenreID = new ArrayList<>();
            listGenres = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                int genreId = jsonArray.getInt(i);
                listGenreID.add(genreId);
                listGenres.add(ParseUtils.getGenre(genreId));
            }

            backDropPath = json.getString(KEY_BACKDROP_PATH);
            adult = json.getBoolean(KEY_ADULT);
            overview = json.getString(KEY_OVERVIEW);
            releaseDate = json.getString(KEY_RELEASE_DATE);

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


    /**
     * Returns Movie Poster Path depending on index of size in POSTER_SIZE[] array
     *
     * @param size int value index of size in POSTER_SIZE[] array
     * @return Movie Poster Path
     */
    private String getPoster(int size) {
        if (size < 0 || size >= POSTER_SIZE.length) {
            size = KEY_POSTER_LOW;
        }
        return POSTER_BASE + POSTER_SIZE[size] + posterPath;
    }

    /**
     * Returns Movie Poster Path for w185 size
     *
     * @return Movie Poster Path
     */
    String getPosterLow() {
        return getPoster(KEY_POSTER_LOW);
    }

    /**
     * Returns BackDrop Poster Path depending on index of size in POSTER_SIZE[] array
     *
     * @param size int value index of size in POSTER_SIZE[] array
     * @return BackDrop Poster Path
     */
    private String getBackDrop(int size) {
        if (size < 0 || size >= POSTER_SIZE.length) {
            size = KEY_POSTER_LOW;
        }
        return POSTER_BASE + POSTER_SIZE[size] + backDropPath;
    }

    /**
     * Returns BackDrop Poster Path depending on screen resolution
     *
     * @param isScreenHighRes input value true for screen width > DP_WIDTH_HIGH, false otherwise
     * @return BackDrop Poster Path
     */
    String getBackDropSelected(boolean isScreenHighRes) {
        if (isScreenHighRes) {
            return getBackDrop(KEY_POSTER_SUPER);
        }
        return getBackDrop(KEY_POSTER_HIGH);
    }

    /**
     * Checks if releaseData value is valid
     * Returns  string year value in "YYYY" format
     * Returns "unknown" if releaseDate invalid
     *
     * @return string year value of releaseDate field "YYYY" format or "unknown"
     */
    String getReleaseYear() {
        String regex = "\\d{4}-\\d{2}-\\d{2}";
        if (!Pattern.compile(regex).matcher(releaseDate).matches()) {
            return "unknown";
        }
        return releaseDate.substring(0, 4);
    }

    /**
     * Checks if releaseData value is valid
     * Returns string value of releaseDate in "MMM-dd-YYYY" format
     * Returns "unknown" if releaseDate invalid
     *
     * @return string value of releaseDate field in "MMM-dd-YYYY" format  or "unknown"
     */
    String getReleaseDateVerbose() {
        if (releaseDate == null || releaseDate.isEmpty()) {
            return "unknown";
        }
        String regex = "\\d{4}-\\d{2}-\\d{2}";
        if (!Pattern.compile(regex).matcher(releaseDate).matches()) {
            return "unknown";
        }

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(releaseDate);
            return DateFormat.getDateInstance().format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "unknown";
    }

    /**
     * Getter of voteAverage
     * Converts double value to formatted string  and returns string
     *
     * @return formatted string value from double voteAverage
     */
    String getRating() {
        if (voteAverage <= 0) {
            return String.format(Locale.US, "%4s", "");
        }
        return String.format(Locale.US, "%2.1f", voteAverage);
    }

// standard getters

    public int getVoteCount() {
        return voteCount;
    }

    public int getId() {
        return id;
    }

    public boolean isVideo() {
        return video;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    String getTitle() {
        return title;
    }

    public double getPopularity() {
        return popularity;
    }

    public String getPosterMid() {
        return getPoster(KEY_POSTER_MID);
    }

    public String getPosterHigh() {
        return getPoster(KEY_POSTER_HIGH);
    }

    public String getOriginLang() {
        return originLang;
    }

    public String getOriginTitle() {
        return originTitle;
    }

    public List<Integer> getListGenreID() {
        return listGenreID;
    }

    public List<String> getListGenres() {
        return listGenres;
    }

    public String getPosterHighRes() {
        return posterHighRes;
    }

    public String getPosterLowRes() {
        return posterLowRes;
    }

    public String getBackDropPath() {
        return backDropPath;
    }

    public String getBackDropLow() {
        return getBackDrop(KEY_POSTER_LOW);
    }

    public String getBackDropMid() {
        return getBackDrop(KEY_POSTER_MID);
    }

    String getBackDropHigh() {
        return getBackDrop(KEY_POSTER_HIGH);
    }

    public boolean isAdult() {
        return adult;
    }

    String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    List<ReviewItem> getListReview() {
        return listReview;
    }

    /**
     * Setter listReview for List<ReviewItem object
     *
     * @param listReview input List<ReviewItem> object to store
     */
    void setListReview(List<ReviewItem> listReview) {
        this.listReview = listReview;
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
        parcel.writeInt(voteCount);
        parcel.writeInt(id);
        parcel.writeByte((byte) (video ? 1 : 0));
        parcel.writeDouble(voteAverage);
        parcel.writeString(title);
        parcel.writeDouble(popularity);
        parcel.writeString(posterPath);
        parcel.writeString(originLang);
        parcel.writeString(originTitle);
        parcel.writeStringList(listGenres);
        parcel.writeString(posterHighRes);
        parcel.writeString(posterLowRes);
        parcel.writeString(backDropPath);
        parcel.writeByte((byte) (adult ? 1 : 0));
        parcel.writeString(overview);
        parcel.writeString(releaseDate);
        parcel.writeByte((byte) (valid ? 1 : 0));
        parcel.writeTypedList(listReview);
    }
}
