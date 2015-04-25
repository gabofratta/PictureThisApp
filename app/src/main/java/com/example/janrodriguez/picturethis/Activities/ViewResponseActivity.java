package com.example.janrodriguez.picturethis.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.janrodriguez.picturethis.Helpers.Challenge;
import com.example.janrodriguez.picturethis.Helpers.ImageHelper;
import com.example.janrodriguez.picturethis.Helpers.ParseHelper;
import com.example.janrodriguez.picturethis.Helpers.ParseTableConstants;
import com.example.janrodriguez.picturethis.Helpers.Response;
import com.example.janrodriguez.picturethis.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ViewResponseActivity extends AppCompatActivity {

    private static final String TAG = "ViewResponseActivity" ;

    private Challenge currentChallenge;


    private TextView challengeTitle;
    private TextView challengerName;
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
            displayResponse(currentChallenge);
            final Button button = (Button) findViewById(R.id.view_map_button);
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

    private void displayResponse(Challenge c) {
        challengeTitle = (TextView) findViewById(R.id.challenge_title);
        challengeTitle.setText(c.getTitle());

        challengerName = (TextView) findViewById(R.id.challenger_name);
        challengerName.setText(c.getChallenger().getName());

        challengeDate = (TextView) findViewById(R.id.challenge_date);
        challengeDate.setText(c.getCreatedAt().toString());

        acceptButton = (Button) findViewById(R.id.accept_button);
        declineButton = (Button) findViewById(R.id.decline_button);


        challenge_pic = (ImageButton) findViewById(R.id.challenge_picture);

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

                        }  catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                    else
                    {
                       // no response submitted
                        acceptButton.setVisibility(View.GONE);
                        declineButton.setVisibility(View.GONE);
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
