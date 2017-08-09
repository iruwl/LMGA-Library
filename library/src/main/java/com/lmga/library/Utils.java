package com.lmga.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by irul on 8/10/17
 */

public class Utils {
    public static String getApplicationID(@NonNull Context context) {
        return context.getPackageName();
    }

    public static PackageInfo getPackageInfo(@NonNull Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(getApplicationID(context), 0);
    }

    public static boolean isOnline(@NonNull Context context) {
        // Cek koneksi
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        // Cek internetnya
        int responseCode = 0;
        try {
            // http://stackoverflow.com/a/17063831
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
            StrictMode.setThreadPolicy(policy);

            URL url = new URL("http://www.google.com");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            responseCode = http.getResponseCode();
            if (responseCode == 200) {
                Log.d("irulApp", "Connection: OK");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return netInfo != null && netInfo.isConnectedOrConnecting() && responseCode == 200;
    }

    public static boolean isFirstTimeLaunch(@NonNull Context context) {
        SharedPreferences pref = context.getSharedPreferences("FIRST_TIME_LAUNCH", 0);
        boolean val = pref.getBoolean("IsFirstTimeLaunch", true);
        if (val) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("IsFirstTimeLaunch", false);
            editor.apply();
            return true;
        } else {
            return false;
        }
    }

    public static String getSimpleDate() {
        String format = "yyyyMMdd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String getSimpleDate(@NonNull String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(new Date());
    }
}
