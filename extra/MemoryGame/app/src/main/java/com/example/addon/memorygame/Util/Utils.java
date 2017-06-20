package com.example.addon.memorygame.Util;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by Roshan on 12/17/2016.
 */

public class Utils {

    public static Drawable GetImage(Context c, String ImageName) {
        return c.getResources().getDrawable(c.getResources().getIdentifier(ImageName, "drawable", c.getPackageName()));
    }


}
