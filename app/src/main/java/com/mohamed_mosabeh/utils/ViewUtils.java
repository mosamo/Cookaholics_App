package com.mohamed_mosabeh.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;

public class ViewUtils {
    public static void IfDataExistsHideProgressBar (int entries, ProgressBar p) {
        if (entries > 0)
            if (p != null)
                p.setVisibility(View.GONE);
    }
    
    public static void getSnackBar(Activity activity, String string) {
        final ViewGroup root = (ViewGroup) ((ViewGroup) activity
                .findViewById(android.R.id.content)).getChildAt(0);
        
        Snackbar.make(root, string, Snackbar.LENGTH_SHORT).show();
    }
}
