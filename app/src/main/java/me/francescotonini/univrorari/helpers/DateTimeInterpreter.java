package me.francescotonini.univrorari.helpers;

import java.util.Calendar;

public class DateTimeInterpreter implements com.alamkanak.weekview.DateTimeInterpreter {
    @Override public String interpretDate(Calendar date) {
        String dayOfWeek = "";
        switch (date.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                dayOfWeek = "DOM";
                break;
            case 2:
                dayOfWeek = "LUN";
                break;
            case 3:
                dayOfWeek = "MAR";
                break;
            case 4:
                dayOfWeek = "MER";
                break;
            case 5:
                dayOfWeek = "GIO";
                break;
            case 6:
                dayOfWeek = "VEN";
                break;
            case 7:
                dayOfWeek = "SAB";
                break;
        }

        return String.format("%s %s/%s", dayOfWeek.toUpperCase(), date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.MONTH) + 1);
    }

    @Override public String interpretTime(int hour) {
        return String.format("%s:00", hour);
    }
}

