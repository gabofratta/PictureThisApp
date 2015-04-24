package com.example.janrodriguez.picturethis.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

/**
 * Created by janrodriguez on 4/18/15.
 */
public class User implements Parcelable {

    static public final String INTENT_TAG = "user";

    private String id;
    private String googleId;
    private String name;

    public User (ParseObject po) {
        this.id = po.getObjectId();
        this.googleId = po.getString(ParseTableConstants.USER_GOOGLE_ID);
        this.name = po.getString(ParseTableConstants.USER_NAME);
    }

    public User (String googleId, String name) {
        this.googleId = googleId;
        this.name = name;
    }

    public User (String id, String googleId, String name) {
        this(googleId, name);
        this.id = id;
    }

    public User (Parcel source) {
        this.id = source.readString();
        this.googleId = source.readString();
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
        dest.writeString(googleId);
        dest.writeString(name);
    }

    public ParseObject createParseObject() {
        ParseObject userPO = new ParseObject(ParseTableConstants.USER_TABLE);
        userPO.put(ParseTableConstants.USER_NAME, getName());
        userPO.put(ParseTableConstants.USER_GOOGLE_ID, getGoogleId());

        return userPO;
    }

    /**Getters**/
    public String getId() {
        return id;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getName() {
        return name;
    }
    /**\Getters**/

    @Override
    public String toString() {
        return name;
    }

}
