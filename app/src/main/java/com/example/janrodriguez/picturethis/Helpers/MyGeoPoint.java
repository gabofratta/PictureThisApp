package com.example.janrodriguez.picturethis.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseGeoPoint;

/**
 * Created by janrodriguez on 4/18/15.
 */
public class MyGeoPoint extends ParseGeoPoint implements Parcelable {

    public MyGeoPoint() {
        super();
    }

    public MyGeoPoint(Parcel source) {
        this.setLatitude(source.readDouble());
        this.setLongitude(source.readDouble());
    }

    public static Creator CREATOR = new Creator() {
        @Override
        public MyGeoPoint createFromParcel(Parcel source) {
            return new MyGeoPoint(source);
        }

        @Override
        public MyGeoPoint[] newArray(int size) {
            return new MyGeoPoint[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(getLatitude());
        dest.writeDouble(getLongitude());
    }
}
