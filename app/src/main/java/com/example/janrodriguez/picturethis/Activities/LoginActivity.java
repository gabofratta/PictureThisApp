package com.example.janrodriguez.picturethis.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.janrodriguez.picturethis.Helpers.User;
import com.example.janrodriguez.picturethis.R;
import com.google.android.gms.common.SignInButton;

public class LoginActivity extends BaseGameActivity {

    private static String TAG = "LoginActivity";

    private SignInButton signinBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Redefine current user when swapping accounts
        currentUser = null;

        mConnectOnStart = false;

        super.onCreate(savedInstanceState);

        //Check if user is already signed in
        SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);;
        //User signing in again
        if(sharedPreferences.getString(STATE_USERID, null) != null) {
            //Set current user info
            String uName = sharedPreferences.getString(STATE_USERNAME, "");
            String uId = sharedPreferences.getString(STATE_USERID, "");
            String uGoogId = sharedPreferences.getString(STATE_USER_GOOG_ID, "");
            int uScore = sharedPreferences.getInt(STATE_USER_SCORE, 0);

            currentUser = new User(uId, uGoogId, uName, uScore);

            Intent intent = new Intent(this, ChallengeFeedActivity.class);
            finish();
            startActivity(intent);
        }

        setContentView(R.layout.activity_login);

        initialize();
    }

    private void initialize() {
        signinBtn = (SignInButton)findViewById(R.id.sign_in_button);
        signinBtn.setOnClickListener(this);
    }
    //Connected to google acount, start main activity and close this one
    @Override
    public void onSignInSucceeded() {

        super.onSignInSucceeded();

        Intent intent = new Intent(this, ChallengeFeedActivity.class);
        finish();

        startActivity(intent);
    }
}
