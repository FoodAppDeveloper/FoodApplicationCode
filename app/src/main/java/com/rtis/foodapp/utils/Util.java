package com.rtis.foodapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.io.File;

/**
 * Created by admin on 2/12/16.
 */
public class Util {

    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;
    public static final int SUNDAY = 7;

    public static final int BREAKFAST = 1;
    public static final int MORNING_SNACK = 2;
    public static final int LUNCH = 3;
    public static final int AFTERNOON_SNACK = 4;
    public static final int DINNER = 5;
    public static final int EVENING_SNACK = 6;

    public static final String BREAKFAST_STRING = "Breakfast";
    public static final String MORNING_SNACK_STRING = "Snack (morning)";
    public static final String LUNCH_STRING = "Lunch";
    public static final String AFTERNOON_SNACK_STRING = "Snack (afternoon)";
    public static final String DINNER_STRING = "Dinner";
    public static final String EVENING_SNACK_STRING = "Snack (evening)";

    public static final String BREAKFAST_FILE = "b";
    public static final String MORNING_SNACK_FILE = "ms";
    public static final String LUNCH_FILE = "l";
    public static final String AFTERNOON_SNACK_FILE = "as";
    public static final String DINNER_FILE = "d";
    public static final String EVENING_SNACK_FILE = "es";

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    public static Typeface getCustomTypeFace(Context context, int index) {
        String fontPath = null;
        Typeface tf = null;
        try {
            switch (index) {
                case 0:
                    fontPath = "fonts/Roboto-Regular.ttf";
                    break;
                case 1:
                    fontPath = "fonts/GothamLight.ttf";
            }
            // Loading Font Face
            tf = Typeface.createFromAsset(context.getAssets(), fontPath);
        } catch (Exception e) {
            tf = Typeface.DEFAULT;
        }
        return tf;
    }

    public static File getStorageDirectory(Context ctx) {
        //Logger.v(" Ext Storage "+ isExternalStorageAvailable());
        File storage = ctx.getExternalFilesDir(null);
        if(storage!=null && !storage.exists())
            storage.mkdir();
        return storage;
    }

    public static Bitmap scaleDown(Bitmap originalBitmap, float maxSize, boolean filter) {
        float ratio = Math.min((float) maxSize / originalBitmap.getWidth(), (float) maxSize / originalBitmap.getHeight());
        int prWidth = Math.round((float) ratio * originalBitmap.getWidth());
        int prHeight = Math.round((float) ratio * originalBitmap.getHeight());
        Bitmap bitmap = Bitmap.createScaledBitmap(originalBitmap, prWidth, prHeight, filter);
        return bitmap;
    }


    public static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    public static boolean isPhoneNoValid(String phno) {
        //TODO: Replace this with your own logic
        return phno.length() == 10;
    }

    public static boolean isNameValid(String name) {
        //TODO: Replace this with your own logic
        return name.length()>0;
    }

    public static void showToast(Context ctx, String message)
    {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }


}
