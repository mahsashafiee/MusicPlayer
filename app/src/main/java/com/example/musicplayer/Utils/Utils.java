package com.example.musicplayer.Utils;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

public class Utils {
    public static Spanned buildSpanned(String res) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                Html.fromHtml(res, Html.FROM_HTML_MODE_LEGACY) :
                Html.fromHtml(res);
    }
}
