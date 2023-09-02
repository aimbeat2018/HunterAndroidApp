package ott.hunter.constant;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import com.google.android.exoplayer2.offline.Download;

import java.text.DecimalFormat;

import ott.hunter.models.CommonModels;
import ott.hunter.utils.MyAppClass;

/**
 * Created by Uzma ansari (uzmaansari.aimbeat@gmail.com) on 30/09/22.
 */
public class AppUtil {

    public static CommonModels getVideoDetail(String videoUri){
        CommonModels videoModel = null;
        for (CommonModels videoModels : MyAppClass.getInstance().videoModels) {
            videoModels.setStremURL(videoUri);
            System.out.println("AppUtil ==> getVideoDetail ==> video_URL = "+videoModels.getStremURL());
            System.out.println("AppUtil ==> getVideoDetail ==> video_URL = "+videoUri);
            if(videoModels.getStremURL().equalsIgnoreCase(videoUri)){
                videoModel = videoModels;
                return videoModel;
            }
        }
        return videoModel;
    }

    public static String createExoDownloadNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = "1017";

            CharSequence channelName = "Adaptive Exo Download";
            String channelDescription = "Adaptive Exoplayer video Download";
            int channelImportance = NotificationManager.IMPORTANCE_NONE;
            // Initializes NotificationChannel.
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(false);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        } else {
            return null;
        }
    }

    public static String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size/1024.0;
        double m = ((size/1024.0)/1024.0);
        double g = (((size/1024.0)/1024.0)/1024.0);
        double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if ( t>1 ) {
            hrSize = dec.format(t).concat(" TB");
        } else if ( g>1 ) {
            hrSize = dec.format(g).concat(" GB");
        } else if ( m>1 ) {
            hrSize = dec.format(m).concat(" MB");
        } else if ( k>1 ) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }



    public static String floatToPercentage(float n){
        return String.format("%.0f",n)+"%";
    }

    public  static String downloadStatusFromId(Download download){

        String value ="";

        switch (download.state) {
            case Download.STATE_COMPLETED:
                value = "Download Completed";
                break;
            case Download.STATE_DOWNLOADING:
                value = "Downloading...";
                break;
            case Download.STATE_FAILED:
                value = "Failed";
                break;
            case Download.STATE_QUEUED:
                value = "Added in Queue";
                break;
            case Download.STATE_REMOVING:
                value = "Removing...";
                break;
            case Download.STATE_RESTARTING:
                value = "Restarting...";
                break;
            case Download.STATE_STOPPED:
                value = "Paused";
                break;
        }
        return value;
    }
}

