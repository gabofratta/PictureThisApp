package com.example.janrodriguez.picturethis.Helpers;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by Gabo on 4/29/15.
 */
public class BitmapCameraWorkerTask extends AsyncTask<Integer, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;
    private Uri pictureUri;
    private int width;
    private int height;

    public BitmapCameraWorkerTask(ImageView imageView, Uri pictureUri, int width, int height) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.pictureUri = pictureUri;
        this.width = width;
        this.height = height;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
        Bitmap decodedBitmap = ImageHelper.DecodeSampledBitmapFromResource(pictureUri.getPath(), width, height);
        return ImageHelper.RotateBitmapIfNeeded(decodedBitmap, pictureUri.getPath());
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                ImageHelper.SaveImage(bitmap, pictureUri);
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
