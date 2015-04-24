package com.example.janrodriguez.picturethis.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
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
import android.widget.RelativeLayout;
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
    private Uri tempPictureUri;
    private boolean listViewOpen;

    private LocationListener locationListener;
    private LocationManager locationManager;
    private MyGeoPoint currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_challenge);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initializeUiComponents();
        initializeLocationServices();
    }

    @Override
    public void onSignInSucceeded() {
        super.onSignInSucceeded();
        initializeUsersList();
    }

    private void initializeUsersList() {
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
                    Log.e(TAG, "Error requesting circles: " + peopleData.getStatus());
                }
            }
        };

        Plus.PeopleApi.loadVisible(getApiClient(), null).setResultCallback(resultCallback);
    }

    private void initializeUiComponents() {
        listViewOpen = false;

        challengedList = new ArrayList<User>();
        usersList = new ArrayList<User>();
        usersAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_multiple_choice, usersList);

        usersListView = (ListView) findViewById(R.id.usersListView);
        usersListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        usersListView.setAdapter(usersAdapter);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.root);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listViewOpen) {
                    closeListViewAndSaveSelection();
                    return;
                }
            }
        });

        EditText titleEditText = (EditText) findViewById(R.id.titleEditText);
        titleEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listViewOpen) {
                    closeListViewAndSaveSelection();
                    return;
                }
            }
        });

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

                tempPictureUri = Uri.fromFile(imageFile);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null && imageFile != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, tempPictureUri);
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
                    Intent intent = new Intent(CreateChallengeActivity.this, MapActivity.class);
                    intent.putExtra(MapActivity.INTENT_SHOW_RADIUS, false);
                    intent.putExtra(MapActivity.INTENT_LATITUDE, currentLocation.getLatitude());
                    intent.putExtra(MapActivity.INTENT_LONGITUDE, currentLocation.getLongitude());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.location_missing), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), getString(R.string.title_missing), Toast.LENGTH_SHORT).show();
                    return;
                } else if (currentLocation == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.location_missing), Toast.LENGTH_SHORT).show();
                    return;
                } else if (challengedList.size() == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.challenged_missing), Toast.LENGTH_SHORT).show();
                    return;
                } else if (currentPictureUri == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.picture_missing), Toast.LENGTH_SHORT).show();
                    return;
                }

                Challenge challenge = new Challenge(title, currentUser, currentLocation, challengedList, currentPictureUri.getPath());
                ParseHelper.CreateChallenge(challenge, new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), getString(R.string.challenge_created), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Error: " + e.getMessage());
                        }
                    }
                });

                finish();
            }
        });
    }

    private void initializeLocationServices() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(CreateChallengeActivity.this);
            dialog.setMessage(getString(R.string.enable_network_location));

            dialog.setPositiveButton(getString(R.string.change_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent intent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });

            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {}
            });

            dialog.show();
        }

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
    }

    private void closeListViewAndSaveSelection() {
        StringBuilder usersDisplayText = new StringBuilder();
        SparseBooleanArray checked = usersListView.getCheckedItemPositions();
        challengedList.clear();

        for (int i = 0; i < checked.size(); i++) {
            int position = checked.keyAt(i);

            if (checked.valueAt(i)) {
                User user = usersAdapter.getItem(position);
                usersDisplayText.append(user.getName()).append(", ");
                challengedList.add(user);
            }
        }

        TextView usersTextView = (TextView) findViewById(R.id.usersTextView);
        String displayText = (usersDisplayText.length() > 2) ? usersDisplayText.substring(0, usersDisplayText.length() - 2) : "";
        usersTextView.setText(displayText);

        usersListView.setVisibility(View.INVISIBLE);
        listViewOpen = false;
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
                imageButton.setImageBitmap(decodedBitmap);
            }
        } else if (resultCode == RESULT_CANCELED && requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            ImageHelper.DeleteImageFile(tempPictureUri);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_challenge, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            case R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}