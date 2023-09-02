package ott.hunter.adapters;

import static android.content.Context.MODE_PRIVATE;
import static android.net.Uri.parse;

import static com.google.android.exoplayer2.offline.Download.STATE_COMPLETED;
import static com.google.android.exoplayer2.offline.Download.STATE_DOWNLOADING;
import static com.google.android.exoplayer2.offline.Download.STATE_FAILED;
import static com.google.android.exoplayer2.offline.Download.STATE_QUEUED;
import static com.google.android.exoplayer2.offline.Download.STATE_REMOVING;
import static com.google.android.exoplayer2.offline.Download.STATE_RESTARTING;
import static com.google.android.exoplayer2.offline.Download.STATE_STOPPED;

import static net.one97.paytm.nativesdk.BasePaytmSDK.getApplicationContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.ExoTrackSelection;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ott.hunter.AppConfig;
import ott.hunter.DetailsActivity;
import ott.hunter.LoginActivity;
import ott.hunter.R;
import ott.hunter.database.DatabaseHelper;
import ott.hunter.models.CommonModels;
import ott.hunter.models.EpiModel;
import ott.hunter.models.single_details.Season;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.LoginApi;
import ott.hunter.network.apis.UserDataApi;
import ott.hunter.network.model.User;
import ott.hunter.offlinedownload.DemoDownloadService;
import ott.hunter.offlinedownload.DownloadTracker;
import ott.hunter.offlinedownload.ExoDownloadState;
import ott.hunter.offlinedownload.TrackKey;
import ott.hunter.utils.Constants;
import ott.hunter.utils.MyAppClass;
import ott.hunter.utils.PreferenceUtils;
import ott.hunter.utils.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.OriginalViewHolder> implements DownloadTracker.Listener {

    private List<EpiModel> items = new ArrayList<>();
    private List<Season> itemsseason = new ArrayList<>();
    private Activity ctx;
    final EpisodeAdapter.OriginalViewHolder[] viewHolderArray = {null};
    private OnTVSeriesEpisodeItemClickListener mOnTVSeriesEpisodeItemClickListener;
    EpisodeAdapter.OriginalViewHolder viewHolder;
    int i = 0;
    private int seasonNo;
    String deviceNoDynamic = "";
    String deviceNo = "";
    String castcrew = "";
    public static String url;
    DatabaseHelper offlineDatabaseHelper;
    public static DownloadManager downloadManager;
    private DownloadTracker downloadTracker;
    private DownloadHelper myDownloadHelper;
    private Runnable runnableCode;
    private Handler handler;
    MyAppClass myApplication;
    OriginalViewHolder mholder;
    private DataSource.Factory dataSourceFactory;
    List<TrackKey> trackKeys = new ArrayList<>();
    EpiModel obj;
    private DefaultTrackSelector trackSelector;

    DefaultTrackSelector.Parameters qualityParams;
    ProgressDialog pDialog;
    List<String> optionsToDownload = new ArrayList<String>();

    CleverTapAPI clevertapdownloaddevice;

    @Override
    public void onDownloadsChanged(Download download) {
        switch (download.state) {
            case STATE_QUEUED:

                break;

            case STATE_STOPPED:


                break;
            case STATE_DOWNLOADING:

//                setCommonDownloadButton(ExoDownloadState.DOWNLOAD_PAUSE, mholder);
//
//                if (download.getPercentDownloaded() != -1) {
//                    mholder.downloadProgress.setVisibility(View.VISIBLE);
//                    mholder.img_download_state.setVisibility(View.GONE);
//                    mholder.downloadProgress.setProgress((int) download.getPercentDownloaded());
//                }
//                Log.d("EXO DOWNLOADING ", +download.getBytesDownloaded() + " " + download.contentLength);
//                Log.d("EXO  DOWNLOADING ", "" + download.getPercentDownloaded());


                break;
            case STATE_COMPLETED:

//                setCommonDownloadButton(ExoDownloadState.DOWNLOAD_COMPLETED, mholder);

                Log.d("EXO COMPLETED ", +download.getBytesDownloaded() + " " + download.contentLength);
                Log.d("EXO  COMPLETED ", "" + download.getPercentDownloaded());


//                if (download.request.uri.toString().equals(singleItem.getEpisodeUrl())) {
//
//                    if (download.getPercentDownloaded() != -1) {
//                    }
//                }

                break;

            case STATE_FAILED:


                break;

            case STATE_REMOVING:


                break;

            case STATE_RESTARTING:

                break;

        }
    }

    public void setCommonDownloadButton(ExoDownloadState exoDownloadState, OriginalViewHolder holder,EpiModel model) {
        switch (exoDownloadState) {
            case DOWNLOAD_START:
                holder.ll_download_video.setTag(exoDownloadState);
                holder.tv_download_state.setText(exoDownloadState.getValue());
                holder.img_download_state.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_download));
                holder.downloadProgress.setVisibility(View.GONE);
                holder.img_download_state.setVisibility(View.VISIBLE);

                if (offlineDatabaseHelper.checkIfMyMovieExists(model.getStreamURL())) {
                    offlineDatabaseHelper.updateStatus("DOWNLOAD_START",model.getStreamURL());
                }
                break;

            case DOWNLOAD_PAUSE:
                holder.ll_download_video.setTag(exoDownloadState);
                holder.tv_download_state.setText(exoDownloadState.getValue());
                holder.img_download_state.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_pause));
                holder.downloadProgress.setVisibility(View.GONE);
                holder.img_download_state.setVisibility(View.VISIBLE);

