package ott.hunter.widget;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.Formatter;
import java.util.Locale;

import ott.hunter.R;
import ott.hunter.utils.MyAppClass;

public class OfflinePlayerNewActivity extends AppCompatActivity implements View.OnClickListener, VideoRendererEventListener, PlaybackPreparer, PlayerControlView.VisibilityListener {


    private static final String TAG = "OfflinePlayer";
    protected static CookieManager DEFAULT_COOKIE_MANAGER;
    // Exoplayer
    private ImageView imgBackPlayer;
    private ImageView imgBwd;
    private ImageView exoPlay;
    private ImageView exoPause;
    private ImageView imgFwd;
    private LinearLayout linMediaController;
    private TextView tvPlayerCurrentTime;
    private ProgressBar exoProgressbar;
    private TextView tvPlayerEndTime;
    private TextView tvPlaybackSpeed;
    private TextView tvPlayBackSpeedSymbol;
    private ImageView imgFullScreenEnterExit;
    private PlayerView playerView;
    private SimpleExoPlayer simpleExoPlayer;
    private FrameLayout frameLayoutMain;
    int tapCount = 1;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private Handler handler;
    private DataSource.Factory dataSourceFactory;
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 2000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            hide();
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }

        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    String offlineVideoLink, title;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_offline_player_new);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title = bundle.getString("video_title");
            offlineVideoLink = bundle.getString("video_url");

        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        dataSourceFactory = buildDataSourceFactory();
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        FullScreencall();
        initView();
        prepareView();
        setOnClickListner();


    }




    private void initView() {
        playerView = findViewById(R.id.player_view);
        frameLayoutMain = findViewById(R.id.frame_layout_main);
        imgBwd = findViewById(R.id.img_bwd);
        exoPlay = findViewById(R.id.exo_play);
        exoPause = findViewById(R.id.exo_pause);
        imgBackPlayer = findViewById(R.id.img_back_player);
        imgBackPlayer.setOnClickListener(this);
        imgFwd = findViewById(R.id.img_fwd);
        linMediaController = findViewById(R.id.lin_media_controller);
        tvPlayerCurrentTime = findViewById(R.id.tv_player_current_time);
        exoProgressbar = findViewById(R.id.loading_exoplayer);
        tvPlayerEndTime = findViewById(R.id.tv_player_end_time);
        tvPlaybackSpeed = findViewById(R.id.tv_play_back_speed);
        tvPlaybackSpeed.setOnClickListener(this);
        tvPlaybackSpeed.setText("" + tapCount);
        tvPlayBackSpeedSymbol = findViewById(R.id.tv_play_back_speed_symbol);
        imgFullScreenEnterExit = findViewById(R.id.img_full_screen_enter_exit);

        tvPlayBackSpeedSymbol.setOnClickListener(this);
    }

    public void prepareView() {

        setProgress();
    }


    private void initExoplayer() {



//
        DefaultAllocator defaultAllocator = new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE);
//        DefaultLoadControl defaultLoadControl = new DefaultLoadControl(defaultAllocator,
//                DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
//                DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
//                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
//                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS,
//                DefaultLoadControl.DEFAULT_TARGET_BUFFER_BYTES,
//                DefaultLoadControl.DEFAULT_PRIORITIZE_TIME_OVER_SIZE_THRESHOLDS
//        );
        LoadControl loadControl = new DefaultLoadControl();

//        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this,
//                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);


        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//        ExoTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
//        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        AdaptiveTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        trackSelector = new DefaultTrackSelector(OfflinePlayerNewActivity.this);
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this);

        renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);

        simpleExoPlayer = new SimpleExoPlayer.Builder(OfflinePlayerNewActivity.this,renderersFactory)
//                .setMediaSourceFactory(new DefaultMediaSourceFactory(cacheDataSourceFactory))

                .setTrackSelector(trackSelector)
                .setBandwidthMeter(bandwidthMeter)
                .setLoadControl(loadControl)
                .build();
//        DefaultAllocator defaultAllocator = new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE);
//        DefaultLoadControl defaultLoadControl = new DefaultLoadControl(defaultAllocator,
//                DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
//                DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
//                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
//                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS,
//                DefaultLoadControl.DEFAULT_TARGET_BUFFER_BYTES,
//                DefaultLoadControl.DEFAULT_PRIORITIZE_TIME_OVER_SIZE_THRESHOLDS
//        );
//
//        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this, null,
//                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
//
//        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, renderersFactory, trackSelector, defaultLoadControl);
        playerView.setUseController(true);
        playerView.setPlayer(simpleExoPlayer);
        simpleExoPlayer.setPlayWhenReady(true); //run file/link when ready to play.

        DownloadRequest downloadRequest = MyAppClass.getInstance().getDownloadTracker().getDownloadRequest(Uri.parse(offlineVideoLink));
        MediaSource mediaSource = DownloadHelper.createMediaSource(downloadRequest, dataSourceFactory);
