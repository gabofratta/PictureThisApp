package com.janrodriguez.picturethis.Helpers;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Gabo on 4/17/15.
 */
public class ParseHelper {

    private static final String TAG = "Parse";

    static public void CreateChallenge(Challenge challenge, SaveCallback callback) {
        try {
            ParseObject challengePO = challenge.createParseObject();
            challengePO.saveInBackground(callback);
        } catch (JSONException e) {
            Log.e(TAG, "Error: " + e.getMessage());
            return;
        }
    }

    static public void CreateResponseToChallenge(Response response, final Challenge challenge, final SaveCallback callback) {
        ParseObject challengePO = ParseObject.createWithoutData(ParseTableConstants.CHALLENGE_TABLE, challenge.getId());
        final ParseObject responsePO = response.createParseObject();
        responsePO.put(ParseTableConstants.RESPONSE_CHALLENGE, challengePO);
        responsePO.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                AddResponseToChallenge(responsePO.getObjectId(), challenge, callback);
            }
        });
    }

    static public void CreateUser(User user, SaveCallback callback) {
        ParseObject userPO = user.createParseObject();
        userPO.saveInBackground(callback);
    }

    static public void UpdateUserScore(User user, SaveCallback callback) {
        ParseObject userPO = ParseObject.createWithoutData(ParseTableConstants.USER_TABLE, user.getId());
        userPO.put(ParseTableConstants.USER_SCORE, user.getScore());
        userPO.saveInBackground(callback);
    }

    static private void AddResponseToChallenge(String responseID, Challenge challenge, SaveCallback saveCallback) {
        JSONObject responseJSON;
        try {
            responseJSON = new JSONObject()
                    .put("__type", "Pointer")
                    .put("className", "Response")
                    .put("objectId", responseID);
        } catch (JSONException e) {
            Log.e(TAG, "Error: " + e.getMessage());
            return;
        }

        ParseObject challengePO = ParseObject.createWithoutData(ParseTableConstants.CHALLENGE_TABLE, challenge.getId());
        challengePO.add(ParseTableConstants.CHALLENGE_RESPONSES, responseJSON);
        challengePO.saveInBackground(saveCallback);
    }

    static private void UpdateResponseStatus(Response response, String status, SaveCallback callback) {
        ParseObject responsePO = ParseObject.createWithoutData(ParseTableConstants.RESPONSE_TABLE, response.getId());
        responsePO.put(ParseTableConstants.RESPONSE_STATUS, status);
        responsePO.saveInBackground(callback);
    }

    static public void SetChallengeInactive(Challenge challenge, SaveCallback callback) {
        ParseObject challengePO = ParseObject.createWithoutData(ParseTableConstants.CHALLENGE_TABLE, challenge.getId());
        challengePO.put(ParseTableConstants.CHALLENGE_ACTIVE, false);
        challengePO.saveInBackground(callback);
    }

    static public void SetResponseStatusAccepted(Response response, SaveCallback responseCallback) {
        UpdateResponseStatus(response, Response.STATUS_ACCEPTED, responseCallback);
    }

    static public void SetResponseStatusDeclined(Response response, SaveCallback responseCallback) {
        UpdateResponseStatus(response, Response.STATUS_DECLINED, responseCallback);
    }

    static private void GetChallengesInitiatedByUser(User user, boolean active, FindCallback<ParseObject> callback) {
        String responseDotResponder = new StringBuilder(ParseTableConstants.CHALLENGE_RESPONSES)
                .append(".")
                .append(ParseTableConstants.RESPONSE_RESPONDER)
                .toString();

        ParseObject challengerPO = ParseObject.createWithoutData(ParseTableConstants.USER_TABLE, user.getId());
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseTableConstants.CHALLENGE_TABLE);
        query.include(ParseTableConstants.CHALLENGE_CHALLENGER);
        query.include(ParseTableConstants.CHALLENGE_CHALLENGED);
        query.include(ParseTableConstants.CHALLENGE_RESPONSES);
        query.include(responseDotResponder);
        query.whereEqualTo(ParseTableConstants.CHALLENGE_CHALLENGER, challengerPO);
        query.whereEqualTo(ParseTableConstants.CHALLENGE_ACTIVE, active);
        query.orderByDescending(ParseTableConstants.CHALLENGE_CREATED_AT);
        query.findInBackground(callback);
    }

    static public void GetActiveChallengesInitiatedByUser(User user, FindCallback<ParseObject> callback) {
        GetChallengesInitiatedByUser(user, true, callback);
    }

    static public void GetInactiveChallengesInitiatedByUser(User user, FindCallback<ParseObject> callback) {
        GetChallengesInitiatedByUser(user, false, callback);
    }

    static private void GetChallengesReceivedByUser(User user, boolean active, FindCallback<ParseObject> callback) {
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

        String responseDotResponder = new StringBuilder(ParseTableConstants.CHALLENGE_RESPONSES)
                .append(".")
                .append(ParseTableConstants.RESPONSE_RESPONDER)
                .toString();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseTableConstants.CHALLENGE_TABLE);
        query.include(ParseTableConstants.CHALLENGE_CHALLENGER);
        query.include(ParseTableConstants.CHALLENGE_CHALLENGED);
        query.include(ParseTableConstants.CHALLENGE_RESPONSES);
        query.include(responseDotResponder);
        query.whereEqualTo(ParseTableConstants.CHALLENGE_CHALLENGED, challenged);
        query.whereEqualTo(ParseTableConstants.CHALLENGE_ACTIVE, active);
        query.orderByDescending(ParseTableConstants.CHALLENGE_CREATED_AT);
        query.findInBackground(callback);
    }

    static public void GetActiveChallengesReceivedByUser(User user, FindCallback<ParseObject> callback) {
        GetChallengesReceivedByUser(user, true, callback);
    }

    static public void GetInactiveChallengesReceivedByUser(User user, FindCallback<ParseObject> callback) {
        GetChallengesReceivedByUser(user, false, callback);
    }

    static private void GetResponsesToChallenge(Challenge challenge, String status, FindCallback<ParseObject> callback) {
        ParseObject challengePO = ParseObject.createWithoutData(ParseTableConstants.CHALLENGE_TABLE, challenge.getId());
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseTableConstants.RESPONSE_TABLE);
        query.include(ParseTableConstants.RESPONSE_CHALLENGE);
        query.include(ParseTableConstants.RESPONSE_RESPONDER);
        query.whereEqualTo(ParseTableConstants.RESPONSE_CHALLENGE, challengePO);
        query.whereEqualTo(ParseTableConstants.RESPONSE_STATUS, status);
        query.orderByAscending(ParseTableConstants.RESPONSE_CREATED_AT);
        query.findInBackground(callback);
    }

    static public void GetPendingResponsesToChallenge(Challenge challenge, FindCallback<ParseObject> callback) {
        GetResponsesToChallenge(challenge, Response.STATUS_PENDING, callback);
    }

    static public void GetAcceptedResponseToChallenge(Challenge challenge, FindCallback<ParseObject> callback) {
        GetResponsesToChallenge(challenge, Response.STATUS_ACCEPTED, callback);
    }

    static public void GetPendingOrAcceptedResponsesToChallenge(Challenge challenge, FindCallback<ParseObject> callback) {
        ParseObject challengePO = ParseObject.createWithoutData(ParseTableConstants.CHALLENGE_TABLE, challenge.getId());
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseTableConstants.RESPONSE_TABLE);
        query.include(ParseTableConstants.RESPONSE_CHALLENGE);
        query.include(ParseTableConstants.RESPONSE_RESPONDER);
        query.whereEqualTo(ParseTableConstants.RESPONSE_CHALLENGE, challengePO);
        query.whereNotEqualTo(ParseTableConstants.RESPONSE_STATUS, Response.STATUS_DECLINED);
        query.orderByAscending(ParseTableConstants.RESPONSE_CREATED_AT);
        query.findInBackground(callback);
    }

    static public void GetUsersLatestResponseToChallenge(User user, Challenge challenge, FindCallback<ParseObject> callback) {
        ParseObject challengePO = ParseObject.createWithoutData(ParseTableConstants.CHALLENGE_TABLE, challenge.getId());
        ParseObject responderPO = ParseObject.createWithoutData(ParseTableConstants.USER_TABLE, user.getId());
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseTableConstants.RESPONSE_TABLE);
        query.include(ParseTableConstants.RESPONSE_CHALLENGE);
        query.include(ParseTableConstants.RESPONSE_RESPONDER);
        query.whereEqualTo(ParseTableConstants.RESPONSE_CHALLENGE, challengePO);
        query.whereEqualTo(ParseTableConstants.RESPONSE_RESPONDER, responderPO);
        query.orderByDescending(ParseTableConstants.RESPONSE_CREATED_AT);
        query.setLimit(1);
        query.findInBackground(callback);
    }

    static public void GetUserByGoogleId(User user, FindCallback<ParseObject> callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseTableConstants.USER_TABLE);
        query.whereEqualTo(ParseTableConstants.USER_GOOGLE_ID, user.getGoogleId());
        query.findInBackground(callback);
    }

    static public void GetMatchingUsers(ArrayList<User> users, FindCallback<ParseObject> callback) {
        ArrayList<String> googleIds = new ArrayList<String>();
        for (User user : users) {
            googleIds.add(user.getGoogleId());
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseTableConstants.USER_TABLE);
        query.whereContainedIn(ParseTableConstants.USER_GOOGLE_ID, googleIds);
        query.findInBackground(callback);
    }

    static public void GetChallengeImage(Challenge challenge, GetCallback callback) {
        ParseObject challengePO = ParseObject.createWithoutData(ParseTableConstants.CHALLENGE_TABLE, challenge.getId());
        challengePO.fetchInBackground(callback);
    }

    static public void GetResponseImage(Response response, GetCallback callback) {
        ParseObject responsePO = ParseObject.createWithoutData(ParseTableConstants.RESPONSE_TABLE, response.getId());
        responsePO.fetchInBackground(callback);
    }
}


