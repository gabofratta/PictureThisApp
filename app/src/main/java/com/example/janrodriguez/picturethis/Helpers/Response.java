package com.example.janrodriguez.picturethis.Helpers;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.File;
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
    private String localFilePath;
    private String remoteFilePath;
    private String status;
    private Date createdAt = new Date();

    public Response (ParseObject po) {
        this.id = po.getObjectId();
        this.challenge = new Challenge(po.getParseObject(ParseTableConstants.RESPONSE_CHALLENGE));
        this.responder = new User(po.getParseObject(ParseTableConstants.RESPONSE_RESPONDER));
        this.remoteFilePath = po.getParseFile(ParseTableConstants.RESPONSE_PICTURE).getUrl();
        this.status = po.getString(ParseTableConstants.RESPONSE_STATUS);
        this.createdAt = po.getCreatedAt();
    }

    public Response (Challenge challenge, User responder, String localFilePath) {
        this.challenge = challenge;
        this.responder = responder;
        this.status = "pending";
    }

    public Response (String id, Challenge challenge, User responder, String remoteFilePath, String status, Date createdAt) {
        this(challenge, responder, null);
        this.id = id;
        this.status = status;
        this.remoteFilePath = remoteFilePath;
        this.createdAt = createdAt;
    }

    public Response (Parcel source) {
        this.id = source.readString();
        this.challenge = (Challenge)source.readValue(Challenge.class.getClassLoader());
        this.responder = (User)source.readValue(User.class.getClassLoader());
        this.localFilePath = source.readString();
        this.remoteFilePath = source.readString();
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
        dest.writeString(localFilePath);
        dest.writeString(remoteFilePath);
        dest.writeString(status);
        dest.writeValue(createdAt);
    }

    public ParseObject createParseObject() {

        String fileName = new File(getLocalFilePath()).getName();
        byte[] fileBytes = ParseHelper.GetImageBytes(getLocalFilePath());
        ParseFile file = new ParseFile(fileName, fileBytes);

        ParseObject responderPO = ParseObject.createWithoutData(ParseTableConstants.USER_TABLE, getResponder().getId());
        ParseObject challengePO = ParseObject.createWithoutData(ParseTableConstants.CHALLENGE_TABLE, getChallenge().getId());

        ParseObject responsePO = new ParseObject(ParseTableConstants.RESPONSE_TABLE);
        responsePO.put(ParseTableConstants.RESPONSE_RESPONDER, responderPO);
        responsePO.put(ParseTableConstants.RESPONSE_CHALLENGE, challengePO);
        responsePO.put(ParseTableConstants.RESPONSE_PICTURE, file);
        responsePO.put(ParseTableConstants.RESPONSE_STATUS, getStatus());

        return responsePO;
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

    public String getLocalFilePath() {
        return localFilePath;
    }

    public String getRemoteFilePath() {
        return remoteFilePath;
    }

    public String getStatus() {
        return status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
