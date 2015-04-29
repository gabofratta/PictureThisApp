package com.example.janrodriguez.picturethis.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.janrodriguez.picturethis.Helpers.Achievement;
import com.example.janrodriguez.picturethis.Helpers.BitmapQueryWorkerTask;
import com.example.janrodriguez.picturethis.Helpers.Challenge;
import com.example.janrodriguez.picturethis.Helpers.ParseHelper;
import com.example.janrodriguez.picturethis.Helpers.ParseTableConstants;
import com.example.janrodriguez.picturethis.Helpers.Response;
import com.example.janrodriguez.picturethis.Helpers.Score;
import com.example.janrodriguez.picturethis.R;
import com.google.android.gms.games.Games;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ViewResponseActivity extends BaseGameActivity {

    private static final String TAG = "ViewResponseActivity" ;

    private Challenge currentChallenge;

    private TextView challengeTitle;
    private TextView challengeDate;
    private ImageButton challenge_pic;
    private ImageButton response_pic;

    private Button acceptButton;
    private Button declineButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_response);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentChallenge = (Challenge) extras.getParcelable(Challenge.INTENT_TAG);

            displayResponse();

            Button button = (Button) findViewById(R.id.view_map_button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(ViewResponseActivity.this, MapActivity.class);
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

    private void displayResponse() {
        challengeTitle = (TextView) findViewById(R.id.challenge_title);
        challengeTitle.setText(currentChallenge.getTitle());

        challengeDate = (TextView) findViewById(R.id.challenge_date);
        SimpleDateFormat format = new SimpleDateFormat("dd MMM h:mm a", Locale.ENGLISH);
        challengeDate.setText(format.format(currentChallenge.getCreatedAt()));

        acceptButton = (Button) findViewById(R.id.accept_button);
        declineButton = (Button) findViewById(R.id.decline_button);

        ImageView multiplayerIcon = (ImageView) findViewById(R.id.multiplayer_icon);
        if (currentChallenge.isMultiplayer()) {
            multiplayerIcon.setImageResource(R.drawable.ic_action_group);
        } else {
            multiplayerIcon.setImageResource(R.drawable.ic_action_person);
        }

        challenge_pic = (ImageButton) findViewById(R.id.challenge_picture);
        response_pic = (ImageButton) findViewById(R.id.response_picture);

        // Setting the Challenge Picture
        challenge_pic = (ImageButton) findViewById(R.id.challenge_picture);
        ParseHelper.GetChallengeImage(this.currentChallenge, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    ParseFile parseFile = parseObject.getParseFile(ParseTableConstants.CHALLENGE_PICTURE);
                    BitmapQueryWorkerTask workerTask = new BitmapQueryWorkerTask(challenge_pic, parseFile);
                    workerTask.execute();
                } else {
                    Log.e(TAG, "Error: " + e.getMessage());
                }
            }
        });

        // Fetching the responses
        ParseHelper.GetPendingOrAcceptedResponsesToChallenge(currentChallenge, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    if (parseObjects.size() != 0) {
                        Response response = new Response(parseObjects.get(0));

                        if (response.getStatus().equals(Response.STATUS_PENDING)) {
                            acceptButton.setVisibility(View.VISIBLE);
                            declineButton.setVisibility(View.VISIBLE);
                            setOnClickListeners(response);
                        }

                        TextView responderTextView = (TextView) findViewById(R.id.responder_name);
                        responderTextView.setText(response.getResponder().getName());

                        ParseFile parseFile = parseObjects.get(0).getParseFile(ParseTableConstants.RESPONSE_PICTURE);
                        BitmapQueryWorkerTask workerTask = new BitmapQueryWorkerTask(response_pic, parseFile);
                        workerTask.execute();
                    }
                } else {
                    Log.e(TAG, "Error: " + e.getMessage());
                }
            }
        });
    }

    private void setOnClickListeners(final Response response) {
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseHelper.SetResponseStatusAccepted(response, getSaveCallback(getString(R.string.response_accepted)));
                ParseHelper.SetChallengeInactive(currentChallenge, getSaveCallback(getString(R.string.challenge_closed)));
                if(loggedIntoGoogleGames()){
                    currentUser.incrementScore(Score.REPLY_RESPONSE);
                    currentUser.updateScore(getApiClient());
                    Games.Achievements.unlock(getApiClient(), Achievement.ACCEPT_RESPONSE);
                }
                finish();
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseHelper.SetResponseStatusDeclined(response, getSaveCallback(getString(R.string.response_declined)));
                if(loggedIntoGoogleGames()){
                    currentUser.incrementScore(Score.REPLY_RESPONSE);
                    currentUser.updateScore(getApiClient());
                    Games.Achievements.unlock(getApiClient(), Achievement.DECLINE_RESPONSE);
                }
                finish();
            }
        });
    }

    private SaveCallback getSaveCallback(final String message) {
        return new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Error: " + e.getMessage());
                }
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
