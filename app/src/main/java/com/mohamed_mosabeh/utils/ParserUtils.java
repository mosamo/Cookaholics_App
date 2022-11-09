package com.mohamed_mosabeh.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ParserUtils {
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
    
    public static long getLastWeekTimestamp() {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(timestamp.getTime());
        calender.add(Calendar.DAY_OF_WEEK, -7);
        return calender.getTimeInMillis();
    }
}
