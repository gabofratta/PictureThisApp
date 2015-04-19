package com.example.janrodriguez.picturethis.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.janrodriguez.picturethis.Helpers.Challenge;
import com.example.janrodriguez.picturethis.Helpers.MyGeoPoint;
import com.example.janrodriguez.picturethis.Helpers.Response;
import com.example.janrodriguez.picturethis.Helpers.User;
import com.example.janrodriguez.picturethis.R;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button openNewActBtn = (Button)findViewById(R.id.open_pacel_act_btn);
        openNewActBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User newUser = new User("test", "mcTesterson");
                ArrayList<User> userList = new ArrayList<User>();
                userList.add(newUser);
                Challenge newChall = new Challenge("Test title", new MyGeoPoint(), userList);
                Response challResp = new Response(newChall, newUser, Response.STATUS_ACCEPTED);
                Intent intent = new Intent(MainActivity.this, TestParcel.class);
                intent.putExtra("user", newUser);
                intent.putExtra("chall", newChall);
                intent.putExtra("resp", challResp);
                startActivity(intent);

            }
        });
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

    public void viewReceivedActivity(View view) {
        Intent intent = new Intent(this, ReceivedActivity.class);
        startActivity(intent);
    }

    public void viewSentActivity(View view) {
        Intent intent = new Intent(this, SentActivity.class);
        startActivity(intent);
    }
}
