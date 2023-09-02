/*
package ott.hunter.constant;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.PlatformScheduler;
import com.google.android.exoplayer2.util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ott.hunter.R;
import ott.hunter.models.CommonModels;
import ott.hunter.utils.Constants;
import ott.hunter.utils.FileUtil;
import ott.hunter.utils.MyAppClass;


*/
/**
 * A service for downloading media.
 *//*

public class DemoDownloadService extends DownloadService implements DownloadTracker.Listener {

    private static final int JOB_ID = 1;
    //  private static final int FOREGROUND_NOTIFICATION_ID = 1;
    Handler handler = new Handler();
    private Runnable runnableCode;
    DownloadManager downloadManager;
    DownloadTracker downloadTracker;


    public DemoDownloadService() {
        super(FOREGROUND_NOTIFICATION_ID_NONE);
    }


    @Override
    protected DownloadManager getDownloadManager() {
        // This will only happen once, because getDownloadManager is guaranteed to be called only once
        // in the life cycle of the process.
        MyAppClass application = (MyAppClass) getApplication();
        DownloadManager downloadManager = application.getDownloadManager();
        downloadManager.setMaxParallelDownloads(1);
        return downloadManager;
    }

    @Override
    protected PlatformScheduler getScheduler() {
        return Util.SDK_INT >= 21 ? new PlatformScheduler(this, JOB_ID) : null;
    }

    @Override
    protected Notification getForegroundNotification(List<Download> downloads) {
        return null;
    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        MyAppClass application = (MyAppClass) getApplication();
        downloadManager = application.getDownloadManager();
        downloadTracker = application.getDownloadTracker();

        downloadTracker.addListener(this);


        if (intent != null) {
            Uri uri = intent.getData();
            switch (Objects.requireNonNull(intent.getAction())) {
                case Constants.EXO_DOWNLOAD_ACTION_PAUSE:
                    ((MyAppClass) getApplication()).getDownloadManager().addDownload(downloadTracker.getDownloadRequest(uri), Download.STATE_STOPPED);

                    break;
                case Constants.EXO_DOWNLOAD_ACTION_START:
                    ((MyAppClass) getApplication()).getDownloadManager().addDownload(downloadTracker.getDownloadRequest(uri), Download.STOP_REASON_NONE);

                    break;

                case Constants.EXO_DOWNLOAD_ACTION_CANCEL:
                    ((MyAppClass) getApplication()).getDownloadManager().removeDownload(downloadTracker.getDownloadRequest(uri).id);

                    break;
            }
        }

        runnableCode = new Runnable() {
            @Override
            public void run() {
                checkAndStartDownload(application.getApplicationContext());
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(runnableCode);


        return START_STICKY;
    }

    public void checkAndStartDownload(Context context) {
        if (downloadManager.getCurrentDownloads().size() > 0) {
            for (Download download : downloadManager.getCurrentDownloads()) {
                if (download.state == Download.STATE_DOWNLOADING) {
                    showNotification(context, download);
                } else {
                    stopForeground(true);
                }
            }
        } else {
            stopForeground(true);
            handler.removeCallbacks(runnableCode);
        }
    }

    @Override
    public void onDestroy() {
        if (downloadTracker != null) {
            downloadTracker.removeListener(this);
        }
        super.onDestroy();


    }

    @Override
    public void onDownloadsChanged(Download download) {
        switch (download.state) {
            case Download.STATE_COMPLETED:
                Log.d("onDownloadChanged", " Notification STATE_COMPLETED");
                break;
            case Download.STATE_FAILED:
                Log.d("onDownloadChanged", " Notification STATE_FAILED");
                break;
            case Download.STATE_QUEUED:
                Log.d("onDownloadChanged", " Notification STATE_QUEUED");


                break;
            case Download.STATE_STOPPED:
                Log.d("onDownloadChanged", " Notification STATE_STOPPED");
                break;

            case Download.STATE_DOWNLOADING:
                Log.d("onDownloadChanged", " Notification STATE_DOWNLOADING");

                break;
            case Download.STATE_REMOVING:
                Log.d("onDownloadChanged", " Notification STATE_REMOVING");

                break;
            case Download.STATE_RESTARTING:
                Log.d("onDownloadChanged", " Notification STATE_RESTARTING");
                break;
        }

    }


    public void showNotification(Context context, Download download) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = FileUtil.createExoDownloadNotificationChannel(context);

        System.out.println("ShowNotification ==> download.request.uri = " + download.request.uri);
        System.out.println("ShowNotification ==> download.request.id = " + download.request.id);
        System.out.println("ShowNotification ==> download.request.customCacheKey = " + download.request.customCacheKey);
        System.out.println("ShowNotification ==> download.request.streamKeys = " + download.request.streamKeys);
        System.out.println("ShowNotification ==> download.request.data = " + Arrays.toString(download.request.data));
        // Start/Resume Download
        Intent pIntentStart = new Intent(context, DemoDownloadService.class);
        pIntentStart.setAction(Constants.EXO_DOWNLOAD_ACTION_START);
        pIntentStart.setData(download.request.uri);
        // Pause Download
        Intent pIntentPause = new Intent(context, DemoDownloadService.class);
        pIntentPause.setAction(Constants.EXO_DOWNLOAD_ACTION_PAUSE);
        pIntentPause.setData(download.request.uri);
        PendingIntent pendingIntentPause = PendingIntent.getService(this, 100, pIntentPause, 0);

        // Cancel Download
        Intent pIntentStartCancel = new Intent(context, DemoDownloadService.class);
        pIntentStartCancel.setData(download.request.uri);
        pIntentStartCancel.setAction(Constants.EXO_DOWNLOAD_ACTION_CANCEL);
        PendingIntent pendingIntentCancel = PendingIntent.getService(this, 100, pIntentStartCancel, 0);

        CommonModels videoModel = FileUtil.getVideoDetail(download.request.id);

        switch (download.state) {

            case Download.STATE_DOWNLOADING:


                Notification notificationDownloading = new NotificationCompat.Builder(context, channelId)
                        .setOngoing(true)
                        .setAutoCancel(false)
                        .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                        .setContentTitle(videoModel.getMovieName())
                        .setContentText("Downloading")
                        .setProgress(100, (int) download.getPercentDownloaded(), false)
                        .setOnlyAlertOnce(true)
                        .setSmallIcon(R.drawable.logo2)
                        .addAction(new NotificationCompat.Action(R.drawable.ic_play_arrow_black_24dp, "Pause", pendingIntentPause))
                        .addAction(new NotificationCompat.Action(R.drawable.ic_play_arrow_black_24dp, "Cancel", pendingIntentCancel))
                        .build();


                startForeground(1001, notificationDownloading);

                break;
            case Download.STATE_COMPLETED:

                Notification notificationCompleted = new NotificationCompat.Builder(context, channelId)
                        .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                        .setContentTitle(videoModel.getMovieName())
                        .setContentText("Completed")
                        .setAutoCancel(false)
                        .setWhen(System.currentTimeMillis())
                        .setOnlyAlertOnce(true)
                        .setSmallIcon(R.drawable.logo2)
                        .build();


                notificationManager.notify(1002, notificationCompleted);

                break;
            case Download.STATE_FAILED:
                break;
            case Download.STATE_QUEUED:
                break;
            case Download.STATE_REMOVING:
                break;
            case Download.STATE_RESTARTING:
                break;
            case Download.STATE_STOPPED:
                break;
        }
    }

}

*/
