package com.coffee.app.shared;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

/**
 * File: Utils.java
 * Description: Đây là class chứa các phương thức/function tái sử dụng.
 */

public class Utils {
    public static String formatVNCurrency(double value) {
        Locale locale = new Locale("vi", "VN");
        java.util.Currency currency =  java.util.Currency.getInstance(locale);
        java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(locale);

        format.setCurrency(currency);
        format.setMaximumFractionDigits(0);

        return format.format(value);
    }

    public static String generateOrderId() {
        String prefix = "ORDER";
        String datePart = Long.toString(System.currentTimeMillis());
        int maxRandomLength = 20 - prefix.length() - datePart.length();

        String randomPart = generateRandomString(maxRandomLength);

        return prefix + datePart + randomPart;
    }

    private static String generateRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder randomString = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            randomString.append(characters.charAt(index));
        }

        return randomString.toString();
    }


    public static String formatDateTimeISO8601(String dateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Parse the input date-time string
        Date date = null;
        try {
            date = inputFormat.parse(dateTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Define the desired output format
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        outputFormat.setTimeZone(TimeZone.getDefault());

        // Format the date to the desired format
        String formattedDateTime = outputFormat.format(date);

        return formattedDateTime;
    }


    public static String getCurrentDateTimeString() {
        // Create a new Date object
        Date currentDate = new Date();

        // Create a SimpleDateFormat object with the desired date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Format the current date
        String formattedDate = sdf.format(currentDate);

        return formattedDate; // Output: YYYY-MM-DD HH:mm:ss
    }
}
