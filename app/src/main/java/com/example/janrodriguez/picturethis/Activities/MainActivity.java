package com.example.janrodriguez.picturethis.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.janrodriguez.picturethis.Helpers.Achievement;
import com.example.janrodriguez.picturethis.R;
import com.google.android.gms.games.Games;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.List;

public class MainActivity extends BaseGameActivity {

    private static String TAG = "MainActivity";

    private static final int REQUEST_ACHIEVEMENTS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        Button openNewActBtn = (Button)findViewById(R.id.view_achieve_btn);
        openNewActBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), REQUEST_ACHIEVEMENTS);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onSignInSucceeded() {
        super.onSignInSucceeded();
        Log.d(TAG, "Signed in successfully");
        Games.Achievements.unlock(getApiClient(), Achievement.INSTALL_AND_SIGN_IN);

    }
}
