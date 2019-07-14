package com.prelimtek.android.basecomponents;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.content.res.Resources;
import android.content.res.Resources.Theme;

public class ResourcesUtils {
    @SuppressWarnings("deprecation")
    public static int getColor(View view, int id){
        Theme theme =  view.getContext().getTheme();
        Resources resources = view.getResources();
        int color = -1;

        if (Build.VERSION.SDK_INT < 23) {
            color = resources.getColor(id);
        }else{
            color = resources.getColor(id,theme);
        }

        return color;
    }
}
