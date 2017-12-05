package ru.vpcb.popularmovie;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import ru.vpcb.popularmovie.utils.QueryType;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 30-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

class DataSource implements Parcelable {
    private List<List<MovieItem>> mArrayListMovie;
    private List<Integer> mListPage;
    private final QueryType mQueryMode;
    private List<Integer> mListPosition;

    /**
     * Constructor  standard
     *
     * @param mArrayListMovie List of List<MovieItems> , one per RecycleView of ViewPager
     * @param mListPage       List of current pages loaded from IMDb
     * @param mQueryMode      mode of current query
     * @param mListPosition   List of current positions of RecycleViews
     */
     DataSource(List<List<MovieItem>> mArrayListMovie, List<Integer> mListPage,
                      QueryType mQueryMode, List<Integer> mListPosition) {
        this.mArrayListMovie = mArrayListMovie;
        this.mListPage = mListPage;
        this.mQueryMode = mQueryMode;
        this.mListPosition = mListPosition;
    }

    /**
     * Constructor     makes object from Parcel source
     *
     * @param in Parcel source of parameters
     */
    private DataSource(Parcel in) {
        in.readList(mArrayListMovie, List.class.getClassLoader());
        in.readList(mListPage, List.class.getClassLoader());
        mQueryMode = QueryType.values()[in.readInt()];
        in.readList(mListPosition, List.class.getClassLoader());
    }

    /**
     * Parcel Creator
     *
     */
    public static final Creator<DataSource> CREATOR = new Creator<DataSource>() {
        @Override
        public DataSource createFromParcel(Parcel in) {
            return new DataSource(in);
        }

        @Override
        public DataSource[] newArray(int size) {
            return new DataSource[size];
        }
    };


     List<List<MovieItem>> getArrayListMovie() {
        return mArrayListMovie;
    }

     List<Integer> getListPage() {
        return mListPage;
    }

     QueryType getQueryMode() {
        return mQueryMode;
    }

     List<Integer> getListPosition() {
        return mListPosition;
    }

    /**
     * Parcel method
     * @return  0
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
        parcel.writeList(mArrayListMovie);
        parcel.writeList(mListPage);
        parcel.writeInt(mQueryMode.ordinal());
        parcel.writeList(mListPosition);

    }
}
