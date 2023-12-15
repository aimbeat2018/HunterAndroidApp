package ott.hunter.firebaseservice;

/**
 * Firebase-Notification
 * https://github.com/quintuslabs/Firebase-Notification
 * Created on 28/04/19..
 * Created by : Santosh Kumar Dash:- http://santoshdash.epizy.com
 */

public class Config {

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";


    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";


    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    public static final String DATA = "data";
}
