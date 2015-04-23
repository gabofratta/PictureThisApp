package com.example.janrodriguez.picturethis.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateChallengeActivity extends BaseGameActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final String TAG = "CreateChallengeActivity";
    private static final int HEIGHT = 200;
    private static final int WIDTH = 200;

    private ArrayList<User> usersList;
    private ArrayList<User> challengedList;
    private ArrayAdapter<User> usersAdapter;

    private ListView usersListView;
    private ImageView imageButton;
    private Button mapButton;
    private Button usersButton;
    private Button sendButton;

    private ResultCallback<People.LoadPeopleResult> resultCallback;
    private Uri currentPictureUri;
    private boolean listViewOpen;

    private LocationListener locationListener;
    private LocationManager locationManager;
    private MyGeoPoint currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_challenge);

        initialize();
    }

    private void initialize() {
        listViewOpen = false;

        resultCallback = new ResultCallback<People.LoadPeopleResult>() {
            @Override
            public void onResult(People.LoadPeopleResult peopleData) {
                if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
                    usersList.clear();
                    PersonBuffer personBuffer = peopleData.getPersonBuffer();
                    try {
                        int count = personBuffer.getCount();
                        for (int i = 0; i < count; i++) {
                            usersList.add(new User(personBuffer.get(i).getId(), personBuffer.get(i).getDisplayName()));
                        }
                    } finally {
                        personBuffer.close();
                    }

                    ParseHelper.GetMatchingUsers(usersList, new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> parseObjects, ParseException e) {
                            if (e == null) {
                                usersList.clear();

                                for (ParseObject parseObject : parseObjects) {
                                    User user = new User(parseObject);
                                    usersList.add(user);
                                }

                                usersAdapter.notifyDataSetChanged();
                            } else {
                                Log.e(TAG, "Error: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "Error requesting visible circles: " + peopleData.getStatus());
                }
            }
        };

        Plus.PeopleApi.loadVisible(getApiClient(), null).setResultCallback(resultCallback);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (currentLocation == null) {
                    currentLocation = new MyGeoPoint();
                    CheckBox checkLocation = (CheckBox) findViewById(R.id.checkLocation);
                    checkLocation.setChecked(true);
                }
                currentLocation.setLatitude(location.getLatitude());
                currentLocation.setLongitude(location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        challengedList = new ArrayList<User>();
        usersList = new ArrayList<User>();
        usersAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_multiple_choice, usersList);

        usersListView = (ListView) findViewById(R.id.usersListView);
        usersListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        usersListView.setAdapter(usersAdapter);

        imageButton = (ImageButton) findViewById(R.id.picture);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listViewOpen) {
                    closeListViewAndSaveSelection();
                    return;
                }

                File imageFile = null;
                try {
                    imageFile = ImageHelper.CreateImageFile();
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

        mapButton = (Button) findViewById(R.id.viewMapBtn);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listViewOpen) {
                    closeListViewAndSaveSelection();
                    return;
                }

                if (currentLocation != null) {
                    StringBuilder query = new StringBuilder("geo:0,0?q=")
                                                .append(currentLocation.getLatitude())
                                                .append(",")
                                                .append(currentLocation.getLongitude())
                                                .append("(Your Location)");
                    Uri geoLocation = Uri.parse(query.toString());
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(geoLocation);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Location is missing", Toast.LENGTH_SHORT).show();
                }
            }
        });

        usersButton = (Button) findViewById(R.id.selectUsersBtn);
        usersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listViewOpen) {
                    closeListViewAndSaveSelection();
                } else {
                    usersListView.setVisibility(View.VISIBLE);
                    listViewOpen = true;
                }
            }
        });

        sendButton = (Button) findViewById(R.id.sendChallengeBtn);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listViewOpen) {
                    closeListViewAndSaveSelection();
                    return;
                }

                EditText titleField = (EditText) findViewById(R.id.titleEditText);
                String title = titleField.getText().toString();

                if (title.trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Title is missing", Toast.LENGTH_SHORT).show();
                    return;
                } else if (currentPictureUri == null) {
                    Toast.makeText(getApplicationContext(), "Picture is missing", Toast.LENGTH_SHORT).show();
                    return;
                } else if (currentLocation == null) {
                    Toast.makeText(getApplicationContext(), "Location is missing", Toast.LENGTH_SHORT).show();
                    return;
                } else if (challengedList.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Challenged user(s) missing", Toast.LENGTH_SHORT).show();
                    return;
                }

                Challenge challenge = new Challenge(title, currentUser, currentLocation, challengedList, currentPictureUri.getPath());
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

    private void closeListViewAndSaveSelection() {
        StringBuilder selectedUsersText = new StringBuilder();
        SparseBooleanArray checked = usersListView.getCheckedItemPositions();
        challengedList.clear();

        for (int i = 0; i < checked.size(); i++) {
            int position = checked.keyAt(i);

            if (checked.valueAt(i)) {
                User user = usersAdapter.getItem(position);
                selectedUsersText.append(user.getName()).append(", ");
                challengedList.add(user);
            }
        }

        if (selectedUsersText.length() > 2) {
            TextView usersTextView = (TextView) findViewById(R.id.usersTextView);
            usersTextView.setText(selectedUsersText.substring(0, selectedUsersText.length() - 2));
        }

        usersListView.setVisibility(View.INVISIBLE);
        listViewOpen = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), currentPictureUri);
            } catch (IOException e) {
                Log.e(TAG, "Error: " + e.getMessage());
            }

            if(bitmap != null) {
                Bitmap decodedBitmap = ImageHelper.DecodeSampledBitmapFromResource(currentPictureUri.getPath(), WIDTH, HEIGHT);
                imageButton.setImageBitmap(decodedBitmap);
            }
        } else if (resultCode == RESULT_CANCELED && requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            ImageHelper.DeleteImageFile(currentPictureUri);
            currentPictureUri = null;
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
