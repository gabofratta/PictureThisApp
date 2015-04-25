package com.example.janrodriguez.picturethis.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.janrodriguez.picturethis.Helpers.Challenge;
import com.example.janrodriguez.picturethis.R;

public class ViewChallengeActivity extends AppCompatActivity {

    private static final String TAG = "ViewChallengeActivity" ;

    private Challenge currentChallenge;

    private TextView challengeTitle;
    private TextView challengerName;
    private TextView challengeDate;
    private ImageButton challenge_pic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_challenge);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentChallenge = (Challenge) extras.getParcelable(Challenge.INTENT_TAG);
            displayChallenge(currentChallenge);
            final Button button = (Button) findViewById(R.id.view_map_button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(ViewChallengeActivity.this, MapActivity.class);
                    intent.putExtra(MapActivity.INTENT_SHOW_RADIUS, true);
                    intent.putExtra(MapActivity.INTENT_LATITUDE, currentChallenge.getLocation().getLatitude());
                    intent.putExtra(MapActivity.INTENT_LONGITUDE, currentChallenge.getLocation().getLongitude());
                    startActivity(intent);
                }
            });
        } else {
            Log.e(TAG, "Called activity without a Challenge");
        }


    }


    private void displayChallenge(Challenge c) {
         challengeTitle = (TextView) findViewById(R.id.challenge_title);
         challengeTitle.setText(c.getTitle());

        challengerName = (TextView) findViewById(R.id.challenger_name);
        challengerName.setText(c.getChallenger().getName());

        challengeDate = (TextView) findViewById(R.id.challenge_date);
        challengeDate.setText(c.getCreatedAt().toString());

        //challenge_pic = (ImageButton) findViewById(R.id.challenge_picture);
        //challenge_pic.setImageAlpha();

    }

    // Intent intent = new Intent(this, MapActivity.class);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
