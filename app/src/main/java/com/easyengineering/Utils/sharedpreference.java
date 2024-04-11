package com.easyengineering.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class sharedpreference {

    private Context mContext;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    public static final String PREFERENCE = "User";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    // Constructor with the same name as the class
    public sharedpreference(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREFERENCE, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setIsFirstTimeLaunch(boolean isFirstTimeLaunch) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTimeLaunch);
        editor.apply();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setSharedPreference(String name, String value) {
        SharedPreferences settings = mContext.getSharedPreferences(PREFERENCE, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(name, value);
        editor.apply();
    }

    public String getSharedPreferences(String name) {
        SharedPreferences settings = mContext.getSharedPreferences(PREFERENCE, 0);
        return settings.getString(name, "");
    }

    public void removePreference(String name) {
        SharedPreferences settings = mContext.getSharedPreferences(PREFERENCE, 0);
        settings.edit().remove(name).apply();
    }

    public void clearPreference() {
        SharedPreferences settings = mContext.getSharedPreferences(PREFERENCE, 0);
        settings.edit().clear().apply();
    }
}


