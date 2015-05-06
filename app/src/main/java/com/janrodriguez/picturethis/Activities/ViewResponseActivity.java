package com.janrodriguez.picturethis.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ViewSwitcher;

import com.google.android.gms.games.Games;
import com.janrodriguez.picturethis.Helpers.Achievement;
import com.janrodriguez.picturethis.Helpers.BitmapQueryWorkerTask;
import com.janrodriguez.picturethis.Helpers.Challenge;
import com.janrodriguez.picturethis.Helpers.ParseHelper;
import com.janrodriguez.picturethis.Helpers.ParseTableConstants;
import com.janrodriguez.picturethis.Helpers.Response;
import com.janrodriguez.picturethis.Helpers.Score;
import com.janrodriguez.picturethis.Helpers.User;
import com.janrodriguez.picturethis.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private ViewSwitcher challengeSwitcher;
    private ViewSwitcher responseSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_response);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        challengeSwitcher = (ViewSwitcher) findViewById(R.id.challenge_switcher);
        responseSwitcher = (ViewSwitcher) findViewById(R.id.response_switcher);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentChallenge = (Challenge) extras.getParcelable(Challenge.INTENT_TAG);

            if(!currentChallenge.isActive()){
                getSupportActionBar().setTitle("Completed Challenge");
            }

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

        if(!currentChallenge.isActive()){
            response_pic.setBackground(null);
        }

        // Setting the Challenge Picture
        challenge_pic = (ImageButton) findViewById(R.id.challenge_picture);
        ParseHelper.GetChallengeImage(this.currentChallenge, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    ParseFile parseFile = parseObject.getParseFile(ParseTableConstants.CHALLENGE_PICTURE);
                    BitmapQueryWorkerTask workerTask = new BitmapQueryWorkerTask(challengeSwitcher, challenge_pic, parseFile);
                    workerTask.execute();
                    challenge_pic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ViewResponseActivity.this, LargePictureActivity.class);
                            intent.putExtra(Challenge.INTENT_TAG, currentChallenge);
                            startActivity(intent);
                        }
                    });
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
                        final Response response = new Response(parseObjects.get(0));

                        if (response.getStatus().equals(Response.STATUS_PENDING)) {
                            acceptButton.setVisibility(View.VISIBLE);
                            declineButton.setVisibility(View.VISIBLE);
                            setOnClickListeners(response);
                        }

                        TextView responderTextView = (TextView) findViewById(R.id.responder_name);
                        responderTextView.setText(response.getResponder().getName());

                        ParseFile parseFile = parseObjects.get(0).getParseFile(ParseTableConstants.RESPONSE_PICTURE);
                        BitmapQueryWorkerTask workerTask = new BitmapQueryWorkerTask(responseSwitcher, response_pic, parseFile);
                        workerTask.execute();

                        response_pic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ViewResponseActivity.this, LargePictureActivity.class);
                                intent.putExtra(LargePictureActivity.RESPONSE_INTENT, response);
                                startActivity(intent);
                            }
                        });
                    } else {
                        responseSwitcher.showNext();
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

                // Create our Installation query
                ParseQuery pushQuery = ParseInstallation.getQuery();
                pushQuery.whereEqualTo("user", response.getResponder().getId());

                // Send push notification to query
                ParsePush push = new ParsePush();
                push.setQuery(pushQuery); // Set our Installation query
                push.setMessage("Congratulations! You are the winner of the challenge \""+ currentChallenge.getTitle()+"\"");

                push.sendInBackground(new SendCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null){
                            Log.i(TAG, "Push sent successfully!");
                        }else{
                            Log.e(TAG, e.getMessage());

                        }
                    }
                });

                if (currentChallenge.isMultiplayer()){

                    ArrayList<String> listOfLoserIDs = new ArrayList<String>();
                    for (User challenged : currentChallenge.getChallengedList()){
                        if (!challenged.getId().equals(response.getResponder().getId())){
                            listOfLoserIDs.add(challenged.getId());
                        }
                    }

                    // Create our Installation query
                    ParseQuery pushQuery2 = ParseInstallation.getQuery();
                    pushQuery2.whereContainedIn("user", listOfLoserIDs);

                    // Send push notification to query
                    ParsePush push2 = new ParsePush();
                    push2.setQuery(pushQuery2); // Set our Installation query
                    push2.setMessage("The winner of the challenge \"" + currentChallenge.getTitle()
                            + "\" is " + response.getResponder().getName() +". Sorry you lost");

                    push2.sendInBackground(new SendCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e==null){
                                Log.i(TAG, "Push sent successfully!");
                            }else{
                                Log.e(TAG, "Error:"+e.getMessage());

                            }
                        }
                    });
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

                // Create our Installation query
                ParseQuery pushQuery = ParseInstallation.getQuery();
                pushQuery.whereEqualTo("user", response.getResponder().getId());

                // Send push notification to query
                ParsePush push = new ParsePush();
                push.setQuery(pushQuery); // Set our Installation query
                push.setMessage("Your response to the challenge \""+ currentChallenge.getTitle()+"\""
                    + " has been declined by " + BaseGameActivity.currentUser.getName());

                push.sendInBackground(new SendCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null){
                            Log.i(TAG, "Push sent successfully!");
                        }else{
                            Log.e(TAG, e.getMessage());

                        }
                    }
                });

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

    @Override
    public void onSignInSucceeded() {
        if (!ParseHelper.haveNetworkConnection(ViewResponseActivity.this)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ViewResponseActivity.this);
            dialog.setMessage(getString(R.string.error_no_internet));

            dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {}
            });

            dialog.show();
            return;
        }

        super.onSignInSucceeded();
    }

}
