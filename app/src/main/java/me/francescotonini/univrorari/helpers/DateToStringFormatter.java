package me.francescotonini.univrorari.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateToStringFormatter {
    public static String getETAString(long endTimestamp) {
        Date now = new Date();
        Date end = new Date(endTimestamp);

        long secs = (end.getTime() - now.getTime()) / 1000;
        long hours = secs / 3600;
        secs = secs % 3600;
        long mins = (secs / 60) + 1; // +1 --> approx.

        String output = "";
        if (hours > 0) {
            output = hours + "h ";
        }
        output += mins + "m";

        return output;
    }

    public static String getTimeString(long endTimestamp) {
        Date end = new Date(endTimestamp);

        return new SimpleDateFormat("HH:mm").format(end);
    }
}
