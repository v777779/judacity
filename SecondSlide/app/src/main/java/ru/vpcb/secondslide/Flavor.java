package ru.vpcb.secondslide;

/**
 * Created by V1 on 19-Sep-17.
 */

public class Flavor {
    private final String mName;
    private final String mVersion;
    private final int mImageId;

    public Flavor(String mName, String mVersion, int mImageId) {
        this.mName = mName;
        this.mVersion = mVersion;
        this.mImageId = mImageId;
    }

    public String getMName() {
        return mName;
    }

    public String getMVersion() {
        return mVersion;
    }

    public int getMImageId() {
        return mImageId;
    }
}
