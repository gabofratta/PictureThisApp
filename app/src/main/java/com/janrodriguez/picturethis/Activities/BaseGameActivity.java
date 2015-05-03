/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.janrodriguez.picturethis.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.janrodriguez.picturethis.Helpers.GameHelper;
import com.janrodriguez.picturethis.Helpers.ParseHelper;
import com.janrodriguez.picturethis.Helpers.User;
import com.janrodriguez.picturethis.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Example base class for games. This implementation takes care of setting up
 * the API client object and managing its lifecycle. Subclasses only need to
 * override the @link{#onSignInSucceeded} and @link{#onSignInFailed} abstract
 * methods. To initiate the sign-in flow when the user clicks the sign-in
 * button, subclasses should call @link{#beginUserInitiatedSignIn}. By default,
 * this class only instantiates the GoogleApiClient object. If the PlusClient or
 * AppStateClient objects are also wanted, call the BaseGameActivity(int)
 * constructor and specify the requested clients. For example, to request
 * PlusClient and GamesClient, use BaseGameActivity(CLIENT_GAMES | CLIENT_PLUS).
 * To request all available clients, use BaseGameActivity(CLIENT_ALL).
 * Alternatively, you can also specify the requested clients via
 * @link{#setRequestedClients}, but you must do so before @link{#onCreate}
 * gets called, otherwise the call will have no effect.
 *
 * @author Bruno Oliveira (Google)
 */
public abstract class BaseGameActivity extends AppCompatActivity implements
        GameHelper.GameHelperListener,
        View.OnClickListener
{

    public static User currentUser;

    // The game helper object. This class is mainly a wrapper around this object.
    protected GameHelper mHelper;

    // We expose these constants here because we don't want users of this class
    // to have to know about GameHelper at all.
    public static final int CLIENT_GAMES = GameHelper.CLIENT_GAMES;
    public static final int CLIENT_APPSTATE = GameHelper.CLIENT_APPSTATE;
    public static final int CLIENT_PLUS = GameHelper.CLIENT_PLUS;
    public static final int CLIENT_ALL = GameHelper.CLIENT_ALL;

    public static final String STATE_USERNAME = "username";
    public static final String STATE_USERID = "userid";
    public static final String STATE_USER_GOOG_ID = "usergoogleid";
    public static final String STATE_USER_SCORE = "userscore";
    public static final String SHARED_PREF_NAME = "userinfo";

     //Requested clients. By default, that's just the games client.
    protected int mRequestedClients = CLIENT_GAMES | CLIENT_PLUS;
//    protected int mRequestedClients = CLIENT_PLUS;

    private final static String TAG = "BaseGameActivity";
    protected boolean mDebugLog = false;
    protected boolean mConnectOnStart = true;

    /**
     * Constructs a BaseGameActivity with default client (GamesClient).
     */
    protected BaseGameActivity() {
        super();
    }

    /**
     * Constructs a BaseGameActivity with the requested clients.
     *
     * @param requestedClients The requested clients (a combination of CLIENT_GAMES,
     *                         CLIENT_PLUS and CLIENT_APPSTATE).
     */
    protected BaseGameActivity(int requestedClients) {
        super();
        setRequestedClients(requestedClients);
    }

    /**
     * Sets the requested clients. The preferred way to set the requested clients is
     * via the constructor, but this method is available if for some reason your code
     * cannot do this in the constructor. This must be called before onCreate or getGameHelper()
     * in order to have any effect. If called after onCreate()/getGameHelper(), this method
     * is a no-op.
     *
     * @param requestedClients A combination of the flags CLIENT_GAMES, CLIENT_PLUS
     *                         and CLIENT_APPSTATE, or CLIENT_ALL to request all available clients.
     */
    protected void setRequestedClients(int requestedClients) {
        mRequestedClients = requestedClients;
    }

    public GameHelper getGameHelper() {
        if (mHelper == null) {
            mHelper = new GameHelper(this, mRequestedClients);
            mHelper.enableDebugLog(mDebugLog);
            mHelper.setConnectOnStart(mConnectOnStart);
        }
        return mHelper;
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        if (mHelper == null) {
            getGameHelper();
        }
        mHelper.setup(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHelper.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHelper.onStop();
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        mHelper.onActivityResult(request, response, data);
    }

    protected GoogleApiClient getApiClient() {
        return mHelper.getApiClient();
    }

    protected boolean isSignedIn() {
        return mHelper.isSignedIn();
    }

    protected void beginUserInitiatedSignIn() {
        mHelper.beginUserInitiatedSignIn();
    }

    protected void signOut() {
        mHelper.signOut();
    }

    protected void showAlert(String message) {
        mHelper.makeSimpleDialog(message).show();
    }

    protected void showAlert(String title, String message) {
        mHelper.makeSimpleDialog(title, message).show();
    }

    protected void enableDebugLog(boolean enabled) {
        mDebugLog = true;
        if (mHelper != null) {
            mHelper.enableDebugLog(enabled);
        }
    }

    @Deprecated
    protected void enableDebugLog(boolean enabled, String tag) {
        Log.w(TAG, "BaseGameActivity.enabledDebugLog(bool,String) is " +
                "deprecated. Use enableDebugLog(boolean)");
        enableDebugLog(enabled);
    }

    protected String getInvitationId() {
        return mHelper.getInvitationId();
    }

    protected void reconnectClient() {
        mHelper.reconnectClient();
    }

    protected boolean hasSignInError() {
        return mHelper.hasSignInError();
    }

    protected GameHelper.SignInFailureReason getSignInError() {
        return mHelper.getSignInError();
    }

    @Override
    public void onSignInFailed() {
//        Log.d(TAG, "Failed to sign in.");

    }

    @Override
    public void onSignInSucceeded() {
        // Retrieve some profile information to personalize our app for the user.
        if (currentUser == null || currentUser.getId() == null) {

            final Person currentGPUser = Plus.PeopleApi.getCurrentPerson(getApiClient());
            currentUser = new User(currentGPUser.getId(), currentGPUser.getDisplayName());
            ParseHelper.GetUserByGoogleId(currentUser, new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        if (parseObjects.size() == 0) { //User not found
                            final ParseObject userPO = currentUser.createParseObject();
                            userPO.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        Log.e(TAG, "Error creating user.");
                                        return;
                                    }else{
                                        currentUser = new User(userPO);
                                        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                        installation.put("user", userPO.getObjectId());
                                        installation.saveInBackground();
                                    }
                                }
                            });

                        } else { //User found
                            currentUser = new User(parseObjects.get(0));

                            saveUserSharedPref ();

                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                            installation.put("user", currentUser.getId());
                            installation.saveInBackground();
                        }
                    } else {
                        Log.e(TAG, "Error getting user from google plus id");
                    }
                }
            });
        }
    }

    private void saveUserSharedPref () {
        SharedPreferences sharedPref = BaseGameActivity.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(STATE_USERNAME, currentUser.getName());
        editor.putString(STATE_USER_GOOG_ID, currentUser.getGoogleId());
        editor.putString(STATE_USERID, currentUser.getId());
        editor.putInt(STATE_USER_SCORE, currentUser.getScore());
        editor.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                mHelper.beginUserInitiatedSignIn();
                break;
        }
    }

    protected boolean loggedIntoGoogleGames() {
        return (mRequestedClients & CLIENT_GAMES )!= 0 && getApiClient().isConnected();
    }

}
