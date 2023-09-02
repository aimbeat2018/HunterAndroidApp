package ott.hunter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.offline.Download;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import ott.hunter.adapters.DownloadedVideoListAdapter;
import ott.hunter.constant.AppUtil;
import ott.hunter.database.DatabaseHelper;
import ott.hunter.models.CommonModels;
import ott.hunter.models.ItemMovie;
import ott.hunter.utils.MyAppClass;
import ott.hunter.utils.RtlUtils;
import ott.hunter.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadActivity extends AppCompatActivity {
    public static DownloadActivity instance;
    public static final String ACTION_PLAY_VIDEO = "play_video";
    public static final String TAG = "DownloadActivity";
    Toolbar toolbar;
    CoordinatorLayout coordinatorLayout;

    private boolean isDark;

    RecyclerView recyclerView;
    private List<Download> downloadedVideoList;
    public static ArrayList<Uri> keys = new ArrayList<>();
    private DownloadedVideoListAdapter downloadedVideoAdapter;
    private Runnable runnableCode;
    private Handler handler;
    private ItemMovie myItemMovie;
    private TextView download_text;
    private LinearLayout lyt_not_found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RtlUtils.setScreenDirection(this);
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("dark", false);

        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        setContentView(R.layout.activity_download);


        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Downloads");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.black_window_light));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }


        recyclerView = findViewById(R.id.recycler_view_downloaded_video);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);


        coordinatorLayout = findViewById(R.id.coordinator_lyt);

        loadVideos();

        handler = new Handler();
        runnableCode = new Runnable() {
            @Override
            public void run() {
                List<Download> exoVideoList = new ArrayList<>();
                for (Map.Entry<Uri, Download> entry : MyAppClass.getInstance().getDownloadTracker().downloads.entrySet()) {
                    Uri keyUri = entry.getKey();
                    Download download = entry.getValue();
                    keys.add(entry.getKey());
                    exoVideoList.add(download);
                }
                if (exoVideoList.size() != 0) {
                    downloadedVideoAdapter.onNewData(exoVideoList);
                    handler.postDelayed(this, 1000);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    lyt_not_found.setVisibility(View.VISIBLE);
                }
            }
        };
        handler.post(runnableCode);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(playVideoBroadcast);
    }

    private void loadVideos() {
        downloadedVideoList = new ArrayList<>();

        for (Map.Entry<Uri, Download> entry : MyAppClass.getInstance().getDownloadTracker().downloads.entrySet()) {
            Download download = entry.getValue();
            downloadedVideoList.add(download);
        }

        if (downloadedVideoList.size() != 0) {
            lyt_not_found.setVisibility(View.GONE);
//            downloadedVideoAdapter = new DownloadedVideoListAdapter(DownloadActivity.this, downloadedVideoList, DownloadActivity.this);
            recyclerView.setAdapter(downloadedVideoAdapter);
            downloadedVideoAdapter.addItems(downloadedVideoList);
        } else {
            lyt_not_found.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void openBottomSheet(Download download) {

        CommonModels videoModel = AppUtil.getVideoDetail(download.request.id);

        String statusTitle = videoModel.getMovieName();

        View dialogView = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);
        BottomSheetDialog dialog = new BottomSheetDialog(DownloadActivity.this);
        dialog.setContentView(dialogView);

        TextView tvVideoTitle = dialog.findViewById(R.id.tv_video_title);
        LinearLayout llDownloadStart = dialog.findViewById(R.id.ll_start_download);
        LinearLayout llDownloadResume = dialog.findViewById(R.id.ll_resume_download);
        LinearLayout llDownloadPause = dialog.findViewById(R.id.ll_pause_download);
        LinearLayout llDownloadDelete = dialog.findViewById(R.id.ll_delete_download);

        llDownloadStart.setVisibility(View.GONE);


        if (download.state == Download.STATE_DOWNLOADING) {
            llDownloadPause.setVisibility(View.VISIBLE);
            llDownloadResume.setVisibility(View.GONE);

        } else if (download.state == Download.STATE_STOPPED) {
            llDownloadPause.setVisibility(View.GONE);
            llDownloadResume.setVisibility(View.VISIBLE);

        } else if (download.state == Download.STATE_QUEUED) {
            llDownloadStart.setVisibility(View.VISIBLE);
            llDownloadPause.setVisibility(View.GONE);
            llDownloadResume.setVisibility(View.GONE);
        } else {
            llDownloadStart.setVisibility(View.GONE);
            llDownloadPause.setVisibility(View.GONE);
            llDownloadResume.setVisibility(View.GONE);
        }

        tvVideoTitle.setText(statusTitle);
        llDownloadStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAppClass.getInstance().getDownloadManager().addDownload(download.request);
                dialog.dismiss();
            }
        });
        llDownloadResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAppClass.getInstance().getDownloadManager().addDownload(download.request, Download.STOP_REASON_NONE);

                dialog.dismiss();
            }
        });

        llDownloadPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAppClass.getInstance().getDownloadManager().addDownload(download.request, Download.STATE_STOPPED);
                dialog.dismiss();
            }
        });

        llDownloadDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AppCompatActivity activity = (AppCompatActivity) v.getContext();
//                Fragment myFragment = new DownloadNewFragment();
//                activity.getSupportFragmentManager().beginTransaction().replace(R.id.Container, myFragment).addToBackStack(null).commit();
                MyAppClass.getInstance().getDownloadManager().removeDownload(download.request.id);

                ott.hunter.database.DatabaseHelper offlineDatabaseHelper1 = new DatabaseHelper(DownloadActivity.this);
                offlineDatabaseHelper1.deleteMovie(download.request.id);

                loadVideos();

//                if (downloadedVideoList.size() != 0) {
//                    lyt_not_found.setVisibility(View.GONE);
//                    downloadedVideoAdapter = new DownloadedVideoListAdapter(getActivity(), downloadedVideoList, DownloadNewFragment.this);
//                    recyclerView.setAdapter(downloadedVideoAdapter);
//                    downloadedVideoAdapter.addItems(downloadedVideoList);
//                } else {
//                    Fragment myFragment1 = new DownloadNewFragment();
//                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.Container, myFragment1).addToBackStack(null).commit();
//                    lyt_not_found.setVisibility(View.VISIBLE);
//                    recyclerView.setVisibility(View.GONE);
//                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
