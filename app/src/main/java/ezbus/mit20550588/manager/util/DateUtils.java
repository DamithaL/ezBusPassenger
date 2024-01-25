package ezbus.mit20550588.manager.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Format the date as per your requirement
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        return dateFormat.format(currentDate);
    }

    public static long getTomorrowNoonTimestamp() {
        // Get tomorrow's date with the time set to 12:00 noon
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        tomorrow.set(Calendar.HOUR_OF_DAY, 12);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.set(Calendar.MILLISECOND, 0);

        // Convert the date to a Unix timestamp
        return tomorrow.getTimeInMillis() / 1000;
    }

    public static String formatDateToString(Date date) {
        if (date == null) {
            return ""; // Handle null date as needed
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        return sdf.format(date);
    }

    public static Date parseDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace(); // Handle the exception as needed
            return null;
        }
    }



}
