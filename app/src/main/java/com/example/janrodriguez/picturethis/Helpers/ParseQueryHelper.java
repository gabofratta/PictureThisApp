package com.example.janrodriguez.picturethis.Helpers;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

/**
 * Created by Gabo on 4/20/15.
 */
public class ParseQueryHelper {

    static public final String CHALLENGE_TABLE = "Challenge";
    static public final String CHALLENGE_LOCATION = "location";
    static public final String CHALLENGE_PICTURE = "picture";
    static public final String CHALLENGE_CHALLENGER = "challenger";
    static public final String CHALLENGE_TITLE = "title";
    static public final String CHALLENGE_CREATED_AT = "createdAt";
    static public final String CHALLENGE_ACTIVE = "active";
    static public final String CHALLENGE_CHALLENGED = "challenged";
    static public final String CHALLENGE_MULTIPLAYER = "multiplayer";

    static public final String CHALLENGED_TABLE = "Challenged";
    static public final String CHALLENGED_CHALLENGE = "challenge";
    static public final String CHALLENGED_CHALLENGED = "challenged";

    static public final String RESPONSE_TABLE = "Response";
    static public final String RESPONSE_RESPONDER = "responder";
    static public final String RESPONSE_PICTURE = "picture";
    static public final String RESPONSE_CHALLENGE = "challenge";
    static public final String RESPONSE_STATUS = "status";
    static public final String RESPONSE_CREATED_AT = "createdAt";

    static public final String USER_TABLE = "User";
    static public final String USER_NAME = "name";
    static public final String USER_GOOGLE_ID = "googleId";

    static public Challenge CreateChallengeFromParseObject(ParseObject po) {
        MyGeoPoint geoPoint = (MyGeoPoint) po.getParseGeoPoint(CHALLENGE_LOCATION);
        String remoteFilePath = po.getParseFile(CHALLENGE_PICTURE).getUrl();
        ParseObject userPO = po.getParseObject(CHALLENGE_CHALLENGER);
        User user = new User(userPO.getObjectId(), userPO.getString(USER_NAME));
        ArrayList<User> challenged = GetChallengedListFromChallengeParseObject(po); //TODO

        return new Challenge(po.getObjectId(), user, po.getString(CHALLENGE_TITLE), geoPoint, challenged, remoteFilePath, po.getBoolean(CHALLENGE_ACTIVE), po.getDate(CHALLENGE_CREATED_AT));
    }

    static public ArrayList<User> GetChallengedListFromChallengeParseObject(ParseObject challengePO) {
        ParseQuery<ParseObject> challengedQuery = ParseQuery.getQuery(CHALLENGED_TABLE);
        challengedQuery.whereEqualTo(CHALLENGED_CHALLENGE, challengePO);

        throw new Error();
    }

    static public ArrayList<Challenge> CreateChallengeListFromParseObjectList(ArrayList<ParseObject> poList) {
        throw new Error();
    }

}
