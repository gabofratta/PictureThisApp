package com.janrodriguez.picturethis.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.janrodriguez.picturethis.Helpers.Achievement;
import com.janrodriguez.picturethis.R;
import com.google.android.gms.games.Games;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.List;

public class MainActivity extends BaseSidePanelActivity {

    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //MAKE SURE TO SET UP SIDE PANEL IN ORDER FOR SIDE PANEL TO WORK
        setUpSidePanel();

        SaveCallback saveCallback = new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {

                } else {
                    Log.e("Tag", "Error: " + e.getMessage());
                }
            }
        };

        FindCallback<ParseObject> findCallback = new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                } else {
                    Log.e("Tag", "Error: " + e.getMessage());
                }
            }
        };

        GetDataCallback dataCallback = new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if (e == null) {
//                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                } else {
                    Log.e("Tag", "Error: " + e.getMessage());
                }
            }
        };
    }

    public void viewReceivedChallenge(View view) {
        Intent intent = new Intent(this, ViewResponseActivity.class);
        startActivity(intent);
    }

    public void viewSentChallenge(View view) {
        Intent intent = new Intent(this, ViewChallengeActivity.class);
        startActivity(intent);
    }

    public void createNewChallenge(View view){
        Intent intent = new Intent(this, CreateChallengeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSignInSucceeded() {
        super.onSignInSucceeded();
        if ((mRequestedClients & CLIENT_GAMES) != 0) {
            Games.Achievements.unlock(getApiClient(), Achievement.INSTALL_AND_SIGN_IN);
            currentUser.incrementScore(5);
            currentUser.updateScore(getApiClient());
        } else {
            Log.d(TAG, "Not signed into google games.");
        }
    }

    public void viewHistory(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void viewMapPage(View view){
        Intent intent = new Intent(this, com.janrodriguez.picturethis.Activities.MapActivity.class);
        intent.putExtra(com.janrodriguez.picturethis.Activities.MapActivity.INTENT_SHOW_RADIUS, true);
        intent.putExtra(com.janrodriguez.picturethis.Activities.MapActivity.INTENT_LATITUDE, 42.3579452);
        intent.putExtra(com.janrodriguez.picturethis.Activities.MapActivity.INTENT_LONGITUDE, -71.0937901);
        startActivity(intent);
    }

    public void viewChallengeFeedActivity(View view) {
        Intent intent = new Intent(this, ChallengeFeedActivity.class);
        startActivity(intent);
    }
}
