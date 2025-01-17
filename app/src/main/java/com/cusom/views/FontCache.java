package com.cusom.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;


import java.util.HashMap;

import in.newdevpoint.sschat.BuildConfig;

public class FontCache {

    private static String LOGTAG = FontCache.class.getName();
    private static HashMap<String, Typeface> fontCache = new HashMap<>();

    public static Typeface getTypeface(String fontName, Context context) {

        Typeface typeFace = fontCache.get(fontName);
        if (typeFace == null) {
            try {
                typeFace = Typeface.createFromAsset(context.getAssets(), fontName);
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    Log.d(LOGTAG, e.toString());
                return null;
            }
            fontCache.put(fontName, typeFace);
        }
        return typeFace;
    }

}
