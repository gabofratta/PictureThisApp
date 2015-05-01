package com.janrodriguez.picturethis.Helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;

import java.lang.ref.WeakReference;

/**
 * Created by Gabo on 4/29/15.
 */
public class BitmapQueryWorkerTask extends AsyncTask<Integer, Void, Bitmap> {

    static public final String TAG = "BitmapQueryWorkerTask";

    private final WeakReference<ImageView> imageViewReference;
    private ParseFile parseFile;

    public BitmapQueryWorkerTask(ImageView imageView, ParseFile parseFile) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.parseFile = parseFile;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
        byte[] data = new byte[0];
        try {
            data = parseFile.getData();
        } catch (ParseException e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }

        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
