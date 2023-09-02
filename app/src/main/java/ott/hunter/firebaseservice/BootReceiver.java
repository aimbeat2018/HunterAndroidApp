package ott.hunter.firebaseservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Firebase-Notification
 * https://github.com/quintuslabs/Firebase-Notification
 * Created on 28/04/19..
 * Created by : Santosh Kumar Dash:- http://santoshdash.epizy.com
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

//            Intent startServiceIntent = new Intent(context, MyFirebaseInstanceIDService.class);
//            context.startService(startServiceIntent);

            Intent notificationServiceIntent = new Intent(context, MyFirebaseMessagingService.class);
            context.startService(notificationServiceIntent);
        }
    }
}