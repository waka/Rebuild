package rejasupotaro.rebuild.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public final class DateUtils {
    public static final String TAG = DateUtils.class.getSimpleName();

    private DateUtils() {
    }

    public static int durationToInt(String duration) {
        String[] dateStructure = duration.split(":");

        int sec = 0;
        for (int i = 0; i < dateStructure.length - 1; i++) {
            sec = (sec + Integer.valueOf(dateStructure[i])) * 60;
        }
        sec += Integer.valueOf(dateStructure[dateStructure.length - 1]);

        return sec * 1000;
    }

    public static String formatCurrentTime(int currentTime) {
        return DurationFormatter.format(currentTime);
    }

    public static Date pubDateToDate(String source) {
        try {
            DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
            return format.parse(source);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    /**
     * FIXME: This is terrible code... I should fix it soon.
     *
     * @param date pubDate
     * @return output instance of String
     */
    public static String dateToString(Date date) {
        int month = date.getMonth() + 1;
        int day = date.getDate();
        int year = 1900 + date.getYear();
        return monthToName(month) + " " + (day < 10 ? "0" + day : day) + " " + year;
    }

    public static String monthToName(int month) {
        switch (month) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return "";
        }
    }

    private static final class DurationFormatter {

        public static String format(int source) {
            StringBuilder stringBuilder = new StringBuilder();
            Formatter formatter = new Formatter(stringBuilder, Locale.getDefault());

            int totalSeconds = source / 1000;

            int seconds = totalSeconds % 60;
            int minutes = (totalSeconds / 60) % 60;
            int hours = totalSeconds / 3600;

            stringBuilder.setLength(0);
            if (hours > 0) {
                return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
            } else {
                return formatter.format("%02d:%02d", minutes, seconds).toString();
            }
        }

        private DurationFormatter() {
        }
    }
}
