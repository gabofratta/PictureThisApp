package com.janrodriguez.picturethis.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ViewSwitcher;

import com.janrodriguez.picturethis.Helpers.Challenge;
import com.janrodriguez.picturethis.Helpers.ParseHelper;
import com.janrodriguez.picturethis.Helpers.ParseTableConstants;
import com.janrodriguez.picturethis.Helpers.Response;
import com.janrodriguez.picturethis.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

public class LargePictureActivity extends AppCompatActivity {

    private static final String TAG = "LargePictureActivity";

    public static final String RESPONSE_INTENT = "Response";

    ImageButton imageButton;
    ViewSwitcher switcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_picture);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageButton = (ImageButton)findViewById(R.id.largeImage);
        switcher = (ViewSwitcher) findViewById(R.id.switcher);

        Intent intent = getIntent();
        Challenge challenge = (Challenge)intent.getParcelableExtra(Challenge.INTENT_TAG);
        if (challenge!=null){

            ParseHelper.GetChallengeImage(challenge, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        final ParseFile parseFile = parseObject.getParseFile(ParseTableConstants.CHALLENGE_PICTURE);

                        byte []bytes = new byte[0];
                        try {
                            bytes = parseFile.getData();
                            ImageProcess2 imageProcess = new ImageProcess2();
                            imageProcess.execute(bytes);

                            imageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            });
                        } catch (ParseException e1) {
                            finish();
                            Log.e(TAG, e1.toString());
                        }

                    } else {
                        finish();
                        Log.e(TAG, e.getMessage());
                    }

                }
            });
            return;
        }

        Response response = (Response)intent.getParcelableExtra(RESPONSE_INTENT);

        if (response!=null){
            ParseHelper.GetResponseImage(response, new GetCallback<ParseObject>() {

                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        final ParseFile parseFile = parseObject.getParseFile(ParseTableConstants.RESPONSE_PICTURE);

                        byte []bytes = new byte[0];
                        try {
                            bytes = parseFile.getData();
                            ImageProcess2 imageProcess = new ImageProcess2();
                            imageProcess.execute(bytes);

                            imageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            });
                        } catch (ParseException e1) {
                            finish();
                            Log.e(TAG, e1.toString());
                        }

                    } else {
                        finish();
                        Log.e(TAG, e.getMessage());
                    }
                }
            });

        }else{
            Log.e(TAG, "No extra for intent");
            finish();
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class ImageProcess2 extends AsyncTask<byte[], Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(byte[]... params) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(params[0], 0, params[0].length);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageButton.setImageBitmap(bitmap);
            switcher.showNext();
        }
    }
}
