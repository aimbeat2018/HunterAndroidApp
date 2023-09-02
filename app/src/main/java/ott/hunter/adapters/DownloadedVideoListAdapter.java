package ott.hunter.adapters;

import static android.view.View.GONE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;

import java.util.List;

//import ott.hunter.OfflinePlayerActivity;
import ott.hunter.AppConfig;
import ott.hunter.R;
import ott.hunter.constant.AppUtil;
import ott.hunter.constant.MyDiffUtilCallback;
import ott.hunter.database.DatabaseHelper;
import ott.hunter.models.CommonModels;
import ott.hunter.nav_fragments.DownloadNewFragment;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.SubscriptionApi;
import ott.hunter.network.model.ActiveStatus;
import ott.hunter.widget.OfflinePlayerNewActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by Mayur Solanki (mayursolanki120@gmail.com) on 25/02/19, 5:48 PM.
 */
public class DownloadedVideoListAdapter extends RecyclerView.Adapter<DownloadedVideoListAdapter.MyViewHolder> { //implements Filterable
    List<Download> videosList;
    Context context;
    CommonModels itemMovie;
    DownloadNewFragment downloadActivity;
    Download download;
    DownloadManager downloadManager;
    public boolean isActive;




    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlContainer;
        ImageView imageView;
        TextView tvDownloadVideoTitle;
        TextView txtDuration;
        TextView tvDownloadVideoPercentage;
        TextView tvDownloadVideoStatus;
        ImageView imgMenuOverFlow;
        ProgressBar progressBarPercentage;

        public MyViewHolder(View view) {
            super(view);
            rlContainer = view.findViewById(R.id.rl_container);
            imageView = view.findViewById(R.id.img_download_banner);
            tvDownloadVideoTitle = view.findViewById(R.id.tv_download_vid_title);
            tvDownloadVideoPercentage = view.findViewById(R.id.tv_downloaded_percentage);
            tvDownloadVideoStatus = view.findViewById(R.id.tv_downloaded_status);
            imgMenuOverFlow = view.findViewById(R.id.img_overflow);
            progressBarPercentage = view.findViewById(R.id.progress_horizontal_percentage);
            txtDuration = view.findViewById(R.id.txtDuration);
        }
    }

    public DownloadedVideoListAdapter(Context context, List<Download> videosList, DownloadNewFragment downloadActivity) {
        this.context = context;
        this.videosList = videosList;
        this.downloadActivity = downloadActivity;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_downloaded_video, parent, false);
        return new MyViewHolder(itemView);
    }

    private void getActiveStatus(String userId) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);
        DownloadNewFragment.download_layout.setVisibility(GONE);

        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(AppConfig.API_KEY, userId);
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(Call<ActiveStatus> call, retrofit2.Response<ActiveStatus> response) {
                ActiveStatus activeStatus = response.body();
                if (activeStatus.getStatus().equals("active")) {
                    DownloadNewFragment.download_layout.setVisibility(GONE);
                    isActive = true;
                } else {
//                    contentDetails.setVisibility(VISIBLE);
//                    subscriptionLayout.setVisibility(GONE);
                    isActive = false;
                }
                // PreferenceUtils.updateSubscriptionStatus(OfflinePlayerNewActivity.this);
            }

            @Override
            public void onFailure(Call<ActiveStatus> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position, List<Object> payloads) {

//        getActiveStatus(PreferenceUtils.getUserId(context));
        Log.d("isActive","isActiveinupperview" + isActive);

        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Bundle o = (Bundle) payloads.get(0);
            for (String key : o.keySet()) {
                if (key.equals("percentDownloaded")) {

//                    if(isActive == true){
//                        holder.itemView.setVisibility(VISIBLE);
//                        DownloadNewFragment.download_layout.setVisibility(GONE);
                        download = (Download) videosList.get(position);

                        System.out.println("OnBindViewHolder ==> Movie URL ==> " + download.request.id);
                        System.out.println("OnBindViewHolder ==> Movie Length ==> " + download.contentLength);

                        if (download.state == Download.STATE_COMPLETED) {
                            holder.progressBarPercentage.setVisibility(View.GONE);
                        } else {
                            holder.progressBarPercentage.setVisibility(View.VISIBLE);
                            holder.progressBarPercentage.setProgress((int) download.getPercentDownloaded());
                        }
                        String percentage = AppUtil.floatToPercentage(download.getPercentDownloaded());
//                    String downloadInMb = AppUtil.getProgressDisplayLine(download.getBytesDownloaded(), downloadRequest.data.length);

                        if (download.state == Download.STATE_DOWNLOADING || download.state == Download.STATE_COMPLETED) {
                            holder.tvDownloadVideoPercentage.setVisibility(View.VISIBLE);
                            holder.tvDownloadVideoPercentage.setText("Size: " + AppUtil.formatFileSize(download.getBytesDownloaded()) + " | Progress: " + percentage);
                        } else {
                            holder.tvDownloadVideoPercentage.setVisibility(View.INVISIBLE);
                        }
                        holder.tvDownloadVideoStatus.setText(AppUtil.downloadStatusFromId(download));

//                    }else{
//                        holder.itemView.setVisibility(GONE);
//                        DownloadNewFragment.download_layout.setVisibility(VISIBLE);
//                    }
                }
            }
        }
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
//        getActiveStatus(PreferenceUtils.getUserId(context));
        DatabaseHelper offlineDatabaseHelper1 = new DatabaseHelper(context);

        Download download = videosList.get(position);
        Log.d("isActive","isActive" + isActive);

