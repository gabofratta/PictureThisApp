package com.example.janrodriguez.picturethis.Helpers;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseFile;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by janrodriguez on 4/18/15.
 */
public class Challenge implements Parcelable {

    static public final String INTENT_TAG = "challenge";

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

    public Challenge(ParseObject po) {
        this.id = po.getObjectId();
        this.title = po.getString(ParseTableConstants.CHALLENGE_TITLE);
        ParseObject userPO = po.getParseObject(ParseTableConstants.CHALLENGE_CHALLENGER);
        this.challenger = new User(userPO);
        this.location = new MyGeoPoint(po.getParseGeoPoint(ParseTableConstants.CHALLENGE_LOCATION));

        ArrayList<ParseObject> challenged = (ArrayList<ParseObject>)po.get(ParseTableConstants.CHALLENGE_CHALLENGED);
        for(ParseObject challengedPO : challenged) {
            challengedList.add(new User(challengedPO));
        }

        this.remoteFilePath = po.getParseFile(ParseTableConstants.CHALLENGE_PICTURE).getUrl();
        this.active = po.getBoolean(ParseTableConstants.CHALLENGE_ACTIVE);
        this.multiplayer = po.getBoolean(ParseTableConstants.CHALLENGE_MULTIPLAYER);
        this.createdAt = po.getCreatedAt();

    }

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

    public ParseObject createParseObject () throws JSONException {
        String fileName = new File(localFilePath).getName();
        byte[] fileBytes = ParseHelper.GetImageBytes(localFilePath);
        ParseFile file = new ParseFile(fileName, fileBytes);

        ParseObject challengerPO = ParseObject.createWithoutData(ParseTableConstants.USER_TABLE, challenger.getId());

        JSONArray pointerArray = new JSONArray();
        for (User challenged : getChallengedList()) {
            pointerArray.put(new JSONObject()
                .put("__type", "Pointer")
                .put("className", "User")
                .put("objectId", challenged.getId()));
        }

        ParseObject challengePO = new ParseObject(ParseTableConstants.CHALLENGE_TABLE);
        challengePO.put(ParseTableConstants.CHALLENGE_TITLE, title);
        challengePO.put(ParseTableConstants.CHALLENGE_CHALLENGER, challengerPO);
        challengePO.put(ParseTableConstants.CHALLENGE_CHALLENGED, pointerArray);
        challengePO.put(ParseTableConstants.CHALLENGE_LOCATION, location);
        challengePO.put(ParseTableConstants.CHALLENGE_PICTURE, file);
        challengePO.put(ParseTableConstants.CHALLENGE_ACTIVE, true);
        challengePO.put(ParseTableConstants.CHALLENGE_MULTIPLAYER, multiplayer);

        return challengePO;
    }

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

    @Override
    public String toString() {
        return new StringBuilder(title)
                .append(": ")
                .append(new SimpleDateFormat("yyyy-MM-dd").format(createdAt))
                .toString();
    }

    private Bitmap picture;
    public void setBitmap(Bitmap bitmap){
        this.picture = bitmap;
    }

    public Bitmap getPictureBitmap(){
        return this.picture;
    }
}
