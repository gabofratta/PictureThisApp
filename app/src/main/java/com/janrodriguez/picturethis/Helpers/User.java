package com.janrodriguez.picturethis.Helpers;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * Created by janrodriguez on 4/18/15.
 */
public class User implements Parcelable {

    static public final String INTENT_TAG = "user";

    static private final String TAG = "UserClass";

    private String id;
    private String googleId;
    private String name;
    private int score;

    public User (ParseObject po) {
        this.id = po.getObjectId();
        this.googleId = po.getString(ParseTableConstants.USER_GOOGLE_ID);
        this.name = po.getString(ParseTableConstants.USER_NAME);
        this.score = po.getInt(ParseTableConstants.USER_SCORE);
    }

    public User (String googleId, String name) {
        this.googleId = googleId;
        this.name = name;
    }

    public User (String id, String googleId, String name, int score) {
        this(googleId, name);
        this.id = id;
        this.score = score;
    }

    public User (Parcel source) {
        this.id = source.readString();
        this.googleId = source.readString();
        this.name = source.readString();
        this.score = source.readInt();
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
        dest.writeInt(score);
    }

    public ParseObject createParseObject() {
        ParseObject userPO = new ParseObject(ParseTableConstants.USER_TABLE);
        userPO.put(ParseTableConstants.USER_NAME, getName());
        userPO.put(ParseTableConstants.USER_GOOGLE_ID, getGoogleId());
        userPO.put(ParseTableConstants.USER_SCORE, getScore());

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

    public int getScore() {
        return score;
    }

    /**\Getters**/

    public void incrementScore(int amount){
        this.score += amount;
    }

    public void updateScore(final GoogleApiClient client) {
        ParseHelper.UpdateUserScore(this, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error setting score: " + e.getMessage());
                }
                Games.Leaderboards.submitScore(client, Leaderboard.ID, getScore());
            }
        });
    }
    @Override
    public String toString() {
        return name;
    }

}