//
//        ProgressiveMediaSource mediaSource =
//                new ProgressiveMediaSource.Factory(cacheDataSourceFactory)
//                        .createMediaSource(MediaItem.fromUri(Uri.parse(offlineVideoLink)));
        simpleExoPlayer.prepare(mediaSource, false, true);
        simpleExoPlayer.addListener(new ExoPlayer.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.v(TAG, "Listener-onTracksChanged...");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.v(TAG, "Listener-onLoadingChanged...isLoading:" + isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.v(TAG, "Listener-onPlayerStateChanged..." + playbackState);


                switch (playbackState) {
                    case ExoPlayer.STATE_IDLE:
                        Log.d(TAG, "playbackState : " + "STATE_IDLE");
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        Log.d(TAG, "playbackState : " + "STATE_BUFFERING");
                        exoProgressbar.setVisibility(View.VISIBLE);
                        break;
                    case ExoPlayer.STATE_READY:
                        Log.d(TAG, "playbackState : " + "STATE_READY");
                        exoProgressbar.setVisibility(View.GONE);
                        break;
                    case ExoPlayer.STATE_ENDED:
                        Log.d(TAG, "playbackState : " + "STATE_ENDED");
                        break;
                    default:

                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Log.v(TAG, "Listener-onRepeatModeChanged...");
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.v(TAG, "Listener-onPlayerError...");
                simpleExoPlayer.stop();
                simpleExoPlayer.prepare(mediaSource);
                simpleExoPlayer.setPlayWhenReady(true);
            }


            @Override
            public void onPositionDiscontinuity(int reason) {

            }


            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.v(TAG, "Listener-onPlaybackParametersChanged...");
            }

            /**
             * Called when all pending seek requests have been processed by the simpleExoPlayer. This is guaranteed
             * to happen after any necessary changes to the simpleExoPlayer state were reported to
             * {@link #onPlayerStateChanged(boolean, int)}.
             */
            @Override
            public void onSeekProcessed() {

            }
        });

        initBwd();
        initFwd();

    }

    private void setProgress() {

        handler = new Handler();
        //Make sure you update Seekbar on UI thread
        handler.post(new Runnable() {

            @Override
            public void run() {
                if (simpleExoPlayer != null) {
                    tvPlayerCurrentTime.setText(stringForTime((int) simpleExoPlayer.getCurrentPosition()));
                    tvPlayerEndTime.setText(stringForTime((int) simpleExoPlayer.getDuration()));

                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void initBwd() {
        imgBwd.requestFocus();
        imgBwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() - 10000);
            }
        });
    }

    private void initFwd() {
        imgFwd.requestFocus();
        imgFwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() + 10000);
            }
        });

    }

    private void setOnClickListner() {
        imgFullScreenEnterExit.setOnClickListener(this);
        tvPlaybackSpeed.setOnClickListener(this);
        tvPlaybackSpeed.setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.img_full_screen_enter_exit) {
            Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
            int orientation = display.getOrientation();

            if (orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) {

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                frameLayoutMain.setLayoutParams(new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 600));
                imgFullScreenEnterExit.setImageResource(R.drawable.exo_controls_fullscreen_enter);

            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                frameLayoutMain.setLayoutParams(new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

                imgFullScreenEnterExit.setImageResource(R.drawable.exo_controls_fullscreen_exit);

            }
            hide();
        } else if (view.getId() == R.id.img_back_player) {
            onBackPressed();
        }

    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        releasePlayer();
        setIntent(intent);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title = bundle.getString("title");
            offlineVideoLink = bundle.getString("link");
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initExoplayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || playerView == null) {
            initExoplayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }

        FullScreencall();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }


    public void finishActivity() {
        OfflinePlayerNewActivity.this.finish();
    }


    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;

        }

    }

    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

//    @Override
//    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
//
//    }
//
//    @Override
//    public void onRenderedFirstFrame(Surface surface) {
//
//    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }

    public void FullScreencall() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private String stringForTime(int timeMs) {
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


    @Override
    public void preparePlayback() {
        initExoplayer();

    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

    private DataSource.Factory buildDataSourceFactory() {
        return ((MyAppClass) getApplication()).buildDataSourceFactory();
    }
}