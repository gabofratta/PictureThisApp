package com.example.janrodriguez.picturethis;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;

/**
 * Created by Gabo on 4/17/15.
 */
public class PictureThisApp extends Application {

    private static final String APPLICATION_ID = "NJb4JPoE4FuysgID0eFJuoZnpadAbqng69bfO61Z";
    private static final String CLIENT_KEY = "cb4uD5M680fPaPXwPCE2YkuWDiXkATFXxXsBSR7l";

    @Override
    public void onCreate()
    {
        super.onCreate();

        // Parse settings
        ParseACL defaultAcl = new ParseACL();
        defaultAcl.setPublicReadAccess(true);
        defaultAcl.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultAcl, true);

        // Parse init
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
