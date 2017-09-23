package ru.vpcb.rgdownload;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by V1 on 19-Sep-17.
 */

public class Flavor implements Parcelable {
    private String mName;
    private String mVersion;
    private int mImageId;
    private String mImagePath;

    public Flavor(String mVersionName, String mVersionNumber, int imageId, String mImagePath) {
        this.mName = mVersionName;
        this.mVersion = mVersionNumber;
        this.mImageId = imageId;
        this.mImagePath = mImagePath;
    }

    private Flavor(Parcel in) {
        mName = in.readString();
        mVersion = in.readString();
        mImageId = in.readInt();
        mImagePath = in.readString();
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

    String getmName() {
        return mName;
    }

    String getmVersion() {
        return mVersion;
    }

    int getmImageId() {
        return mImageId;
    }
    String getmImagePath() {
        return mImagePath;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeString(mVersion);
        parcel.writeInt(mImageId);
        parcel.writeString(mImagePath);
    }
}
