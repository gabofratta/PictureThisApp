package com.example.janrodriguez.picturethis.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.janrodriguez.picturethis.Helpers.Challenge;
import com.example.janrodriguez.picturethis.Helpers.Response;
import com.example.janrodriguez.picturethis.Helpers.User;
import com.example.janrodriguez.picturethis.R;

public class TestParcel extends ActionBarActivity {

    private static String TAG = "TEST_PARCEL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_parcel);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_parcel, menu);

        Intent i = getIntent();
        User testUser = (User)i.getParcelableExtra("user");
        Log.d(TAG, testUser.getId());
        Log.d(TAG, testUser.getName());
        Challenge chall = (Challenge)i.getParcelableExtra("chall");
        Log.d(TAG, chall.getId());
        Log.d(TAG, chall.getTitle());
        Log.d(TAG, chall.getLocation().toString());
        Log.d(TAG, chall.getChallengedList().toString());
        Log.d(TAG, String.valueOf(chall.isActive()));
        Log.d(TAG, String.valueOf(chall.isMultiplayer()));
        Log.d(TAG, chall.getCreatedAt().toString());
        Response response = (Response)i.getParcelableExtra("resp");
        Log.d(TAG, response.getStatus());
        Log.d(TAG, response.getResponder().getName());
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
