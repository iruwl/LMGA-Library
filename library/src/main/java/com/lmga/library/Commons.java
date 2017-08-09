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
                // newer versions of fb app
                facebookUrl = "fb://facewebmodal/f?href=https://www.facebook.com/lmga2017";
            } else {
                // older versions of fb app
                facebookUrl = "fb://page/lmga2017";
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("irulApp", "Error: " + e.getMessage());
        }

        openClearIntent(context, new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
    }

    public static void showDevStore(@NonNull Context context) {
        final String developerID = "6711078644974658535";
        Intent intent = new Intent(Intent.ACTION_VIEW);

        try {
            intent.setData(Uri.parse("market://dev?id=" + developerID));
            openClearIntent(context, intent);
        } catch (android.content.ActivityNotFoundException anfe) {
            intent.setData(Uri.parse("https://play.google.com/store/apps/dev?id=" + developerID));
            openClearIntent(context, intent);
        }
    }

    public static void showAppList(@NonNull Context context) {
        final String publisherName = "LMGA";
        Intent intent = new Intent(Intent.ACTION_VIEW);

        try {
            intent.setData(Uri.parse("market://search?q=pub:" + publisherName));
            openClearIntent(context, intent);
        } catch (android.content.ActivityNotFoundException anfe) {
            intent.setData(Uri.parse("https://play.google.com/store/search?q=pub:" + publisherName));
            openClearIntent(context, intent);
        }
    }

    public static void showAppStore(@NonNull Context context) {
        // String appId = "id.web.echoirul.profilpns";
        String appId = context.getPackageName();
        boolean marketFound = false;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appId));

        // find all applications able to handle our intent
        final List<ResolveInfo> otherApps = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo otherApp : otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName.equals("com.android.vending")) {
                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName, otherAppActivity.name);

                // this make sure only the Google Play app is allowed to intercept the intent
                intent.setComponent(componentName);
                openClearIntent(context, intent);

                marketFound = true;
                break;
            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            openClearIntent(context, new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appId)));
        }
    }

    public static void shareApp(@NonNull Context context, String shortUrlApiKey) {
        String appName = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
        String appId = context.getPackageName();
        String appUrl = "https://play.google.com/store/apps/details?id=" + appId;

        String appShortUrl = ShortURL.makeShort(appUrl, shortUrlApiKey);
        String shareBody = "Unduh aplikasi \"%s\" pada link berikut: %s";
        String shareBodyText = String.format(shareBody, appName, appShortUrl);

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, appName);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
        context.startActivity(Intent.createChooser(intent, "Silahkan pilih media"));
    }

    @NonNull
    public static ProgressDialog dialogWait(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Harap tunggu...");
        return dialog;
    }

    public static void openClearIntent(Context context, Intent intent) {
        // task reparenting if needed
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        // if the Google Play was already open in a search result
        // this make sure it still go to the app page you requested
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // make sure it does NOT open in the stack of your activity
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // this make sure only the Google Play app is allowed to
        context.startActivity(intent);
    }
}
