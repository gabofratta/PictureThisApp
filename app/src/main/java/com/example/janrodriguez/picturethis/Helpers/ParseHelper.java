package com.example.janrodriguez.picturethis.Helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Gabo on 4/17/15.
 */
public class ParseHelper {

    private static final String TAG = "Parse";

    static public void CreateChallenge(Challenge challenge, SaveCallback callback) {
        if (challenge.getLocalFilePath() == null) {
            Log.e(TAG, "Trying to create a challenge without a local file path.");
            return;
        }

        String fileName = new File(challenge.getLocalFilePath()).getName();
        byte[] fileBytes = GetImageBytes(challenge.getLocalFilePath());
        ParseFile file = new ParseFile(fileName, fileBytes);

        ParseObject challengerPO = ParseObject.createWithoutData(ParseQueryHelper.USER_TABLE, challenge.getChallenger().getId());
        ParseObject challengePO = new ParseObject(ParseQueryHelper.CHALLENGE_TABLE);
        challengePO.put(ParseQueryHelper.CHALLENGE_TITLE, challenge.getTitle());
        challengePO.put(ParseQueryHelper.CHALLENGE_CHALLENGER, challengerPO);
        challengePO.put(ParseQueryHelper.CHALLENGE_LOCATION, challenge.getLocation());
        challengePO.put(ParseQueryHelper.CHALLENGE_PICTURE, file);
        challengePO.put(ParseQueryHelper.CHALLENGE_ACTIVE, true);

        JSONArray pointerArray = new JSONArray();
        for (User challenged : challenge.getChallengedList()) {
            try {
                pointerArray.put(new JSONObject()
                            .put("__type", "Pointer")
                            .put("className", "User")
                            .put("objectId", challenged.getId()));
            } catch (JSONException e) {
                Log.e(TAG, "Error: " + e.getMessage());
                return;
            }
        }

        challengePO.put(ParseQueryHelper.CHALLENGE_CHALLENGED, pointerArray);
        challengePO.saveInBackground(callback);
    }

    static public void CreateResponse(Response response, SaveCallback callback) {
        if (response.getLocalFilePath() == null) {
            Log.e(TAG, "Trying to create a response without a local file path.");
            return;
        }

        String fileName = new File(response.getLocalFilePath()).getName();
        byte[] fileBytes = GetImageBytes(response.getLocalFilePath());
        ParseFile file = new ParseFile(fileName, fileBytes);

        ParseObject responderPO = ParseObject.createWithoutData(ParseQueryHelper.USER_TABLE, response.getResponder().getId());
        ParseObject challengePO = ParseObject.createWithoutData(ParseQueryHelper.CHALLENGE_TABLE, response.getChallenge().getId());
        ParseObject responsePO = new ParseObject(ParseQueryHelper.RESPONSE_TABLE);
        responsePO.put(ParseQueryHelper.RESPONSE_RESPONDER, responderPO);
        responsePO.put(ParseQueryHelper.RESPONSE_CHALLENGE, challengePO);
        responsePO.put(ParseQueryHelper.RESPONSE_PICTURE, file);
        responsePO.put(ParseQueryHelper.RESPONSE_STATUS, response.getStatus());
        responsePO.saveInBackground(callback);
    }

    static public void CreateUser(User user, SaveCallback callback) {
        ParseObject userPO = new ParseObject(ParseQueryHelper.USER_TABLE);
        userPO.put(ParseQueryHelper.USER_NAME, user.getName());
        userPO.put(ParseQueryHelper.USER_GOOGLE_ID, user.getGoogleId());
        userPO.saveInBackground(callback);
    }

    static public void UpdateResponse(Response response, SaveCallback responseCallback, SaveCallback challengeCallback) {
        ParseObject responsePO = ParseObject.createWithoutData(ParseQueryHelper.RESPONSE_TABLE, response.getId());
        responsePO.put(ParseQueryHelper.RESPONSE_STATUS, response.getStatus());
        responsePO.saveInBackground(responseCallback);

        if (response.getStatus() == Response.STATUS_ACCEPTED) {
            ParseObject challengePO = ParseObject.createWithoutData(ParseQueryHelper.CHALLENGE_TABLE, response.getChallenge().getId());
            challengePO.put(ParseQueryHelper.CHALLENGE_ACTIVE, false);
            challengePO.saveInBackground(challengeCallback);
        }
    }

