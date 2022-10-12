package com.mohamed_mosabeh.utils;

import android.view.View;
import android.widget.ProgressBar;

public class ViewUtil {
    public static void IfDataExistsHideProgressBar (int entries, ProgressBar p) {
        if (entries > 0)
            if (p != null)
                p.setVisibility(View.GONE);
    }
}
