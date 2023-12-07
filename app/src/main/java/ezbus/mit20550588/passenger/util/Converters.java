package ezbus.mit20550588.passenger.util;

import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

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
}
