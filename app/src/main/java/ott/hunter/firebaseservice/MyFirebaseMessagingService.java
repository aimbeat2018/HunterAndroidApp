package ott.hunter.firebaseservice;

import static ott.hunter.firebaseservice.Config.TOPIC_GLOBAL;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.pushnotification.NotificationInfo;
import com.clevertap.android.sdk.pushnotification.fcm.CTFcmMessageHandler;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import ott.hunter.DetailsActivity;
import ott.hunter.GoldDetailsActivity;
import ott.hunter.R;
import ott.hunter.firebaseservice.util.NotificationHelper;
import ott.hunter.firebaseservice.util.NotificationUtils;

/**
 * Firebase-Notification
 * https://github.com/quintuslabs/Firebase-Notification
 * Created on 28/04/19..
 * Created by : Santosh Kumar Dash:- http://santoshdash.epizy.com
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;
    String offer_id, issue, location;
    Intent intent;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        //(RK)add CTFcmMessageHandler() for clevertap receive notification
        // new  CTFcmMessageHandler().createNotification(getApplicationContext(), remoteMessage);
        //this if for clevertap notification
        if (remoteMessage.getData().size() > 0) {
            Bundle extras = new Bundle();
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                extras.putString(entry.getKey(), entry.getValue());
            }

            NotificationInfo info = CleverTapAPI.getNotificationInfo(extras);
            if (info.fromCleverTap) {
                new CTFcmMessageHandler()
                        .createNotification(getApplicationContext(), remoteMessage);
            }

        } else {
            //this is for firebase notification

            if (remoteMessage == null)
                return;

            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());

                handleNotification(remoteMessage.getNotification());
            }

            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

                try {
                    JSONObject json = new JSONObject(remoteMessage.getData().toString());
                    handleDataMessage(json);
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
            }

        }

    }


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GLOBAL);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }


    private void handleNotification(RemoteMessage.Notification message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
//            String title = remoteMessage.getNotification().getTitle();
//            String messageBody = remoteMessage.getNotification().getBody();
//            Uri imageUrl = remoteMessage.getNotification().getImageUrl();

            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message.getBody());
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

            showNotificationMessageWithBigImage(this, message.getTitle(), message.getBody(), "", pushNotification, String.valueOf(message.getImageUrl()));
        } else {
            // If the app is in background, firebase itself handles the notification
//            Uri alarmSound = Uri.parse("android.resource://" +
//                    getPackageName() + "/" + R.raw.notification_sound);
//            Ringtone r = RingtoneManager.getRingtone(this, alarmSound);
//            r.play();
//
//            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//            pushNotification.putExtra("message", message.getBody());
//            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//            // play notification sound
//            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//            notificationUtils.playNotificationSound();
//
//            showNotificationMessageWithBigImage(this, message.getTitle(), message.getBody(), "", pushNotification, String.valueOf(message.getImageUrl()));
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            String id = data.getString("id");
            String type = data.getString("type");
            String flag = data.getString("flag");
            String is_gold = data.getString("is_gold");
            String amt = data.getString("amt");

            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

            NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());

            if (flag.equals("Gold")) {
                intent = new Intent(getApplicationContext(), GoldDetailsActivity.class);
                intent.putExtra("vType", type);
                intent.putExtra("id", id);
                intent.putExtra("is_gold", is_gold);
                intent.putExtra("amt", amt);
            } else {
                intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.putExtra("vType", type);
                intent.putExtra("id", id);
            }

            if (imageUrl != null) {
                notificationHelper.createNotification(title, message, R.drawable.ppapplogo, imageUrl, timestamp, intent);
            } else {
                notificationHelper.createNotification(title, message, timestamp, intent);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
