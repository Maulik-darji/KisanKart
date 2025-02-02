package com.android.agrifarm;

import android.content.Context;
import android.text.format.DateFormat; // Import the correct DateFormat class
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class Utils {

    public static final String AD_STATUS_AVAILABLE = "AVAILABLE";
    public static final String AD_STATUS_SOLD = "SOLD";

    public static final String[] categories = {
            "Cereal/Grains",
            "Pulses/Legumes",
            "Fruits",
            "Vegetables",
    };

    public static final String[] condition = {"10(kg)", "20(kg)", "30(kg)", "40(kg)", "50(kg)", "60(kg)", "70(kg)", "80(kg)", "90(kg)", "100(kg)","150(kg)","200(kg)"};

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * A function to get the current timestamp
     * @return Return the current timestamp as long datatype
     */
    public static long getTimestamp() {
        return System.currentTimeMillis();
    }

    public static String formatTimestampDate(Long timestamp) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);

        // Use android.text.format.DateFormat
        String date = DateFormat.format("dd/MM/yyyy", calendar).toString();

        return date;
    }
}
