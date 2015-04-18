package com.example.janrodriguez.picturethis.Helpers;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by janrodriguez on 4/18/15.
 */
public class Response implements Parcelable {

    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_DECLINED = "declined";
    public static final String STATUS_PENDING = "pending";

    private String id;
    private Challenge challenge;
    private User responder;
    private String status;
    private Date createdAt;

    public Response (Challenge challenge, User responder, String status) {
        this.challenge = challenge;
        this.responder = responder;
        this.status = status;
    }

    public Response (String id, Challenge challenge, User responder, String status, Date createdAt) {
        this(challenge, responder, status);
        this.id = id;
        this.createdAt = createdAt;
    }

    public Response (Parcel source) {
        this.id = source.readString();
        this.challenge = (Challenge)source.readValue(Challenge.class.getClassLoader());
        this.responder = (User)source.readValue(User.class.getClassLoader());
        this.status = source.readString();
        this.createdAt = (Date)source.readValue(Date.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeValue(challenge);
        dest.writeValue(responder);
        dest.writeString(status);
        dest.writeValue(createdAt);
    }
}
