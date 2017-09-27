package ru.vpcb.rgdownload;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by V1 on 19-Sep-17.
 */

public class Flavor implements Parcelable {
    private int imageId;
    private MovieItem movieItem;

    public Flavor(MovieItem movieItem, int imageId) {
        this.movieItem = movieItem;
        this.imageId = imageId;
    }

    private Flavor(Parcel in) {
        movieItem = in.readParcelable(MovieItem.class.getClassLoader());
        imageId = in.readInt();

    }

    public static final Creator<Flavor> CREATOR = new Creator<Flavor>() {
        @Override
        public Flavor createFromParcel(Parcel in) {
            return new Flavor(in);
        }

        @Override
        public Flavor[] newArray(int size) {
            return new Flavor[size];
        }
    };

    int getImageId() {
        return imageId;
    }

    MovieItem getMovieItem() {
        return movieItem;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(movieItem, i);
        parcel.writeInt(imageId);
    }
}
