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

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class MovieItem implements Parcelable {
    private static final String KEY_VOTE = "vote_count";
    private static final String KEY_ID = "id";
    private static final String KEY_VIDEO = "video";
    private static final String KEY_VOTE_AVG = "vote_average";
    private static final String KEY_TITLE = "title";
    private static final String KEY_POP = "popularity";
    private static final String KEY_PATH = "poster_path";
    private static final String KEY_ORIGIN_LANG = "original_language";
    private static final String KEY_ORIGIN_TITLE = "original_title";
    private static final String KEY_GENRE_IDS = "genre_ids";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";
    private static final String KEY_ADULT = "adult";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_RELEASE_DATE = "release_date";


    private static final String[] POSTER_SIZE = "w92,w154,w185,w342,w500,w780,original".split(",");
    private static final int KEY_POSTER_LOW = 2;
    private static final int KEY_POSTER_MID = 4;
    private static final int KEY_POSTER_HIGH = 5;
    private static final String POSTER_BASE = "http://image.tmdb.org/t/p/";

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
    private boolean valid;
    private List<ReviewItem> listReview;


    public MovieItem(JSONObject json) {
        valid = parser(json);

    }

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
     * Returns true if completed successfully
     *
     * @param json input JSON object
     * @return result of operation,  true completed successfully
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


    public boolean isValid() {
        return valid;
    }

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

    private String getPoster(int size) {
        if (size < 0 || size >= POSTER_SIZE.length) {
            size = KEY_POSTER_LOW;
        }
        return POSTER_BASE + POSTER_SIZE[size] + posterPath;
    }

    String getPosterLow() {
        return getPoster(KEY_POSTER_LOW);
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

    private String getBackDrop(int size) {
        if (size < 0 || size >= POSTER_SIZE.length) {
            size = KEY_POSTER_LOW;
        }
        return POSTER_BASE + POSTER_SIZE[size] + backDropPath;
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

    String getReleaseYear() {
        String regex = "\\d{4}-\\d{2}-\\d{2}";
        if (!Pattern.compile(regex).matcher(releaseDate).matches()) {
            return "unknown";
        }
        return releaseDate.substring(0, 4);
    }

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

    String getRating() {
        if (voteAverage <= 0) {
            return String.format(Locale.US, "%4s", "");
        }
        return String.format(Locale.US, "%2.1f", voteAverage);
    }


    List<ReviewItem> getListReview() {
        return listReview;
    }

    void setListReview(List<ReviewItem> listReview) {
        this.listReview = listReview;
    }


    @Override
    public int describeContents() {
        return 0;
    }

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
