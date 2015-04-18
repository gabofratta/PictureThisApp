package com.example.janrodriguez.picturethis.Helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by Gabo on 4/17/15.
 */
public class ParseHelper {

    // get location in background before calling this ?
    static public void CreateChallenge(ParseObject[] challengedUsers, String title, double latitude,
                                       double longitude, String filePath) {
        String fileName = new File(filePath).getName();
        byte[] fileBytes = GetFileBytes(filePath);
        ParseFile file = new ParseFile(fileName, fileBytes);

        ParseObject po = new ParseObject("Challenge");
        po.put("title", title);
        po.put("challenger", ParseUser.getCurrentUser());
        po.put("location", new ParseGeoPoint(latitude, longitude));
        po.put("picture", file);
        po.put("active", true);
        po.saveInBackground(); //add callback

        for (ParseObject challenged : challengedUsers) {
            ParseObject poc = new ParseObject("Challenged");
            poc.put("challenge", po);
            poc.put("challenged", challenged);
            poc.saveInBackground(); //add callback
        }
    }

    static public void CreateChallenge(String[] challengedUsers, String title, double latitude,
                                       double longitude, String filePath) {
        String fileName = new File(filePath).getName();
        byte[] fileBytes = GetFileBytes(filePath);
        ParseFile file = new ParseFile(fileName, fileBytes);

        ParseObject po = new ParseObject("Challenge");
        po.put("title", title);
        po.put("challenger", ParseUser.getCurrentUser());
        po.put("location", new ParseGeoPoint(latitude, longitude));
        po.put("picture", file);
        po.put("active", true);
        po.saveInBackground(); //add callback

        for (String challenged : challengedUsers) {
            ParseObject poc = new ParseObject("Challenged");
            poc.put("challenge", po);
            poc.put("challenged", ParseObject.createWithoutData("User", challenged));
            poc.saveInBackground(); //add callback
        }
    }

    static public void CreateResponse(ParseObject challenge, String filePath) {
        String fileName = new File(filePath).getName();
        byte[] fileBytes = GetFileBytes(filePath);
        ParseFile file = new ParseFile(fileName, fileBytes);

        ParseObject po = new ParseObject("Response");
        po.put("responder", ParseUser.getCurrentUser());
        po.put("challenge", challenge);
        po.put("picture", file);
        po.put("status", "pending");
        po.saveInBackground(); //add callback
    }

    static public void CreateResponse(String challenge, String filePath) {
        String fileName = new File(filePath).getName();
        byte[] fileBytes = GetFileBytes(filePath);
        ParseFile file = new ParseFile(fileName, fileBytes);

        ParseObject po = new ParseObject("Response");
        po.put("responder", ParseUser.getCurrentUser());
        po.put("challenge", ParseObject.createWithoutData("Challenge", challenge));
        po.put("picture", file);
        po.put("status", "pending");
        po.saveInBackground(); //add callback
    }

    static public void CreateUser(String username, String email, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.signUpInBackground(); //add callback
    }

    static public void AcceptResponse(ParseObject response) {
        response.put("status", "accepted");
        response.saveInBackground();

        ParseObject poc = response.getParseObject("challenge");
        poc.put("active", false);
        poc.saveInBackground(); //add callback
    }

    static public void AcceptResponse(String response) {
        ParseObject po = ParseObject.createWithoutData("Response", response);
        po.put("status", "accepted");
        po.saveInBackground(); //add callback

        ParseObject poc = po.getParseObject("challenge"); //wont work bc not downloaded
        poc.put("active", false);
        poc.saveInBackground(); //add callback
    }

    static public void DeclineResponse(ParseObject response) {
        response.put("status", "declined");
        response.saveInBackground(); //add callback
    }

    static public void DeclineResponse(String response) {
        ParseObject po = ParseObject.createWithoutData("Response", response);
        po.put("status", "declined");
        po.saveInBackground(); //add callback
    }

    static private byte[] GetFileBytes(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

}
