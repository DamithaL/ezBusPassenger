package ezbus.mit20550588.manager.util;

import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Converters {
    @TypeConverter
    public static LatLng fromString(String value) {
        if (value == null) {
            return null;
        }

        String[] parts = value.split(",");
        double lat = Double.parseDouble(parts[0]);
        double lng = Double.parseDouble(parts[1]);
        return new LatLng(lat, lng);
    }

    @TypeConverter
    public static String toString(LatLng latLng) {
        if (latLng == null) {
            return null;
        }

        return latLng.latitude + "," + latLng.longitude;
    }

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String timestampToFormattedTime(Long timestamp) {
        if (timestamp == null) {
            return null;
        }

        // Create a SimpleDateFormat object
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        // Convert timestamp to Date
        Date date = new Date(timestamp);

        // Format the Date as a string
        return sdf.format(date);
    }

    @TypeConverter
    public static Long formattedTimeToTimestamp(String formattedTime) {
        if (formattedTime == null) {
            return null;
        }

        // Create a SimpleDateFormat object
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        try {
            // Parse the formatted time string to obtain a Date
            Date date = sdf.parse(formattedTime);

            // Return the timestamp
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
