package com.example.janrodriguez.picturethis.Helpers;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by janrodriguez on 4/18/15.
 */
public class Challenge implements Parcelable {

    private String id;
    private String title;
    private User challenger;
    private ArrayList<User> challengedList;
    private Location location;
    private boolean active;
    private boolean multiplayer;
    private Date createdAt;

    public  Challenge (String title, Location location, ArrayList<User> challengedList) {
        this.title = title;
        this.location = location;
        this.challengedList = challengedList;
        this.multiplayer = challengedList.size() > 1;
    }

    public Challenge (String id, String title, Location location, ArrayList<User> challengedList, boolean active, Date createdAt) {
        this(title, location, challengedList);
        this.id = id;
        this.active = active;
        this.createdAt = createdAt;
    }

    //CREATE FROM A PARCEL OBJECT
    public Challenge(Parcel source) {
        this.id = source.readString();
        this.title = source.readString();
        this.challenger = (User)source.readValue(User.class.getClassLoader());
        source.readList(this.challengedList, User.class.getClassLoader());
        this.location = (Location)source.readValue(Location.class.getClassLoader());
        this.active = source.readByte() == 1;
        this.multiplayer = source.readByte() == 1;
        this.createdAt = (Date)source.readValue(Date.class.getClassLoader());
    }

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
        dest.writeTypedList(challengedList);
        dest.writeValue(location);
        dest.writeByte((byte)(active ? 1 : 0));
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

    public Location getLocation() {
        return location;
    }

    public boolean isActive() {
        return active;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
    /**\Getters**/

    /**Setters**/
    public void setActive (boolean active) {
        this.active = active;
    }
    /**\Setters**/


}
