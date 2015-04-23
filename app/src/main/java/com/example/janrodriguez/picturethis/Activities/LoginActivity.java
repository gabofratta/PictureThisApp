package com.example.janrodriguez.picturethis.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.janrodriguez.picturethis.R;
import com.google.android.gms.common.SignInButton;

public class LoginActivity extends BaseGameActivity {

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
    public void onSignInSucceeded() {

        super.onSignInSucceeded();

        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}
