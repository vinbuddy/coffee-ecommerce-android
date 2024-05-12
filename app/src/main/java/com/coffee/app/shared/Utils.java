package com.coffee.app.shared;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * File: Utils.java
 * Description: Đây là class chứa các phương thức/function tái sử dụng.
 */

public class Utils {
    public static String formatVNCurrency(double value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00 ₫", symbols);
        String formatValue = decimalFormat.format(value);

        return formatValue;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            Bitmap myBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
