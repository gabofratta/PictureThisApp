package com.example.janrodriguez.picturethis.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.janrodriguez.picturethis.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.List;

public class MainActivity extends BaseSidePanelActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //MAKE SURE TO SET UP SIDE PANEL IN ORDER FOR SIDE PANEL TO WORK
        setUpSidePanel();

        final SaveCallback saveCallback = new SaveCallback() {
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
    }

    public void viewReceivedChallenge(View view) {
        Intent intent = new Intent(this, ReceivedChallengeActivity.class);
        startActivity(intent);
    }

    public void viewSentChallenge(View view) {
        Intent intent = new Intent(this, SentChallengeActivity.class);
        startActivity(intent);
    }

    public void createNewChallenge(View view){
        Intent intent = new Intent(this, CreateChallengeActivity.class);
        startActivity(intent);
    }

    public void viewLoginPage(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void viewHistory(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void viewMapPage(View view){
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(MapActivity.INTENT_SHOW_RADIUS, true);
        intent.putExtra(MapActivity.INTENT_LATITUDE, 42.3579452);
        intent.putExtra(MapActivity.INTENT_LONGITUDE, -71.0937901);
        startActivity(intent);
    }
}
