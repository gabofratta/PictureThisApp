package com.example.janrodriguez.picturethis.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by janrodriguez on 4/18/15.
 */
public class Response implements Parcelable {

    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_DECLINED = "declined";
    public static final String STATUS_PENDING = "pending";

    private String id = "";
    private Challenge challenge;
    private User responder;
    private String status;
    private Date createdAt = new Date();

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

    //Must have this for parcelable objects
    public static final Creator CREATOR = new Creator() {
        @Override
        public Response createFromParcel(Parcel source) {
            return new Response(source);
        }

        @Override
        public Response[] newArray(int size) {
            return new Response[size];
        }
    };

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

    public String getId() {
        return id;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public User getResponder() {
        return responder;
    }

    public String getStatus() {
        return status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