//                if (offlineDatabaseHelper.checkIfMyMovieExists(model.getStreamURL())) {
//                    offlineDatabaseHelper.updateStatus("DOWNLOAD_PAUSE",model.getStreamURL());
//                }

                break;

            case DOWNLOAD_RESUME:
                holder.ll_download_video.setTag(exoDownloadState);
                holder.tv_download_state.setText(exoDownloadState.getValue());
                holder.img_download_state.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_play));
                holder.downloadProgress.setVisibility(View.GONE);
                holder.img_download_state.setVisibility(View.VISIBLE);

                if (offlineDatabaseHelper.checkIfMyMovieExists(model.getStreamURL())) {
                    offlineDatabaseHelper.updateStatus("DOWNLOAD_RESUME",model.getStreamURL());
                }

                break;

            case DOWNLOAD_RETRY:
                holder.ll_download_video.setTag(exoDownloadState);
                holder.tv_download_state.setText(exoDownloadState.getValue());
                holder.img_download_state.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_retry));
                holder.downloadProgress.setVisibility(View.GONE);
                holder.img_download_state.setVisibility(View.VISIBLE);

                if (offlineDatabaseHelper.checkIfMyMovieExists(model.getStreamURL())) {
                    offlineDatabaseHelper.updateStatus("DOWNLOAD_RETRY",model.getStreamURL());
                }

                break;

            case DOWNLOAD_COMPLETED:
                holder.ll_download_video.setTag(exoDownloadState);
                holder.tv_download_state.setText(exoDownloadState.getValue());
                holder.img_download_state.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_plus));
                holder.downloadProgress.setVisibility(View.GONE);
                holder.img_download_state.setVisibility(View.VISIBLE);

                if (offlineDatabaseHelper.checkIfMyMovieExists(model.getStreamURL())) {
                    offlineDatabaseHelper.updateStatus("DOWNLOAD_COMPLETED",model.getStreamURL());
                }

                break;

            case DOWNLOAD_QUEUE:
                holder.ll_download_video.setTag(exoDownloadState);
                holder.tv_download_state.setText(exoDownloadState.getValue());
                holder.img_download_state.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_queue));
                holder.downloadProgress.setVisibility(View.GONE);
                holder.img_download_state.setVisibility(View.VISIBLE);

                if (offlineDatabaseHelper.checkIfMyMovieExists(model.getStreamURL())) {
                    offlineDatabaseHelper.updateStatus("DOWNLOAD_QUEUE",model.getStreamURL());
                }


                break;
        }

    }

    public interface OnTVSeriesEpisodeItemClickListener {
        void onEpisodeItemClickTvSeries(String type, View view, EpiModel obj, int position, OriginalViewHolder holder);
    }

    private void observerVideoStatus(EpiModel singleItem, OriginalViewHolder holder) {
//        itemMovie=new ItemMovie();
        if (downloadManager.getCurrentDownloads().size() > 0) {
            for (int i = 0; i < downloadManager.getCurrentDownloads().size(); i++) {
                Download currentDownload = downloadManager.getCurrentDownloads().get(i);
                if (!singleItem.getStreamURL().isEmpty() && currentDownload.request.uri.equals(parse(singleItem.getStreamURL()))) {
                    url = singleItem.getStreamURL();
                    Log.d("getStreamURL","getStreamURL = " + url );
                    ((Activity) ctx).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            if (currentDownload.state == Download.STATE_DOWNLOADING) {
//                                setCommonDownloadButton(ExoDownloadState.DOWNLOAD_PAUSE, holder,singleItem);
//                                currentDownload.getPercentDownloaded();
//
//                                if (currentDownload.getPercentDownloaded() != -1) {
//                                    if(currentDownload.getPercentDownloaded() >= 80){
//                                        setCommonDownloadButton(ExoDownloadState.DOWNLOAD_COMPLETED, holder,singleItem);
//                                    }else {
//                                        holder.downloadProgress.setVisibility(View.VISIBLE);
//                                        holder.img_download_state.setVisibility(View.GONE);
//                                        holder.downloadProgress.setProgress((int) currentDownload.getPercentDownloaded());
//                                    }
//                                }
//
//
//                                Log.d("EXOSTATE_DOWNLOADING","downloadPercentage = " +  currentDownload.getPercentDownloaded());
//                            }else if(currentDownload.state == Download.STATE_QUEUED){
//                                setCommonDownloadButton(ExoDownloadState.DOWNLOAD_QUEUE, holder,singleItem);
//
//                            }else if(currentDownload.state == Download.STATE_STOPPED){
//                                setCommonDownloadButton(ExoDownloadState.DOWNLOAD_RESUME, holder,singleItem);
//
//                            }else if(currentDownload.state == STATE_COMPLETED){
//                                setCommonDownloadButton(ExoDownloadState.DOWNLOAD_COMPLETED, holder,singleItem);
//                                Toast.makeText(ctx, "State Complete", Toast.LENGTH_SHORT).show();
//
//                            }
                            if (downloadTracker.downloads.size() > 0) {
                                if (currentDownload.request.uri.equals(parse(singleItem.getStreamURL()))) {

                                    Download downloadFromTracker = downloadTracker.downloads.get(parse(singleItem.getStreamURL()));
                                    if (downloadFromTracker != null) {
                                        switch (downloadFromTracker.state) {
                                            case STATE_QUEUED:
                                                setCommonDownloadButton(ExoDownloadState.DOWNLOAD_QUEUE, holder,singleItem);
                                                break;

                                            case STATE_STOPPED:
                                                setCommonDownloadButton(ExoDownloadState.DOWNLOAD_RESUME, holder,singleItem);
                                                Log.d("EXOSTATE_DOWNLOADING ", +downloadFromTracker.getBytesDownloaded() + " " + downloadFromTracker.contentLength);
                                                Log.d("EXOSTATE_DOWNLOADING ", "" + downloadFromTracker.getPercentDownloaded());
                                                break;

                                            case STATE_DOWNLOADING:
//                                                setCommonDownloadButton(ExoDownloadState.DOWNLOAD_PAUSE, holder);

                                                if (downloadFromTracker.getPercentDownloaded() != -1) {
                                                    holder.downloadProgress.setVisibility(View.VISIBLE);
                                                    holder.img_download_state.setVisibility(View.GONE);
                                                    holder.downloadProgress.setProgress((int) downloadFromTracker.getPercentDownloaded());
                                                }

                                                Log.d("EXO STATE_DOWNLOADING ", +downloadFromTracker.getBytesDownloaded() + " " + downloadFromTracker.contentLength);
                                                Log.d("EXO  STATE_DOWNLOADING ", "" + downloadFromTracker.getPercentDownloaded());
                                                break;
                                            case STATE_COMPLETED:
                                                setCommonDownloadButton(ExoDownloadState.DOWNLOAD_RESUME, holder,singleItem);

                                                Log.d("EXO STATE_COMPLETED ", +downloadFromTracker.getBytesDownloaded() + " " + downloadFromTracker.contentLength);
                                                Log.d("EXO  STATE_COMPLETED ", "" + downloadFromTracker.getPercentDownloaded());
                                                break;

                                            case STATE_FAILED:
                                                setCommonDownloadButton(ExoDownloadState.DOWNLOAD_RESUME, holder,singleItem);
                                                break;

                                            case STATE_REMOVING:
                                                break;

                                            case STATE_RESTARTING:
                                                break;
                                        }
                                    }
                                }

                            }
                        }
                    });
                }
            }
        }

    }

    public void setOnEmbedItemClickListener(OnTVSeriesEpisodeItemClickListener mItemClickListener) {
        this.mOnTVSeriesEpisodeItemClickListener = mItemClickListener;
    }

    public EpisodeAdapter(String castcrew, Activity context, List<EpiModel> items) {
        this.castcrew = castcrew;
        ctx = context;
        this.items = items;

        myApplication = MyAppClass.getInstance();
        myApplication = (MyAppClass) ctx.getApplication();


        offlineDatabaseHelper = new DatabaseHelper(ctx);
        dataSourceFactory = buildDataSourceFactory();

        downloadTracker = myApplication.getDownloadTracker();
        downloadManager = myApplication.getDownloadManager();
        downloadTracker.addListener(this);

        try {
            DownloadService.start(ctx, DemoDownloadService.class);
        } catch (IllegalStateException e) {
            DownloadService.startForeground(ctx, DemoDownloadService.class);
        }



    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public EpisodeAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        EpisodeAdapter.OriginalViewHolder vh;
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_episode_item, parent, false);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_episode_item_vertical, parent, false);
        vh = new EpisodeAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final EpisodeAdapter.OriginalViewHolder holder, final int position) {
        mholder = (OriginalViewHolder) holder;

        EpiModel obj1 = items.get(position);
        obj = obj1;
        holder.name.setText(obj.getEpi());

        //holder.seasonName.setText("Season: " + obj.getSeson());
        holder.seasonName.setText(castcrew);




        //holder.seasonName.setText("Season: " + obj.getSeson().);

        getProfile(PreferenceUtils.getUserId(ctx));
        //holder.publishDate.setText(obj.);

        //check if isDark or not.
        //if not dark, change the text color
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("push", MODE_PRIVATE);
        boolean isDark = sharedPreferences.getBoolean("dark", false);
        if (!isDark) {
            holder.name.setTextColor(ctx.getResources().getColor(R.color.black));
            holder.seasonName.setTextColor(ctx.getResources().getColor(R.color.black));
            holder.publishDate.setTextColor(ctx.getResources().getColor(R.color.black));
        }

        Picasso.get()
                .load(obj.getImageUrl())
                .placeholder(R.drawable.poster_placeholder)
                .into(holder.episodIv);


        /*if (seasonNo == 0) {
            if (position==i){
                chanColor(viewHolderArray[0],position);
                ((DetailsActivity)ctx).setMediaUrlForTvSeries(obj.getStreamURL(), obj.getSeson(), obj.getEpi());
                new DetailsActivity().iniMoviePlayer(obj.getStreamURL(),obj.getServerType(),ctx);
                holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                holder.playStatusTv.setText("Playing");
                holder.playStatusTv.setVisibility(View.VISIBLE);
                viewHolderArray[0] =holder;
                i = items.size()+items.size() + items.size();

            }
        }*/


        holder.cardView.setOnClickListener(v -> {

            if (deviceNoDynamic != null) {
                if (!deviceNoDynamic.equals("")) {
                    if (!deviceNo.equals(deviceNoDynamic)) {
                        Toast.makeText(ctx, "Logged in other device", Toast.LENGTH_SHORT).show();
                        logoutUser(PreferenceUtils.getUserId(ctx));
                    } else {
                        //change
//                            DetailsActivity.castImageUrl=obj.getImageUrl();

                        ((DetailsActivity) ctx).hideDescriptionLayout();
                        ((DetailsActivity) ctx).showSeriesLayout();
                        ((DetailsActivity) ctx).setMediaUrlForTvSeries(obj.getStreamURL(), obj.getSeson(), obj.getEpi());
                        boolean castSession = ((DetailsActivity) ctx).getCastSession();
                        //Toast.makeText(ctx, "cast:"+castSession, Toast.LENGTH_SHORT).show();
                        if (!castSession) {
                            if (obj.getServerType().equalsIgnoreCase("embed")) {
                                if (mOnTVSeriesEpisodeItemClickListener != null) {
                                    mOnTVSeriesEpisodeItemClickListener.onEpisodeItemClickTvSeries("embed", v, obj, position, viewHolder);
                                }
                            } else {
                                //new DetailsActivity().initMoviePlayer(obj.getStreamURL(), obj.getServerType(), ctx);
                                if (mOnTVSeriesEpisodeItemClickListener != null) {
                                    mOnTVSeriesEpisodeItemClickListener.onEpisodeItemClickTvSeries("normal", v, obj, position, viewHolder);
                                }
                            }
                        } else {
                            ((DetailsActivity) ctx).showQueuePopup(ctx, holder.cardView, ((DetailsActivity) ctx).getMediaInfo());

                        }

                        chanColor(viewHolderArray[0], position);
                        holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                        holder.playStatusTv.setText("Playing");
                        holder.playStatusTv.setVisibility(View.VISIBLE);


                        viewHolderArray[0] = holder;
                    }
                } else {
                    ((DetailsActivity) ctx).hideDescriptionLayout();
                    ((DetailsActivity) ctx).showSeriesLayout();
                    ((DetailsActivity) ctx).setMediaUrlForTvSeries(obj.getStreamURL(), obj.getSeson(), obj.getEpi());
                    boolean castSession = ((DetailsActivity) ctx).getCastSession();
                    //Toast.makeText(ctx, "cast:"+castSession, Toast.LENGTH_SHORT).show();
                    if (!castSession) {
                        if (obj.getServerType().equalsIgnoreCase("embed")) {
                            if (mOnTVSeriesEpisodeItemClickListener != null) {
                                mOnTVSeriesEpisodeItemClickListener.onEpisodeItemClickTvSeries("embed", v, obj, position, viewHolder);
                            }
                        } else {
                            //new DetailsActivity().initMoviePlayer(obj.getStreamURL(), obj.getServerType(), ctx);
                            if (mOnTVSeriesEpisodeItemClickListener != null) {
                                mOnTVSeriesEpisodeItemClickListener.onEpisodeItemClickTvSeries("normal", v, obj, position, viewHolder);
                            }
                        }
                    } else {
                        ((DetailsActivity) ctx).showQueuePopup(ctx, holder.cardView, ((DetailsActivity) ctx).getMediaInfo());

                    }

                    chanColor(viewHolderArray[0], position);
                    holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                    holder.playStatusTv.setText("Playing");
                    holder.playStatusTv.setVisibility(View.VISIBLE);


                    viewHolderArray[0] = holder;
                }
            } else {
                ((DetailsActivity) ctx).hideDescriptionLayout();
                ((DetailsActivity) ctx).showSeriesLayout();
                ((DetailsActivity) ctx).setMediaUrlForTvSeries(obj.getStreamURL(), obj.getSeson(), obj.getEpi());
                boolean castSession = ((DetailsActivity) ctx).getCastSession();
                //Toast.makeText(ctx, "cast:"+castSession, Toast.LENGTH_SHORT).show();
                if (!castSession) {
                    if (obj.getServerType().equalsIgnoreCase("embed")) {
                        if (mOnTVSeriesEpisodeItemClickListener != null) {
                            mOnTVSeriesEpisodeItemClickListener.onEpisodeItemClickTvSeries("embed", v, obj, position, viewHolder);
                        }
                    } else {
                        //new DetailsActivity().initMoviePlayer(obj.getStreamURL(), obj.getServerType(), ctx);
                        if (mOnTVSeriesEpisodeItemClickListener != null) {
                            mOnTVSeriesEpisodeItemClickListener.onEpisodeItemClickTvSeries("normal", v, obj, position, viewHolder);
                        }
                    }
                } else {
                    ((DetailsActivity) ctx).showQueuePopup(ctx, holder.cardView, ((DetailsActivity) ctx).getMediaInfo());

                }

                chanColor(viewHolderArray[0], position);
                holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                holder.playStatusTv.setText("Playing");
                holder.playStatusTv.setVisibility(View.VISIBLE);


                viewHolderArray[0] = holder;
            }

        });

        /*Download video*/
//        downloadTracker = myApplication.getDownloadTracker();
//        downloadManager = myApplication.getDownloadManager();
//        downloadTracker.addListener(this);

//        try {
//            DownloadService.start(ctx, DemoDownloadService.class);
//        } catch (IllegalStateException e) {
//            DownloadService.startForeground(ctx, DemoDownloadService.class);
//        }

//        if (offlineDatabaseHelper.checkIfMyMovieExists(items.get(position).getEpisodeId())) {
//            holder.ll_download_video.setVisibility(View.INVISIBLE);
//        }else{
//            holder.ll_download_video.setVisibility(View.VISIBLE);
//        }

        if (offlineDatabaseHelper.checkIfMyMovieExists(items.get(position).getEpisodeId())) {
            CommonModels itemMovie = offlineDatabaseHelper.getMovieByURL(items.get(position).getStreamURL());
            if(itemMovie.getStatus() != null && itemMovie.getStatus().equals("DOWNLOAD_COMPLETED")){
                holder.ll_download_video.setVisibility(View.GONE);
            }else{
                holder.ll_download_video.setVisibility(View.VISIBLE);
            }
        }else{
            holder.ll_download_video.setVisibility(View.VISIBLE);
        }

        holder.ll_download_video.setOnClickListener(view -> {
//                if (NetworkUtils.isConnected(mContext)) {
//                    if (myApplication.getIsLogin()) {

            if (offlineDatabaseHelper.checkIfMyMovieExists(items.get(position).getEpisodeId())) {
                CommonModels itemMovie = offlineDatabaseHelper.getMovieByURL(items.get(position).getStreamURL());
                if(itemMovie.getStatus().equals("DOWNLOAD_COMPLETED")){
                    Toast.makeText(ctx, "Already completed", Toast.LENGTH_SHORT).show();
                }else{
                    if (holder.ll_download_video.getTag() == null) {
                        Log.d("","");
                        ExoDownloadState exoDownloadState = ExoDownloadState.DOWNLOAD_START;
                        exoVideoDownloadDecision(exoDownloadState, items.get(position));
                    } else {
                        ExoDownloadState exoDownloadState = (ExoDownloadState) holder.ll_download_video.getTag();
                        exoVideoDownloadDecision(exoDownloadState, items.get(position));
                    }
                }
//                System.out.println("llDownloadVideo.getTag() ==> " + holder.ll_download_video.getTag());

            } else {
                if (holder.ll_download_video.getTag() == null) {
                    Log.d("","");
                    ExoDownloadState exoDownloadState = ExoDownloadState.DOWNLOAD_START;
                    exoVideoDownloadDecision(exoDownloadState, items.get(position));
                } else {
                    ExoDownloadState exoDownloadState = (ExoDownloadState) holder.ll_download_video.getTag();
                    exoVideoDownloadDecision(exoDownloadState, items.get(position));
                }
            }
        });

        runnableCode = new Runnable() {
            @Override
            public void run() {
                observerVideoStatus(items.get(position), holder);
                handler.postDelayed(this, 1000);
            }
        };
        handler = new Handler();
        handler.post(runnableCode);

    }

    private DataSource.Factory buildDataSourceFactory() {
        return myApplication.buildDataSourceFactory();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView name, playStatusTv, seasonName, publishDate, tv_download_state;
        public MaterialRippleLayout cardView;
        public ImageView episodIv, img_download_state;
        LinearLayout ll_download_video;
        ProgressBar downloadProgress;

        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            playStatusTv = v.findViewById(R.id.play_status_tv);
            cardView = v.findViewById(R.id.lyt_parent);
            episodIv = v.findViewById(R.id.image);
            seasonName = v.findViewById(R.id.season_name);
            publishDate = v.findViewById(R.id.publish_date);
            ll_download_video = v.findViewById(R.id.ll_download_video);
            img_download_state = v.findViewById(R.id.img_download_state);
            tv_download_state = v.findViewById(R.id.tv_download_state);
            downloadProgress = v.findViewById(R.id.downloadProgress);
        }
    }

    private void chanColor(EpisodeAdapter.OriginalViewHolder holder, int pos) {

        if (holder != null) {
            holder.name.setTextColor(ctx.getResources().getColor(R.color.grey_20));
            holder.playStatusTv.setVisibility(View.GONE);
        }
    }

    private void getProfile(String uid) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        UserDataApi api = retrofit.create(UserDataApi.class);
        Call<User> call = api.getUserData(AppConfig.API_KEY, uid);
        call.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        User user = response.body();
                        deviceNoDynamic = user.getDevice_no();
                        deviceNo = Settings.Secure.getString(ctx.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });


    }

    /*private void logoutUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseAuth.getInstance().signOut();
        }

        SharedPreferences.Editor editor = ctx.getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
        editor.putBoolean(Constants.USER_LOGIN_STATUS, false);
        editor.apply();
        editor.commit();

        DatabaseHelper databaseHelper = new DatabaseHelper(ctx);
        databaseHelper.deleteUserData();

        PreferenceUtils.clearSubscriptionSavedData(ctx);

        Intent intent = new Intent(ctx, LoginActivity.class);
        ctx.startActivity(intent);
        ((Activity) ctx).finish();
    }*/

    private void logoutUser(String id) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        LoginApi api = retrofit.create(LoginApi.class);
        Call<User> call = api.postLogoutStatus(AppConfig.API_KEY, id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            FirebaseAuth.getInstance().signOut();
                        }

                        SharedPreferences.Editor editor = ctx.getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
                        editor.putBoolean(Constants.USER_LOGIN_STATUS, false);
                        editor.apply();
                        editor.commit();

                        DatabaseHelper databaseHelper = new DatabaseHelper(ctx);
                        databaseHelper.deleteUserData();

                        PreferenceUtils.clearSubscriptionSavedData(ctx);

                        Intent intent = new Intent(ctx, LoginActivity.class);
                        ctx.startActivity(intent);
                        ((Activity) ctx).finish();
                    } else {
                        new ToastMsg(ctx).toastIconError(response.body().getData());

                    }
                } else {

                    new ToastMsg(ctx).toastIconError(ctx.getString(R.string.error_toast));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                new ToastMsg(ctx).toastIconError(ctx.getString(R.string.error_toast));
            }
        });
    }


    private void exoVideoDownloadDecision(ExoDownloadState exoDownloadState, EpiModel model) {
//        if (exoDownloadState == null || Remember.getString("movie_url", "0").isEmpty()) {
//            Toast.makeText(this, "Please, Tap Again", Toast.LENGTH_SHORT).show();
//            return;
//        }

        switch (exoDownloadState) {

            case DOWNLOAD_START:
                fetchDownloadOptions(model);

                downloadDevicePushEvent();


                break;

            case DOWNLOAD_PAUSE:
                Toast.makeText(ctx, "Download paused.... ", Toast.LENGTH_SHORT).show();
                downloadManager.addDownload(downloadTracker.getDownloadRequest(Uri.parse(model.getStreamURL())), Download.STATE_STOPPED);
                break;

            case DOWNLOAD_RESUME:

                Toast.makeText(ctx, "Downloaded started.... ", Toast.LENGTH_SHORT).show();
                downloadManager.addDownload(downloadTracker.getDownloadRequest(Uri.parse(model.getStreamURL())), Download.STOP_REASON_NONE);
//                downloadManager.addDownload(downloadTracker.getDownloadRequest(Uri.parse(model.getStreamURL())), Download.STATE_DOWNLOADING);
                break;

            case DOWNLOAD_RETRY:

                break;

            case DOWNLOAD_COMPLETED:
                Toast.makeText(ctx, "Already Downloaded, Delete from Downloaded video ", Toast.LENGTH_SHORT).show();

                break;
        }
    }


    private void downloadDevicePushEvent() {

        clevertapdownloaddevice= CleverTapAPI.getDefaultInstance(getApplicationContext());
        clevertapdownloaddevice.pushEvent("Android");

    }

    private void fetchDownloadOptions(EpiModel model) {
        trackKeys.clear();

        ExoTrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();

        trackSelector = new DefaultTrackSelector(ctx, trackSelectionFactory);
        if (pDialog == null || !pDialog.isShowing()) {
            pDialog = new ProgressDialog(ctx);
            pDialog.setTitle(null);
            pDialog.setCancelable(false);
            pDialog.setMessage("Preparing Download Options...");
            pDialog.show();
        }


        DownloadHelper downloadHelper = DownloadHelper.forHls(ctx, Uri.parse(model.getStreamURL()), dataSourceFactory, new DefaultRenderersFactory(ctx));


        downloadHelper.prepare(new DownloadHelper.Callback() {
            @Override
            public void onPrepared(DownloadHelper helper) {
                myDownloadHelper = helper;
                for (int i = 0; i < helper.getPeriodCount(); i++) {
                    TrackGroupArray trackGroups = helper.getTrackGroups(i);
                    for (int j = 0; j < trackGroups.length; j++) {
                        TrackGroup trackGroup = trackGroups.get(j);
                        for (int k = 0; k < trackGroup.length; k++) {
                            Format track = trackGroup.getFormat(k);
                            if (shouldDownload(track)) {
                                trackKeys.add(new TrackKey(trackGroups, trackGroup, track));
                            }
                        }
                    }
                }

                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }


                optionsToDownload.clear();
                showDownloadOptionsDialog(myDownloadHelper, trackKeys, model);
            }

            @Override
            public void onPrepareError(DownloadHelper helper, IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void showDownloadOptionsDialog(DownloadHelper helper, List<TrackKey> trackKeyss, EpiModel model) {

        if (helper == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Select Download Format");
        int checkedItem = 1;


        for (int i = 0; i < trackKeyss.size(); i++) {
            TrackKey trackKey = trackKeyss.get(i);
            String videoResoultionDashSize = " " + trackKey.getTrackFormat().height + "      (" + "MB" + ")";
            optionsToDownload.add(i, videoResoultionDashSize);
        }

        // Initialize a new array adapter instance
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(
                ctx, // Context
                android.R.layout.simple_list_item_single_choice, // Layout
                optionsToDownload // List
        );

        TrackKey trackKey = trackKeyss.get(0);
        qualityParams = ((DefaultTrackSelector) trackSelector).getParameters().buildUpon()
                .setMaxVideoSize(trackKey.getTrackFormat().width, trackKey.getTrackFormat().height)
                .setMaxVideoBitrate(trackKey.getTrackFormat().bitrate)
                .build();

        builder.setSingleChoiceItems(arrayAdapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TrackKey trackKey = trackKeyss.get(i);

                qualityParams = ((DefaultTrackSelector) trackSelector).getParameters().buildUpon()
                        .setMaxVideoSize(trackKey.getTrackFormat().width, trackKey.getTrackFormat().height)
                        .setMaxVideoBitrate(trackKey.getTrackFormat().bitrate)
                        .build();


            }
        });
        // Set the a;ert dialog positive button
        builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                CommonModels itemMovie = new CommonModels();
                itemMovie.setId(model.getEpisodeId());
                itemMovie.setMovieName(model.getEpi());
                itemMovie.setMovieDuration("0");
                itemMovie.setImageUrl(model.getImageUrl());
                itemMovie.setStremURL(model.getStreamURL());
                itemMovie.setStatus("DOWNLOAD_START");
                offlineDatabaseHelper.addEpisodeData(itemMovie);

                for (int periodIndex = 0; periodIndex < helper.getPeriodCount(); periodIndex++) {
                    MappingTrackSelector.MappedTrackInfo mappedTrackInfo = helper.getMappedTrackInfo(/* periodIndex= */ periodIndex);
                    helper.clearTrackSelections(periodIndex);
                    for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
//                        TrackGroupArray rendererTrackGroups = mappedTrackInfo.getTrackGroups(i);
                        helper.addTrackSelection(
                                periodIndex,
                                qualityParams);
                    }

                }


                DownloadRequest downloadRequest = helper.getDownloadRequest(Util.getUtf8Bytes(model.getStreamURL()));
                if (downloadRequest.streamKeys.isEmpty()) {
                    // All tracks were deselected in the dialog. Don't start the download.
                    return;
                }

                startDownload(downloadRequest);

                dialogInterface.dismiss();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    private void startDownload(DownloadRequest downloadRequestt) {

        DownloadRequest myDownloadRequest = downloadRequestt;

        if (myDownloadRequest.uri.toString().isEmpty()) {
            Toast.makeText(ctx, "Try Again!!", Toast.LENGTH_SHORT).show();

            return;
        } else {
            downloadManager.addDownload(myDownloadRequest);
        }
    }

    private boolean shouldDownload(Format track) {
        return track.height != 240 && track.sampleMimeType.equalsIgnoreCase("video/avc");
    }
}

