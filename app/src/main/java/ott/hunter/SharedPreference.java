package ott.hunter;


import android.content.Context;
import android.content.SharedPreferences;



public class SharedPreference {

    public static String SharedPref = "SharedPref";
    public static String isLogin = "isLogin";
    public static String selectedLanguage = "selectedLanguage";

    public static void putBol(Context context, String key, boolean val) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, val);
        editor.commit();
    }

    public static boolean getBol(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPref, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public static String getVal(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPref, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static String getPinVal(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPref, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "0");
    }

    public static void putVal(Context context, String key, String val) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, val);
        editor.commit();
    }

    public static void clearData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
