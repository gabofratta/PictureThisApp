package com.example.janrodriguez.picturethis.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by janrodriguez on 4/18/15.
 */
public class Challenge implements Parcelable {

    private String id = "";
    private String title;
    private User challenger;
    private ArrayList<User> challengedList = new ArrayList<User>();
    private MyGeoPoint location;
    private String localFilePath;
    private String remoteFilePath;
    private boolean active;
    private boolean multiplayer;
    private Date createdAt = new Date();

    public  Challenge (String title, User challenger, MyGeoPoint location, ArrayList<User> challengedList, String localFilePath) {
        this.title = title;
        this.challenger = challenger;
        this.location = location;
        this.challengedList = challengedList;
        this.localFilePath = localFilePath;
        this.multiplayer = challengedList.size() > 1;
    }

    public Challenge (String id, User challenger, String title, MyGeoPoint location, ArrayList<User> challengedList, String remoteFilePath, boolean active, Date createdAt) {
        this(title, challenger, location, challengedList, null);
        this.id = id;
        this.remoteFilePath = remoteFilePath;
        this.active = active;
        this.createdAt = createdAt;
    }

    //CREATE FROM A PARCEL OBJECT
    public Challenge(Parcel source) {
        this.id = source.readString();
        this.title = source.readString();
        this.challenger = (User)source.readValue(User.class.getClassLoader());
        source.readList(challengedList, User.class.getClassLoader());
        this.localFilePath = source.readString();
        this.remoteFilePath = source.readString();
        this.location = (MyGeoPoint)source.readValue(MyGeoPoint.class.getClassLoader());
        this.active = source.readByte() == 1;
        this.multiplayer = source.readByte() == 1;
        this.createdAt = (Date)source.readValue(Date.class.getClassLoader());
    }

    //Must have this for parcelable objects
    public static final Creator CREATOR = new Creator() {
        @Override
        public Challenge createFromParcel(Parcel source) {
            return new Challenge(source);
        }

        @Override
        public Challenge[] newArray(int size) {
            return new Challenge[size];
        }
    };

    /**PARCELABLE IMPLEMENTATION**/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeValue(challenger);
        dest.writeList(challengedList);
        dest.writeString(localFilePath);
        dest.writeString(remoteFilePath);
        dest.writeValue(location);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeByte((byte)(multiplayer ? 1 : 0));
        dest.writeValue(createdAt);
    }
    /**\PARCELABLE IMPLEMENTATION**/

    /**Getters**/
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public User getChallenger() {
        return challenger;
    }

    public MyGeoPoint getLocation() {
        return location;
    }

    public String getLocalFilePath() { return localFilePath; }

    public String getRemoteFilePath() { return remoteFilePath; }

    public boolean isActive() {
        return active;
    }

    public boolean isMultiplayer() {
        return multiplayer;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public ArrayList<User> getChallengedList() {
        return challengedList;
    }
    /**\Getters**/

    /**Setters**/
    public void setActive (boolean active) {
        this.active = active;
    }
    /**\Setters**/


}
