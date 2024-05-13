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
import java.util.Locale;

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
}