//        if(isActive){
//            holder.itemView.setVisibility(VISIBLE);
//            DownloadNewFragment.download_layout.setVisibility(GONE);

            for (int i = 0; i < videosList.size(); i++) {
                itemMovie = offlineDatabaseHelper1.getMovieByURL(download.request.id);
                holder.tvDownloadVideoTitle.setText(itemMovie.getMovieName());
                Glide.with(context).load(itemMovie.getImageUrl()).into(holder.imageView);
                holder.txtDuration.setText(itemMovie.getMovieDuration());

                if (download.state == Download.STATE_COMPLETED) {
                    holder.progressBarPercentage.setVisibility(View.GONE);
                } else {
                    holder.progressBarPercentage.setVisibility(View.VISIBLE);
                    holder.progressBarPercentage.setProgress((int) download.getPercentDownloaded());
                }
                String percentage = AppUtil.floatToPercentage(download.getPercentDownloaded());
                if (download.state == Download.STATE_DOWNLOADING || download.state == Download.STATE_COMPLETED) {
                    holder.tvDownloadVideoPercentage.setVisibility(View.VISIBLE);
                    holder.tvDownloadVideoPercentage.setText("Size: " + AppUtil.formatFileSize(download.getBytesDownloaded()) + " | Progress: " + percentage);
                } else {
                    holder.tvDownloadVideoPercentage.setVisibility(View.INVISIBLE);
                }


            }
//        }else{
//
//            holder.itemView.setVisibility(GONE);
//            DownloadNewFragment.download_layout.setVisibility(VISIBLE);
//        }


//
//        if(videosList.size() > 0){
//
//            try{
//
//                if (EpisodeAdapter.downloadManager.getCurrentDownloads().size() > 0) {
//
//
//                    for (int j = 0; j < EpisodeAdapter.downloadManager.getCurrentDownloads().size(); j++) {
//                        Download currentDownload = EpisodeAdapter.downloadManager.getCurrentDownloads().get(j);
//
//                        for(int g = 0 ; g < DownloadNewFragment.keys.size(); g++ ){
//
//                            if(!itemMovie.getStremURL().isEmpty() && currentDownload.request.uri.equals(parse(itemMovie.getStremURL()))){
//
//                                if (currentDownload.state == Download.STATE_DOWNLOADING) {
//
//                                    if (currentDownload.state == Download.STATE_COMPLETED) {
//                                        holder.progressBarPercentage.setVisibility(View.GONE);
//                                    } else {
//                                        holder.progressBarPercentage.setVisibility(View.VISIBLE);
//                                        holder.progressBarPercentage.setProgress((int) currentDownload.getPercentDownloaded());
//                                    }
//                                    String percentage = AppUtil.floatToPercentage(download.getPercentDownloaded());
//                                    if (currentDownload.state == Download.STATE_DOWNLOADING || currentDownload.state == Download.STATE_COMPLETED) {
//                                        holder.tvDownloadVideoPercentage.setVisibility(View.VISIBLE);
//                                        holder.tvDownloadVideoPercentage.setText("Size: " + AppUtil.formatFileSize(currentDownload.getBytesDownloaded()) + " | Progress: " + percentage);
//                                    } else {
//                                        holder.tvDownloadVideoPercentage.setVisibility(View.INVISIBLE);
//                                    }
//
//
//                                }
//                            }
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//        }


        holder.tvDownloadVideoStatus.setText(AppUtil.downloadStatusFromId(download));
        holder.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (download.state == Download.STATE_COMPLETED) {
                    Bundle bundle = new Bundle();
                    bundle.putString("video_url", download.request.id);
                    Intent intent = new Intent(context, OfflinePlayerNewActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } else {
                    downloadActivity.openBottomSheet(download);
                }
            }
        });

        holder.imgMenuOverFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadActivity.openBottomSheet(download);
            }
        });

    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public Download getItem(int position) {
        return videosList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItems(List<Download> lst) {
        this.videosList = lst;
    }

    public void onNewData(List<Download> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffUtilCallback(newData, videosList));
        diffResult.dispatchUpdatesTo(this);
        this.videosList.clear();
        this.videosList.addAll(newData);
        notifyDataSetChanged();
    }
}