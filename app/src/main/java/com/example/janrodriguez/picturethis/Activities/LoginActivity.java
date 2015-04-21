package com.example.janrodriguez.picturethis.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.janrodriguez.picturethis.R;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class LoginActivity extends GooglePlusBaseActivity {

    private SignInButton signinBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();
    }

    private void initialize() {
        signinBtn = (SignInButton)findViewById(R.id.sign_in_button);
        signinBtn.setOnClickListener(this);
    }



    //Connected to google acount, start main activity and close this one
    @Override
    public void onConnected(Bundle connectionHint) {

        super.onConnected(connectionHint);

        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}
