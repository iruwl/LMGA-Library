package com.lmga.library;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import java.util.List;

/**
 * Created by irul on 8/10/17
 */

public class Commons {
    public static void checkUpdate(@NonNull Context context) {
        if (Utils.isOnline(context)) {
            new AppUpdater(context)
                    .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                    .setTitleOnUpdateAvailable("Update Aplikasi")
                    .setContentOnUpdateAvailable("Versi terbaru dari aplikasi ini sudah tersedia " +
                            "di PlayStore, silahkan lakukan update mendapatkan versi yang lebih " +
                            "stabil dan fitur terbaru.")
                    .setButtonUpdate("Update sekarang")
                    .setButtonDismiss("Update nanti")
                    .setButtonDoNotShowAgain("Jangan tampilkan pesan ini lagi")
                    .start();
        }
    }

    public static void showAbout(@NonNull final Context context) {
        new MaterialStyledDialog.Builder(context)
                .setTitle(":: LMGA")
                .setDescription("LMGA merupakan salah satu pengembang aplikasi Android " +
                        "yang masih baru. Dengan semangat dan kerja keras berusaha " +
                        "untuk menghadirkan aplikasi-aplikasi yang bermanfaat bagi Anda " +
                        "dan gratis tentunya.\n\n" +
                        "Jika Anda telah menggunakan dan terbantu dengan aplikasi yang kami " +
                        "buat maka itu merupakan kabar yang menggembirakan untuk kami.\n\n" +
                        "Akhir kata, dukung kami agar dapat terus berkarya. Terimakasih."
                )
                .setHeaderDrawable(R.drawable.banner)
                .withDialogAnimation(true)
                .setCancelable(false)
                .setPositiveText("Tutup")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeText("FB Page")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        showFbPage(context);
                    }
                })
                .show();
    }

    // http://stackoverflow.com/a/34564284
    public static void showFbPage(@NonNull Context context) {
        String facebookUrl = "https://www.facebook.com/lmga2017";
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                //newer versions of fb app
                facebookUrl = "fb://facewebmodal/f?href=https://www.facebook.com/lmga2017";
            } else {
                //older versions of fb app
                facebookUrl = "fb://page/lmga2017";
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("irulApp", "Error: " + e.getMessage());
        }

        Intent fbIntent = new Intent(Intent.ACTION_VIEW);
        fbIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        fbIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        fbIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        fbIntent.setData(Uri.parse(facebookUrl));
        context.startActivity(fbIntent);
    }

    public static void showDevStore(@NonNull Context context) {
        final String developerID = "6711078644974658535";
        final String publisherName = "LMGA";

        Intent appsIntent = new Intent(Intent.ACTION_VIEW);
        appsIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        appsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        appsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            // appsIntent.setData(Uri.parse("market://search?q=pub:" + publisherName));
            appsIntent.setData(Uri.parse("market://dev?id=" + developerID));
            context.startActivity(appsIntent);
        } catch (android.content.ActivityNotFoundException anfe) {
            // appsIntent.setData(Uri.parse("https://play.google.com/store/search?q=pub:" + publisherName));
            appsIntent.setData(Uri.parse("https://play.google.com/store/apps/dev?id=" + developerID));
            context.startActivity(appsIntent);
        }
    }

    public static void showAppStore(@NonNull Context context) {
        // String appId = "id.web.echoirul.profilpns";
        String appId = context.getPackageName();
        boolean marketFound = false;

        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appId));

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager().queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp : otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName.equals("com.android.vending")) {
                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                // task reparenting if needed
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                // if the Google Play was already open in a search result
                // this make sure it still go to the app page you requested
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // make sure it does NOT open in the stack of your activity
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;
            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appId));
            webIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            webIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(webIntent);
        }
    }

    @NonNull
    public static ProgressDialog dialogWait(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Harap tunggu...");
        return dialog;
    }
}
