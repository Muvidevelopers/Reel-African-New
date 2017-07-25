package com.release.reelAfrican.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Created by Muvi on 5/15/2017.
 */

public  class AppUpdate {

    public interface clickOnAppUpdate {
        public boolean wantsToUpdate(boolean result);
    }

    private static clickOnAppUpdate listener;
    static  Context context;
    public static boolean checkForAppUpdate(Context context) {
        String existingVersion;
        String newVersion = null;
        VersionChecker versionChecker = new VersionChecker();
        try {
            newVersion = versionChecker.execute().get();
            Log.v("Nihar", "" + newVersion + "/");
        } catch (Exception e) {
            e.printStackTrace();
        }

        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Log.v("SUBHA","package name = " + pInfo);

        assert pInfo != null;
        existingVersion = pInfo.versionName;
        //below are used for 1.1.1 type of version
        if (TextUtils.isEmpty(existingVersion) || TextUtils.isEmpty(newVersion)) {
            return false;
        }
        boolean newVersionIsGreater = false;
        String[] existingVersionArray = existingVersion.split("\\.");
        String[] newVersionArray = newVersion.split("\\.");

        int maxIndex = Math.max(existingVersionArray.length, newVersionArray.length);
        for (int i = 0; i < maxIndex; i++) {
            int newValue;
            int oldValue;
            try {
                oldValue = Integer.parseInt(existingVersionArray[i]);
            } catch (ArrayIndexOutOfBoundsException e) {
                oldValue = 0;
            }
            try {
                newValue = Integer.parseInt(newVersionArray[i]);
            } catch (ArrayIndexOutOfBoundsException e) {
                newValue = 0;
            }
            if (oldValue < newValue) {
                newVersionIsGreater = true;

            }
        }
        return newVersionIsGreater;
    }


    public static void showAppUpdateAlert(final Context context) {
        listener = (clickOnAppUpdate) context;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Available")
                .setCancelable(false)
                .setMessage("An Update is Available .Would you like to update now ?")
                .setPositiveButton("update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.wantsToUpdate(true);
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=com.release.appupdate"));
                        context.startActivity(intent);

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        listener.wantsToUpdate(false);
                        dialog.dismiss();
                        /*public void callOtherMethod(){
                            SplashScreen.doStuff();
                        }*/
                      /*  Intent intent = new Intent(context,MainActivity.class);
                        context.startActivity(intent);*/



                    }
                });
        builder.create();
        builder.show();
    }


    private static class VersionChecker extends AsyncTask<String, String, String> {

        String newVersion;

        @Override
        protected String doInBackground(String... params) {

            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.getPackageName().trim())
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return newVersion;
        }
    }
}
