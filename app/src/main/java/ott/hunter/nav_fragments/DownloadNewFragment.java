package ott.hunter.nav_fragments;

import static android.view.View.GONE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.Login;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ott.hunter.AppConfig;
import ott.hunter.LoginActivity;
import ott.hunter.MainActivity;
import ott.hunter.NoInternet;
import ott.hunter.R;
import ott.hunter.SubscriptionActivity;
import ott.hunter.adapters.DownloadedVideoListAdapter;
import ott.hunter.constant.AppUtil;
import ott.hunter.database.DatabaseHelper;
import ott.hunter.models.CommonModels;
import ott.hunter.models.ItemMovie;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.SubscriptionApi;
import ott.hunter.network.model.ActiveStatus;
import ott.hunter.utils.MyAppClass;
import ott.hunter.utils.PreferenceUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DownloadNewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class DownloadNewFragment extends Fragment {

    RecyclerView recyclerView;
    private List<Download> downloadedVideoList;
    public static ArrayList<Uri> keys = new ArrayList<>();
    private DownloadedVideoListAdapter downloadedVideoAdapter;
    private Runnable runnableCode;
    private Handler handler;
    private ItemMovie myItemMovie;
    private TextView download_text;
    public LinearLayout lyt_not_found;
    public static LinearLayout download_layout;
    public boolean isActive;
    RelativeLayout rel_rec;
    Button subscribe_btn;
    private NoInternet noactivity;

    private MainActivity mainActivity;
    String from = "";

    public void setMyItemMovie(ItemMovie myItemMovie) {
        this.myItemMovie = myItemMovie;
    }

    public static DownloadNewFragment newInstance(String url, boolean isTrailer) {
        DownloadNewFragment f = new DownloadNewFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_download_new, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view_downloaded_video);
        subscribe_btn = rootView.findViewById(R.id.subscribe_button);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        rel_rec = rootView.findViewById(R.id.rel_rec);
        download_text = rootView.findViewById(R.id.download_text);
        download_text.setText("Downloads");
       // mainActivity = (MainActivity) getActivity();


        try {
            from = getArguments().getString("from");

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (from.equals("nointernetactivity")) {
            noactivity = (NoInternet) getActivity();

        } else {
            mainActivity = (MainActivity) getActivity();

        }


        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        download_layout = rootView.findViewById(R.id.download_layouts);
        download_layout.setVisibility(View.GONE);
        getActiveStatus(PreferenceUtils.getUserId(getActivity()));
        loadVideos();


        subscribe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PreferenceUtils.isLoggedIn(mainActivity)) {
                    startActivity(new Intent(mainActivity, SubscriptionActivity.class));
                } else {
                    startActivity(new Intent(mainActivity, LoginActivity.class));
                }
            }
        });
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
        return rootView;
    }

    private void getActiveStatus(String userId) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);


        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(AppConfig.API_KEY, userId);
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(Call<ActiveStatus> call, retrofit2.Response<ActiveStatus> response) {
                ActiveStatus activeStatus = response.body();
                if (activeStatus.getStatus().equals("active")) {

                    isActive = true;
                    rel_rec.setVisibility(View.VISIBLE);
                    download_layout.setVisibility(GONE);

                } else {
//                    contentDetails.setVisibility(VISIBLE);
//                    subscriptionLayout.setVisibility(GONE);
                    isActive = false;
                    rel_rec.setVisibility(GONE);
                    download_layout.setVisibility(View.VISIBLE);


                }
                // PreferenceUtils.updateSubscriptionStatus(OfflinePlayerNewActivity.this);
            }

            @Override
            public void onFailure(Call<ActiveStatus> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }


    private void loadVideos() {

        download_layout.setVisibility(View.GONE);

        downloadedVideoList = new ArrayList<>();

        for (Map.Entry<Uri, Download> entry : MyAppClass.getInstance().getDownloadTracker().downloads.entrySet()) {
            Download download = entry.getValue();
            downloadedVideoList.add(download);
        }

        if (downloadedVideoList.size() != 0) {
            lyt_not_found.setVisibility(View.GONE);
            downloadedVideoAdapter = new DownloadedVideoListAdapter(getActivity(), downloadedVideoList, DownloadNewFragment.this);
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
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
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

                DatabaseHelper offlineDatabaseHelper1 = new DatabaseHelper(getContext());
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