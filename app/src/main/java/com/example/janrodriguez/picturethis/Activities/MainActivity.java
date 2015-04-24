package com.example.janrodriguez.picturethis.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.janrodriguez.picturethis.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.List;

public class MainActivity extends BaseGameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SaveCallback saveCallback = new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {

                } else {
                    Log.e("Tag", "Error: " + e.getMessage());
                }
            }
        };

        FindCallback<ParseObject> findCallback = new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                } else {
                    Log.e("Tag", "Error: " + e.getMessage());
                }
            }
        };

        Button openNewActBtn = (Button)findViewById(R.id.open_pacel_act_btn);
        openNewActBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                User newUser = new User("test", "mcTesterson");
//                ArrayList<User> userList = new ArrayList<User>();
//                userList.add(newUser);
//                Challenge newChall = new Challenge("Test title", newUser, new MyGeoPoint(), userList);
//                Response challResp = new Response(newChall, newUser, Response.STATUS_ACCEPTED);
//                Intent intent = new Intent(MainActivity.this, TestParcel.class);
//                intent.putExtra("user", newUser);
//                intent.putExtra("chall", newChall);
//                intent.putExtra("resp", challResp);
//                startActivity(intent);
            }
        });
        findViewById(R.id.sign_out_button).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    public void viewReceivedChallenge(View view) {
        Intent intent = new Intent(this, ReceivedChallengeActivity.class);
        startActivity(intent);
    }

    public void viewSentChallenge(View view) {
        Intent intent = new Intent(this, SentChallengeActivity.class);
        startActivity(intent);
    }

    public void createNewChallenge(View view){
        Intent intent = new Intent(this, CreateChallengeActivity.class);
        startActivity(intent);
    }

    public void viewLoginPage(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void viewMapPage(View view){
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("showRadius", true);
        intent.putExtra("latitude", 42.3579452);
        intent.putExtra("longitude", -71.0937901);
        startActivity(intent);
    }
}
