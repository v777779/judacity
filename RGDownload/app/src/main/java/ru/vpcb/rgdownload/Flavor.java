package ru.vpcb.rgdownload;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by V1 on 19-Sep-17.
 */

public class Flavor implements Parcelable {
    private String rating;
    private String year;
    private int imageId;
    private String imagePath;

    public Flavor(String rating, String year, int imageId, String mImagePath) {
        this.rating = rating;
        this.year = year;
        this.imageId = imageId;
        this.imagePath = mImagePath;
    }

    private Flavor(Parcel in) {
        year = in.readString();
        rating = in.readString();
        imageId = in.readInt();
        imagePath = in.readString();
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

    String getRating() {
        return rating;
    }

    String getYear() {
        return year;
    }

    int getImageId() {
        return imageId;
    }
    String getImagePath() {
        return imagePath;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(year);
        parcel.writeString(rating);
        parcel.writeInt(imageId);
        parcel.writeString(imagePath);
    }
}
