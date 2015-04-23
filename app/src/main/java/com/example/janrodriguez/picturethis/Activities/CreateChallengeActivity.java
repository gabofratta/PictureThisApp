package com.example.janrodriguez.picturethis.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.janrodriguez.picturethis.Helpers.Challenge;
import com.example.janrodriguez.picturethis.Helpers.ImageHelper;
import com.example.janrodriguez.picturethis.Helpers.MyGeoPoint;
import com.example.janrodriguez.picturethis.Helpers.ParseHelper;
import com.example.janrodriguez.picturethis.Helpers.User;
import com.example.janrodriguez.picturethis.R;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CreateChallengeActivity extends BaseGameActivity implements ResultCallback<People.LoadPeopleResult> {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final String TAG = "CreateChallengeActivity";
    private static final int HEIGHT = 200;
    private static final int WIDTH = 200;

    private ArrayList<String> usersList;
    private ArrayAdapter<String> usersAdapter;
    private ListView usersListView;
    private ImageView imageButton;
    private Button mapButton;
    private Button usersButton;
    private Button sendButton;

    private Uri currentPictureUri;
    private boolean inListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_challenge);

        initialize();
    }

    private void initialize() {
        inListView = false;

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.root);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inListView) {
                    usersListView.setVisibility(View.INVISIBLE);
                } else {
                    inListView = false;
                }
            }
        });

        usersListView = (ListView) findViewById(R.id.listView);
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> myAdapter, View myView, int pos, long mylng) {
                inListView = true;
            }
        });

        usersList = new ArrayList<String>();
        usersAdapter = new ArrayAdapter<String>(this, R.layout.user_item, usersList);
        usersListView.setAdapter(usersAdapter);
//        Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
//                .setResultCallback(this);

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File imageFile = null;
                try {
                    imageFile = ImageHelper.createImageFile();
                } catch (IOException e) {
                    Log.e(TAG, "Error: " + e.getMessage());
                }

                currentPictureUri = Uri.fromFile(imageFile);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null && imageFile != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPictureUri);
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }
        });

        mapButton = (Button) findViewById(R.id.button4);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Uri geoLocation = Uri.parse("geo:0,0?q=" + savedLatitude + "," + savedLongitude + "(Target)");
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(geoLocation);
//                startActivity(intent);
            }
        });

        usersButton = (Button) findViewById(R.id.button5);
        usersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersListView.setVisibility(View.VISIBLE);
            }
        });

        sendButton = (Button) findViewById(R.id.button6);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText titleField = (EditText) findViewById(R.id.editText);
                String title = titleField.getText().toString();

                MyGeoPoint location = null;
                ArrayList<User> challengedList = null;

                if (title.trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Title is missing", Toast.LENGTH_SHORT).show();
                    return;
                } else if (currentPictureUri == null) {
                    Toast.makeText(getApplicationContext(), "Picture is missing", Toast.LENGTH_SHORT).show();
                    return;
                } else if (location == null) {
                    Toast.makeText(getApplicationContext(), "Location is missing", Toast.LENGTH_SHORT).show();
                    return;
                } else if (challengedList.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Challenged users missing", Toast.LENGTH_SHORT).show();
                    return;
                }

                Challenge challenge = new Challenge(title, currentUser, location, challengedList, currentPictureUri.getPath());
                ParseHelper.CreateChallenge(challenge, new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "Challenge created", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Error: " + e.getMessage());
                        }
                    }
                });

                finish();
            }
        });
    }

    @Override
    public void onResult(People.LoadPeopleResult peopleData) {
        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            usersList.clear();
            PersonBuffer personBuffer = peopleData.getPersonBuffer();
            try {
                int count = personBuffer.getCount();
                for (int i = 0; i < count; i++) {
                    usersList.add(personBuffer.get(i).getDisplayName());
                }
            } finally {
                personBuffer.close();
            }

            usersAdapter.notifyDataSetChanged();
        } else {
            Log.e(TAG, "Error requesting visible circles: " + peopleData.getStatus());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), currentPictureUri);
            } catch (IOException e) {
                Log.e(TAG, "Error: " + e.getMessage());
            }

            if(bitmap != null) {
                Bitmap decodedBitmap = ImageHelper.decodeSampledBitmapFromResource(currentPictureUri.getPath(), WIDTH, HEIGHT);
                imageButton.setImageBitmap(decodedBitmap);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_challenge, menu);
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
}