    static public void GetChallengesInitiatedByUser(User user, FindCallback<ParseObject> callback) {
        ParseObject challengerPO = ParseObject.createWithoutData(ParseQueryHelper.USER_TABLE, user.getId());
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseQueryHelper.CHALLENGE_TABLE);
        query.include(ParseQueryHelper.CHALLENGE_CHALLENGER);
        query.include(ParseQueryHelper.CHALLENGE_CHALLENGED);
        query.whereEqualTo(ParseQueryHelper.CHALLENGE_CHALLENGER, challengerPO);
        query.orderByDescending(ParseQueryHelper.CHALLENGE_CREATED_AT);
        query.findInBackground(callback);
    }

    static public void GetChallengesReceivedByUser(User user, FindCallback<ParseObject> callback) {
        JSONObject challenged;
        try {
            challenged = new JSONObject()
                    .put("__type", "Pointer")
                    .put("className", "User")
                    .put("objectId", user.getId());
        } catch (JSONException e) {
            Log.e(TAG, "Error: " + e.getMessage());
            return;
        }

        ParseObject challengedPO = ParseObject.createWithoutData(ParseQueryHelper.USER_TABLE, user.getId());
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseQueryHelper.CHALLENGE_TABLE);
        query.include(ParseQueryHelper.CHALLENGE_CHALLENGER);
        query.include(ParseQueryHelper.CHALLENGE_CHALLENGED);
        query.whereEqualTo(ParseQueryHelper.CHALLENGE_CHALLENGED, challenged);
        query.orderByDescending(ParseQueryHelper.CHALLENGE_CREATED_AT);
        query.findInBackground(callback);
    }

    static public void GetPendingResponsesToChallenge(Challenge challenge, FindCallback<ParseObject> callback) {
        ParseObject challengePO = ParseObject.createWithoutData(ParseQueryHelper.CHALLENGE_TABLE, challenge.getId());
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseQueryHelper.RESPONSE_TABLE);
        query.include(ParseQueryHelper.RESPONSE_CHALLENGE);
        query.include(ParseQueryHelper.RESPONSE_RESPONDER);
        query.whereEqualTo(ParseQueryHelper.RESPONSE_CHALLENGE, challengePO);
        query.whereEqualTo(ParseQueryHelper.RESPONSE_STATUS, Response.STATUS_PENDING);
        query.orderByAscending(ParseQueryHelper.RESPONSE_CREATED_AT);
        query.findInBackground(callback);
    }

    static public void GetAcceptedResponseToChallenge(Challenge challenge, FindCallback<ParseObject> callback) {
        ParseObject challengePO = ParseObject.createWithoutData(ParseQueryHelper.CHALLENGE_TABLE, challenge.getId());
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseQueryHelper.RESPONSE_TABLE);
        query.include(ParseQueryHelper.RESPONSE_CHALLENGE);
        query.include(ParseQueryHelper.RESPONSE_RESPONDER);
        query.whereEqualTo(ParseQueryHelper.RESPONSE_CHALLENGE, challengePO);
        query.whereEqualTo(ParseQueryHelper.RESPONSE_STATUS, Response.STATUS_ACCEPTED);
        query.findInBackground(callback);
    }

    static public void GetMatchingUsers(ArrayList<User> users, FindCallback<ParseObject> callback) {
        ArrayList<String> googleIds = new ArrayList<String>();
        for (User user : users) {
            googleIds.add(user.getGoogleId());
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseQueryHelper.USER_TABLE);
        query.whereContainedIn(ParseQueryHelper.USER_GOOGLE_ID, googleIds);
        query.findInBackground(callback);
    }

    static public byte[] GetImageBytes(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

}
