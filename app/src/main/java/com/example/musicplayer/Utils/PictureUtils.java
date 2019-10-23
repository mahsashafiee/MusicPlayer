package com.example.musicplayer.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

public class PictureUtils {

    private static Bitmap getScaledBitmap(String path, int desWidth, int desHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        int inSampleSize = Math.min(srcWidth/desWidth, srcHeight/desHeight);

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getScaledBitmap(String path, View view) {

        return getScaledBitmap(path, view.getLayoutParams().width, view.getLayoutParams().height);
    }
}
