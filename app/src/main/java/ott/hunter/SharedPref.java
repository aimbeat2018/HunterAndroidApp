package ott.hunter;


import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    public static String SharedPref = "SharedPref";
    public static String isLogin = "isLogin";
    public static String ratepopup = "";
    public static String user_id = "user_id";
    public static String mobile_no = "mobile_no";
    public static String email_id = "email_id";
    public static String user_fullname = "user_fullname";
    public static String password = "password";
    public static String selected_category_name = "selected_category_name";
    public static String selected_category_id = "selected_category_id";
    public static String selected_subcategory_id = "selected_subcategory_id";
    public static String business_update_status = "business_update_status";
    public static String seladdress = "seladdress";
    public static String seladdressID = "seladdressID";
    public static String totalDeliveryCharges = "totalDeliveryCharges";
    public static String orderNote = "orderNote";
    public static String userType = "userType";
    public static String flag = "flag";
    public static String seller_flag = "seller_flag";

    public static String order_listing_key = "order_listing_key";


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
