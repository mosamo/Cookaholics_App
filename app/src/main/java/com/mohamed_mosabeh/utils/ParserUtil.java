package com.mohamed_mosabeh.utils;

import java.util.ArrayList;

public class ParserUtil {
    public static String parseTags(ArrayList<String> tags) {
        if (tags != null && tags.size() > 0) {
            String str = "";
            int i = 0;
            for (String tag : tags) {
                str += "#" + tag + " ";
            }
            return str;
        }
        return "No Tags";
    }
}
