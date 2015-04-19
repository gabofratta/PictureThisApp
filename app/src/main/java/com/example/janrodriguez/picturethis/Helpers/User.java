package com.example.janrodriguez.picturethis.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by janrodriguez on 4/18/15.
 */
public class User implements Parcelable {

    private String id;
    private String name;

    public User (String id, String name) {
        this.id = id;
        this.name = name;
    }

    public User (Parcel source) {
        this.id = source.readString();
        this.name = source.readString();
    }

    //Must have this for parcelable objects
    public static final Creator CREATOR = new Creator() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }


    /**Getters**/
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    /**\Getters**/



}
