package com.janrodriguez.picturethis.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.janrodriguez.picturethis.Helpers.BitmapCameraWorkerTask;
import com.janrodriguez.picturethis.Helpers.BitmapQueryWorkerTask;
import com.janrodriguez.picturethis.Helpers.Challenge;
import com.janrodriguez.picturethis.Helpers.ImageHelper;
import com.janrodriguez.picturethis.Helpers.ParseHelper;
import com.janrodriguez.picturethis.Helpers.ParseTableConstants;
import com.janrodriguez.picturethis.Helpers.Response;
import com.janrodriguez.picturethis.Helpers.Score;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ViewChallengeActivity extends BaseGameActivity {

    private static final String TAG = "ViewChallengeActivity" ;
    private static final int HEIGHT = 500;
    private static final int WIDTH = 500;

    private Challenge currentChallenge;

    private TextView challengeTitle;
    private TextView challengerName;
    private TextView challengeDate;
    private ImageButton challenge_pic;
    private ImageButton response_pic;
    private Button sendResponseButton;
    private ViewSwitcher challengeSwitcher;
    private ViewSwitcher responseSwitcher;

    private Uri tempPictureUri;
    private Uri currentPictureUri;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_challenge);

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

            displayChallenge();

            Button button = (Button) findViewById(R.id.view_map_button);
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


    private void displayChallenge() {
        challengeTitle = (TextView) findViewById(R.id.challenge_title);
        challengeTitle.setText(currentChallenge.getTitle());

        challengerName = (TextView) findViewById(R.id.challenger_name);
        challengerName.setText(currentChallenge.getChallenger().getName());

        challengeDate = (TextView) findViewById(R.id.challenge_date);
        SimpleDateFormat format = new SimpleDateFormat("dd MMM h:mm a", Locale.ENGLISH);
        challengeDate.setText(format.format(currentChallenge.getCreatedAt()));

        response_pic = (ImageButton) findViewById(R.id.response_picture);
        sendResponseButton = (Button) findViewById(R.id.sendResponse_button);

        if(!currentChallenge.isActive()){
            response_pic.setBackground(null);
        }

        ImageView multiplayerIcon = (ImageView) findViewById(R.id.multiplayer_icon);
        if (currentChallenge.isMultiplayer()) {
            multiplayerIcon.setImageResource(R.drawable.ic_action_group);
        } else {
            multiplayerIcon.setImageResource(R.drawable.ic_action_person);
        }

        // Setting the Challenge Picture
        challenge_pic = (ImageButton) findViewById(R.id.challenge_picture);
        ParseHelper.GetChallengeImage(currentChallenge, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    final ParseFile parseFile = parseObject.getParseFile(ParseTableConstants.CHALLENGE_PICTURE);
                    BitmapQueryWorkerTask workerTask = new BitmapQueryWorkerTask(challengeSwitcher, challenge_pic, parseFile);
                    workerTask.execute();
                    challenge_pic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ViewChallengeActivity.this, LargePictureActivity.class);
                            intent.putExtra(Challenge.INTENT_TAG, currentChallenge);
                            startActivity(intent);
                        }
                    });
                } else {
                    //TODO: Getting weird concurrency issues here
                    Log.e(TAG, "Error: " + e.getMessage());
                }
            }
        });

        if (currentChallenge.isActive()) {
            ParseHelper.GetUsersLatestResponseToChallenge(BaseGameActivity.currentUser, currentChallenge, new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        TextView statusTextView = (TextView) findViewById(R.id.status_text);

                        if (parseObjects.size() != 0) {
                            final Response response = new Response(parseObjects.get(0));
                            statusTextView.setText(response.getStatus());

                            if (response.getStatus().equals(Response.STATUS_DECLINED)) {
                                sendResponseButton.setVisibility(View.VISIBLE);
                                setClickListeners();
                                responseSwitcher.showNext();
                            } else {
                                ParseFile parseFile = parseObjects.get(0).getParseFile(ParseTableConstants.RESPONSE_PICTURE);
                                BitmapQueryWorkerTask workerTask = new BitmapQueryWorkerTask(responseSwitcher, response_pic, parseFile);
                                workerTask.execute();
                                response_pic.setBackground(null);

                                response_pic.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ViewChallengeActivity.this, LargePictureActivity.class);
                                        intent.putExtra(LargePictureActivity.RESPONSE_INTENT, response);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                        else
                        {
                            responseSwitcher.showNext();
                            statusTextView.setText(Response.STATUS_OPEN);
                            sendResponseButton.setVisibility(View.VISIBLE);
                            setClickListeners();
                        }
                    }
                    else
                    {
                        Log.e(TAG, "Error: " + e.getMessage());
                    }
                }
            });
        } else {
            ParseHelper.GetAcceptedResponseToChallenge(currentChallenge, new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        TextView statusTextView = (TextView) findViewById(R.id.status_text);

                        if (parseObjects.size() != 0) {
                            final Response response = new Response(parseObjects.get(0));

                            String status = (response.getResponder().getGoogleId().equals(currentUser.getGoogleId())) ?
                                    getString(R.string.status_winner) : getString(R.string.status_loser);
                            statusTextView.setText(status);

                            ParseFile parseFile = parseObjects.get(0).getParseFile(ParseTableConstants.RESPONSE_PICTURE);
                            BitmapQueryWorkerTask workerTask = new BitmapQueryWorkerTask(responseSwitcher, response_pic, parseFile);
                            workerTask.execute();

                            response_pic.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ViewChallengeActivity.this, LargePictureActivity.class);
                                    intent.putExtra(LargePictureActivity.RESPONSE_INTENT, response);
                                    startActivity(intent);
                                }
                            });
                        }
                        else
                        {
                            Log.e(TAG, "Error: No accepted response found for a challenge in history");
                        }
                    } else {
                        Log.e(TAG, "Error: " + e.getMessage());
                    }
                }
            });
        }
    }

    private void setClickListeners() {
        response_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File imageFile = null;
                try {
                    imageFile = ImageHelper.CreateImageFile();
                } catch (IOException e) {
                    Log.e(TAG, "Error: " + e.getMessage());
                }

                tempPictureUri = Uri.fromFile(imageFile);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null && imageFile != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, tempPictureUri);
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }
        });

        sendResponseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPictureUri == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.picture_missing), Toast.LENGTH_SHORT).show();
                    return;
                }

                Response response = new Response(currentChallenge, BaseGameActivity.currentUser, currentPictureUri.getPath());
                ParseHelper.CreateResponseToChallenge(response, currentChallenge, new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), getString(R.string.response_created), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Error: " + e.getMessage());
                        }
                    }
                });

                // Create our Installation query
                ParseQuery pushQuery = ParseInstallation.getQuery();
                pushQuery.whereEqualTo("user", currentChallenge.getChallenger().getId());

                // Send push notification to query
                ParsePush push = new ParsePush();
                push.setQuery(pushQuery); // Set our Installation query
                push.setMessage(BaseGameActivity.currentUser.getName() + " sent you a response to the challenge \""
                        + currentChallenge.getTitle() +"\"");

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

                //Update user's score
                if(loggedIntoGoogleGames()){
                    currentUser.incrementScore(Score.SEND_RESPONSE);
                    currentUser.updateScore(getApiClient());
                    Games.Achievements.unlock(getApiClient(), Achievement.SEND_RESPONSE);
                }
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            currentPictureUri = tempPictureUri;
            BitmapCameraWorkerTask workerTask = new BitmapCameraWorkerTask(response_pic, currentPictureUri, WIDTH, HEIGHT);
            workerTask.execute();
        } else if (resultCode == RESULT_CANCELED && requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            ImageHelper.DeleteImageFile(tempPictureUri);
        }
    };

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
        if (!ParseHelper.haveNetworkConnection(ViewChallengeActivity.this)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ViewChallengeActivity.this);
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
