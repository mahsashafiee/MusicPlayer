package com.example.musicplayer.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicplayer.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PictureUtils {

    public static CustomTarget<Drawable> getTarget(ImageView view) {
        return new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                view.setImageDrawable(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        };
    }

    private static int getDominantColorFast(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
    }

    public static void getDominantColor(MutableLiveData<Integer> data, Bitmap bitmap) {
        Palette.generateAsync(bitmap, palette -> {
            assert palette != null;
            List<Palette.Swatch> swatches = new ArrayList<>(palette.getSwatches());
            Collections.sort(swatches, (swatch1, swatch2) -> swatch2.getPopulation() - swatch1.getPopulation());
            data.setValue(swatches.size() > 0 ? swatches.get(0).getRgb() : getRandomColor());

        });


    }

    private static int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    public static int setBackgroundGradient(Activity context, Bitmap bitmap) {

        View layout = context.findViewById(R.id.container);
        int dominantColor = getDominantColorFast(bitmap);

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{dominantColor, Color.BLACK});
        gd.setCornerRadius(0f);

        layout.setBackgroundDrawable(gd);

        return dominantColor;
    }

    public static void setBackgroundGradient(Activity context, int dominantColor) {

        View layout = context.findViewById(R.id.container);

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{dominantColor, Color.BLACK});
        gd.setCornerRadius(0f);

        layout.setBackground(gd);
    }
}


