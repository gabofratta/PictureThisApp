package com.example.janrodriguez.picturethis.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.janrodriguez.picturethis.Helpers.Challenge;
import com.example.janrodriguez.picturethis.Helpers.ImageHelper;
import com.example.janrodriguez.picturethis.Helpers.ParseHelper;
import com.example.janrodriguez.picturethis.Helpers.ParseTableConstants;
import com.example.janrodriguez.picturethis.Helpers.Response;
import com.example.janrodriguez.picturethis.Helpers.User;
import com.example.janrodriguez.picturethis.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ViewChallengeActivity extends AppCompatActivity {

    private static final String TAG = "ViewChallengeActivity" ;
    private static final int HEIGHT = 200;
    private static final int WIDTH = 200;


    private Challenge currentChallenge;

    private TextView challengeTitle;
    private TextView challengerName;
    private TextView challengeDate;
    private ImageButton challenge_pic;
    private ImageButton response_pic;
    private Button sendResponseButton;

    private Uri tempPictureUri;
    private Uri currentPictureUri;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_challenge);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentChallenge = (Challenge) extras.getParcelable(Challenge.INTENT_TAG);
            displayChallenge();
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

        //GetUsersLatestResponseToChallenge(User user, Challenge challenge, FindCallback < ParseObject > callback
        // if challenge has repsonse
            // show response
        // else
            // make it a button

    }


    private void displayChallenge() {
        challengeTitle = (TextView) findViewById(R.id.challenge_title);
        challengeTitle.setText(currentChallenge.getTitle());

        challengerName = (TextView) findViewById(R.id.challenger_name);
        challengerName.setText(currentChallenge.getChallenger().getName());

        challengeDate = (TextView) findViewById(R.id.challenge_date);
        challengeDate.setText(currentChallenge.getCreatedAt().toString());

        response_pic = (ImageButton) findViewById(R.id.response_picture);
        sendResponseButton = (Button) findViewById(R.id.sendResponse_button);

        // Setting the Challenge Picture
        challenge_pic = (ImageButton) findViewById(R.id.challenge_picture);
        ParseHelper.GetChallengeImage(currentChallenge, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject data, ParseException e) {
                if (e == null) {
                    Bitmap bmp = null;
                    try {
                        bmp = BitmapFactory.decodeByteArray(data.getParseFile(ParseTableConstants.CHALLENGE_PICTURE).getData(), 0, data.getParseFile(ParseTableConstants.CHALLENGE_PICTURE).getData().length);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    challenge_pic.setImageBitmap(bmp);
                } else {
                    Log.e("Tag", "Error: " + e.getMessage());
                }
            }
        });

        // Fetching the responses
        ParseHelper.GetUsersLatestResponseToChallenge(BaseGameActivity.currentUser, currentChallenge, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    Log.e(TAG, "GOT TO LEVEL 0");
                    if ( parseObjects.size() != 0 ){
                        Log.e(TAG, "GOT TO LEVEL 1");
                        Bitmap bmp = null;
                        try {
                            Response response = new Response(parseObjects.get(0));
                            bmp = BitmapFactory.decodeByteArray(parseObjects.get(0).getParseFile(ParseTableConstants.RESPONSE_PICTURE).getData(),0,parseObjects.get(0).getParseFile(ParseTableConstants.RESPONSE_PICTURE).getData().length);
                            response_pic.setImageBitmap(bmp);
                            sendResponseButton.setVisibility(View.GONE);
                        }  catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                    else
                    {
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
                                Response response = new Response(currentChallenge, BaseGameActivity.currentUser, currentPictureUri.getPath());
                                //Challenge challenge = new Challenge(title, currentUser, currentLocation, challengedList, currentPictureUri.getPath());
                                ParseHelper.CreateResponse(response, new SaveCallback() {
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(getApplicationContext(), getString(R.string.response_created), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.e(TAG, "Error: " + e.getMessage());
                                        }
                                    }
                                });

                                finish();
                            }
                        });

                    }
                }
                else
                {
                    Log.e(TAG, "Error: " + e.getMessage());
                }
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

            currentPictureUri = tempPictureUri;
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), currentPictureUri);
            } catch (IOException e) {
                Log.e(TAG, "Error: " + e.getMessage());
            }

            if(bitmap != null) {
                Bitmap decodedBitmap = ImageHelper.DecodeSampledBitmapFromResource(currentPictureUri.getPath(), WIDTH, HEIGHT);
                response_pic.setImageBitmap(decodedBitmap);
            }
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



}
