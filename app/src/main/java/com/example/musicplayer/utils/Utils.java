package com.example.musicplayer.utils;

import android.app.Activity;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.Window;
import android.view.WindowManager;

public class Utils {
    public static Spanned buildSpanned(String res) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                Html.fromHtml(res, Html.FROM_HTML_MODE_LEGACY) :
                Html.fromHtml(res);
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
