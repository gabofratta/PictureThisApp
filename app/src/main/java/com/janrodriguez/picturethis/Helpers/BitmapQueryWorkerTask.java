package com.janrodriguez.picturethis.Helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ViewSwitcher;

import com.parse.ParseException;
import com.parse.ParseFile;

import java.lang.ref.WeakReference;

/**
 * Created by Gabo on 4/29/15.
 */
public class BitmapQueryWorkerTask extends AsyncTask<Integer, Void, Bitmap> {

    static public final String TAG = "BitmapQueryWorkerTask";

    private final WeakReference<ViewSwitcher> switcherReference;
    private final WeakReference<ImageButton> imageButtonReference;
    private ParseFile parseFile;

    public BitmapQueryWorkerTask(ViewSwitcher switcher, ImageButton imageButton, ParseFile parseFile) {
        this.switcherReference = new WeakReference<ViewSwitcher>(switcher);
        this.imageButtonReference = new WeakReference<ImageButton>(imageButton);
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
        if (imageButtonReference != null && bitmap != null) {
            final ImageButton imageButton = imageButtonReference.get();
            if (imageButton != null) {
                imageButton.setImageBitmap(bitmap);
                switcherReference.get().showNext();
            }
        }
    }
}
