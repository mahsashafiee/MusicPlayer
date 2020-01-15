package com.example.musicplayer.Utils;

import android.content.Context;
import android.util.AttributeSet;

import com.makeramen.roundedimageview.RoundedImageView;

public class SquareRoundedImage extends RoundedImageView {

    public SquareRoundedImage(Context context) {
        super(context);
    }

    public SquareRoundedImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareRoundedImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
