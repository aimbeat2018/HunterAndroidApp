package ott.hunter.reels;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import okhttp3.ResponseBody;
import ott.hunter.AppConfig;
import ott.hunter.DetailsActivity;
import ott.hunter.R;
import ott.hunter.SearchGridAdapter;
import ott.hunter.SubscriptionActivity;
import ott.hunter.adapters.InactiveSubscriptionAdapter;
import ott.hunter.databinding.ActivityReelsBinding;
import ott.hunter.databinding.ItemReelsBinding;
import ott.hunter.firebaseservice.Config;
import ott.hunter.models.ReelsModel;
import ott.hunter.network.OnReelsVideoAdapterListner;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.ReelsApi;
import ott.hunter.network.apis.SubscriptionApi;
import ott.hunter.network.model.SubscriptionHistory;
import ott.hunter.utils.MyAppClass;
import ott.hunter.utils.PreferenceUtils;
import paytm.assist.easypay.easypay.appinvoke.BuildConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReelsActivity extends AppCompatActivity implements Player.Listener {

    ActivityReelsBinding binding;
    //    RecyclerView recList;
    private String userId = "";
    List<ReelsModel> reelsModelList = new ArrayList<>();
    private SimpleExoPlayer player;
    private ItemReelsBinding playerBinding;
    private int lastPosition = -1;
    ReelsAdapter reelsAdapter = new ReelsAdapter();
    int position = -1;
    String reelId = "";
    private DefaultTrackSelector trackSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reels);
        trackSelector = new DefaultTrackSelector(this);
        player = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build();

        binding.imgBack.setOnClickListener(v -> onBackPressed());

        reelId = getIntent().getStringExtra("reelId");

        Log.d("TAG", "onStart:ReelId  " + reelId);

//        recList = findViewById(R.id.recList);
//        binding.recList.setLayoutManager(new LinearLayoutManager(this));

        userId = PreferenceUtils.getUserId(ReelsActivity.this);

        new PagerSnapHelper().attachToRecyclerView(binding.recList);

        binding.recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int position = ((LinearLayoutManager) binding.recList.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                    if (!(position <= -1) && lastPosition != position) {
                        if (binding.recList.getLayoutManager() != null) {
                            View view = binding.recList.getLayoutManager().findViewByPosition(position);
                            if (view != null) {
                                lastPosition = position;
                                ItemReelsBinding binding1 = DataBindingUtil.bind(view);
                                if (binding1 != null) {
//                                    binding1.lytSound.startAnimation(animation);
                                    //new GlobalApi().increaseView(binding1.getModel().getPostId());
                                    playVideo(reelsModelList.get(position).getReelsId(), reelsModelList.get(position).getVideoLink(), binding1, reelsModelList.get(position), position);
                                }
                            }
                        }
                    }
                }
            }
        });

        getReelsList();
    }

    private void showMediaButton() {
        if (player != null) {
            if (player.getPlayWhenReady()) {
                binding.imgMedia.setImageDrawable(ContextCompat.getDrawable(ReelsActivity.this, R.drawable.exo_icon_play));
            } else {
                binding.imgMedia.setImageDrawable(ContextCompat.getDrawable(ReelsActivity.this, R.drawable.exo_icon_pause));
            }
            binding.imgMedia.setVisibility(View.VISIBLE);
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> binding.imgMedia.setVisibility(View.GONE), 1000);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_BUFFERING) {
            binding.pd.setVisibility(View.VISIBLE);
            if (playerBinding != null) {
                binding.buffering.setVisibility(View.VISIBLE);
            }
        } else if (playbackState == Player.STATE_READY) {
            binding.pd.setVisibility(View.GONE);
            if (playerBinding != null) {
                binding.buffering.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        if (player != null) {
            player.setPlayWhenReady(true);
        }


        super.onResume();
    }

    @Override
    public void onStop() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
        }
        super.onDestroy();
    }

    private void getReelsList() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        ReelsApi subscriptionApi = retrofit.create(ReelsApi.class);
        Call<List<ReelsModel>> call = subscriptionApi.getReels(AppConfig.API_KEY, userId);
        call.enqueue(new Callback<List<ReelsModel>>() {
            @Override
            public void onResponse(Call<List<ReelsModel>> call, Response<List<ReelsModel>> response) {
                if (response.code() == 200) {
                    reelsModelList.clear();
                    reelsModelList = response.body();

//                    reelsAdapter = new ReelsAdapter();

                    binding.recList.setAdapter(reelsAdapter);


                    if (!reelId.equals("")) {

                        for (int i = 0; i < reelsModelList.size(); i++) {
                            if (reelsModelList.get(i).getReelsId().equals(reelId)) {
                                position = i;
                            }
                        }
                        if (position != -1) {
                            binding.recList.scrollToPosition(position);
//                            Log.d("Video", "after scroll:URL1234 ");
                            if (binding.recList.getLayoutManager() != null) {
                                new Handler(Looper.myLooper()).postDelayed(() -> {
                                    View view = binding.recList.getLayoutManager().findViewByPosition(position);
                                    if (view != null) {
                                        lastPosition = position;
                                        ItemReelsBinding binding1 = DataBindingUtil.bind(view);
                                        if (binding1 != null) {
//                                            Log.d("Video", "playVideo:URL1234 ");
//                                    binding1.lytSound.startAnimation(animation);
                                            //new GlobalApi().increaseView(binding1.getModel().getPostId());
                                            playVideo(reelsModelList.get(position).getReelsId(), reelsModelList.get(position).getVideoLink(), binding1, reelsModelList.get(position), position);
                                        }
                                    } else {
//                                        Log.d("Video", "null scroll:URL1234 ");
                                    }
                                }, 500);

                            }
                        }
                    }

                    reelsAdapter.setOnReelsVideoAdapterListner(new OnReelsVideoAdapterListner() {
                        @Override
                        public void onItemClick(ItemReelsBinding reelsBinding, int pos, int type) {

                            Log.d("TAG", "onItemClick: " + type);
                            if (type == 1) {
                                lastPosition = pos;
                                playVideo(reelsModelList.get(pos).getVideoId(), reelsModelList.get(pos).getVideoLink(), reelsBinding, reelsModelList.get(pos), pos);
                            } else {
                                if (player != null) {
                                    if (player.isPlaying()) {
                                        player.setPlayWhenReady(false);

                                    } else {
                                        player.setPlayWhenReady(true);
                                    }
                                    showMediaButton();
                                }

                            }
               /* lastPosition = pos;
                reelsBinding.imgSound.startAnimation(animation);
                playVideo(viewModel.reelsAdapter.getList().get(pos).getVideo(), reelsBinding);*/
                        }

                        @Override
                        public void onOpenClick(ItemReelsBinding reelsBinding, int pos) {
                            Intent intent = new Intent(ReelsActivity.this, DetailsActivity.class);
                            intent.putExtra("vType", "tvseries");
                            intent.putExtra("id", reelsModelList.get(pos).getVideoId());

//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                        @Override
                        public void onShareClick(ItemReelsBinding reelsBinding, int pos) {

                            shareReels(reelsModelList.get(pos));
//                            FirebaseDynamicLinks dynamicLinks = FirebaseDynamicLinks.getInstance();
//
//                            String link = "https://primeplay.page.link/" + reelsModelList.get(pos).getReelsId();
//                            Uri deepLink = Uri.parse(link);
//                            String packageName = getPackageName();
//
//                            DynamicLink.Builder linkBuilder = dynamicLinks.createDynamicLink()
//                                    .setLink(deepLink)
//                                    .setDomainUriPrefix("https://primeplay.page.link")
//                                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder(packageName).build())
//                                    .setNavigationInfoParameters(
//                                            new DynamicLink.NavigationInfoParameters.Builder().setForcedRedirectEnabled(true).build())
//                                    .setSocialMetaTagParameters(
//                                            new DynamicLink.SocialMetaTagParameters.Builder().setTitle("").build());
//
//
////                                    .setGoogleAnalyticsParameters(
////                                            new DynamicLink.GoogleAnalyticsParameters.Builder()
////                                                    .setSource("example-android-app")
////                                                    .setMedium("social")
////                                                    .setCampaign("example-promo")
////                                                    .build())
////                                    .setItunesConnectAnalyticsParameters(
////                                            new DynamicLink.ItunesConnectAnalyticsParameters.Builder().setProviderToken("123456").build())
////                                    .setWarning(Warning.SUFFIX_WARNING);
//
//                            String shareText = "Download the Primeplay app and watch (" + reelsModelList.get(pos).getTitle() + ")\n\n";
//
//                            String finalShare = shareText + linkBuilder.getLink();
//                            Intent intent = new Intent();
//                            intent.setAction(Intent.ACTION_SEND);
//                            intent.setType("text/plain");
//                            intent.putExtra(Intent.EXTRA_TEXT, finalShare);
//                            startActivity(Intent.createChooser(intent, "Share"));
                        }

                        @Override
                        public void onDoubleClick(ReelsModel model, MotionEvent event, ItemReelsBinding binding) {
                        }

                        @Override
                        public void onClickLike(ItemReelsBinding reelsBinding, String likeStatus, int pos) {
                            ReelsModel model = reelsModelList.get(pos);
                            if (userId != null)
                                if (!userId.equals(""))
                                    likeVideo(model.getReelsId(), reelsBinding, likeStatus, pos);
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<List<ReelsModel>> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });

    }

    private void shareReels(ReelsModel model) {
        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setTitle(model.getTitle())
                .setContentDescription(model.getVideoDescription())
//                .setContentImageUrl(BuildConfig.BASE_URL + reel.getScreenshot())
                .setContentMetadata(new ContentMetadata().addCustomMetadata("type", "reel").addCustomMetadata(Config.DATA, model.getReelsId()));

        LinkProperties lp = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .setCampaign("content 123 launch")
                .setStage("new user")

                .addControlParameter("", "")
                .addControlParameter("", Long.toString(Calendar.getInstance().getTimeInMillis()));

        buo.generateShortUrl(getApplicationContext(), lp, (url, error) -> {
            Log.d("TAG", "initListnear: branch url" + url);
            try {
                Log.d("TAG", "initListnear: share");
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareMessage = url;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                Log.d("TAG", "initListnear: " + e.getMessage());
                //e.toString();
            }
        });
    }

    public class ReelsAdapter extends RecyclerView.Adapter<ReelsAdapter.ViewHolder> {

        private int playAtPosition = 0;


        OnReelsVideoAdapterListner onReelsVideoAdapterListner;

        OnReelsVideoAdapterListner getOnReelsVideoAdapterListner() {
            return onReelsVideoAdapterListner;
        }

        public void setOnReelsVideoAdapterListner(OnReelsVideoAdapterListner onReelsVideoAdapterListner) {
            this.onReelsVideoAdapterListner = onReelsVideoAdapterListner;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reels, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setData(reelsModelList.get(position), position);
        }


        @Override
        public int getItemCount() {
            return reelsModelList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemReelsBinding itemReelsBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                itemReelsBinding = ItemReelsBinding.bind(itemView);
            }

            public void setData(ReelsModel reelsModel, int position) {

                itemReelsBinding.tvVideoName.setText(reelsModel.getTitle());
                itemReelsBinding.tvVideoDesc.setText(reelsModel.getVideoDescription());
                itemReelsBinding.tvLikeCount.setText(reelsModel.getTotalLikeCount());
                itemReelsBinding.tvViewCount.setText(reelsModel.getTotalView());

                if (userId != null) {
                    if (userId.equals("")) {
                        itemReelsBinding.relLike.setVisibility(View.GONE);
                        itemReelsBinding.lnrView.setVisibility(View.GONE);
                    } else {
                        itemReelsBinding.relLike.setVisibility(View.VISIBLE);
                        itemReelsBinding.lnrView.setVisibility(View.VISIBLE);
                    }
                } else {
                    itemReelsBinding.relLike.setVisibility(View.VISIBLE);
                    itemReelsBinding.lnrView.setVisibility(View.VISIBLE);
                }


                if (reelsModel.getLikeReels().equals("0"))
                    itemReelsBinding.likebtn.setLiked(false);
                else
                    itemReelsBinding.likebtn.setLiked(true);

                itemReelsBinding.relOpen.setOnClickListener(v -> onReelsVideoAdapterListner.onOpenClick(itemReelsBinding, position));

                itemReelsBinding.relShare.setOnClickListener(v -> onReelsVideoAdapterListner.onShareClick(itemReelsBinding, position));


                if (position == playAtPosition) {
                    onReelsVideoAdapterListner.onItemClick(itemReelsBinding, playAtPosition, 1);

                }

                itemReelsBinding.likebtn.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        onReelsVideoAdapterListner.onClickLike(itemReelsBinding, "1", position);
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        onReelsVideoAdapterListner.onClickLike(itemReelsBinding, "0", position);
                    }
                });

                itemReelsBinding.playerView.setOnTouchListener(new View.OnTouchListener() {
                    GestureDetector gestureDetector = new GestureDetector(itemReelsBinding.getRoot().getContext(), new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {
                            Log.d("TAGA", "onSingleTapUp: ");

                            return true;
                        }

                        @Override
                        public void onShowPress(MotionEvent e) {
                            Log.d("TAGA", "onShowPress: ");
                            super.onShowPress(e);
                        }

                        @Override
                        public boolean onSingleTapConfirmed(MotionEvent e) {
                            Log.d("TAGA", "onSingleTapConfirmed: ");
                            onReelsVideoAdapterListner.onItemClick(itemReelsBinding, position, 2);
                            return super.onSingleTapConfirmed(e);
                        }

                        @Override
                        public void onLongPress(MotionEvent e) {
                            // onReelsVideoAdapterListner.onItemClick(reel, position, 8, binding);
                            super.onLongPress(e);
                        }

                        @Override
                        public boolean onDoubleTap(MotionEvent e) {
                            Log.d("TAGA", "onDoubleTap: ");
                            onReelsVideoAdapterListner.onDoubleClick(reelsModel, e, itemReelsBinding);
                            return true;
                        }
                    });

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        gestureDetector.onTouchEvent(event);
                        return false;
                    }
                });

            }
        }

    }

//    private void playVideo(String videoUrl, ItemReelsBinding binding) {
//        if (player != null) {
//            player.removeListener(this);
//            player.setPlayWhenReady(false);
//            player.release();
//        }
//        Log.d("TAG", "playVideo: ");
//        playerBinding = binding;
//        player = new SimpleExoPlayer.Builder(ReelsActivity.this).build();
//        //    SimpleCache simpleCache = MyApp.simpleCache;
//      /*  CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(simpleCache, new DefaultHttpDataSourceFactory(Util.getUserAgent(ReelsActivity.this, "TejTok"))
//                , CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
//*/
//        //    ProgressiveMediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(Uri.parse(videoUrl));
//        binding.playerView.setPlayer(player);
//        player.setPlayWhenReady(true);
//        player.seekTo(0, 0);
//        player.setRepeatMode(Player.REPEAT_MODE_ALL);
//        player.addListener(this);
//        binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
//        MediaSource mediaSource = buildMediaSource(Uri.parse(videoUrl));
//        player.prepare(mediaSource, true, false);
//    }

    private void playVideo(String videoId, String videoUrl, ItemReelsBinding binding, ReelsModel model, int pos) {
        int HI_BITRATE = 2097152;
        int MI_BITRATE = 1048576;
        int LO_BITRATE = 524288;
//        videoUrl = "https://casanovasheaven.com/casanova/public/asset/uploaded_videos/videos/16e65488-8c69-4702-862e-60c9036ffd4a.mp4";
        if (player != null) {
            player.removeListener(this);
            player.setPlayWhenReady(false);
            player.release();
        }
        Log.d("TAG", "playVideo:URL  " + videoUrl);
        playerBinding = binding;

//        DefaultTrackSelector.Parameters defaultTrackParam = trackSelector.buildUponParameters().build();

        DefaultTrackSelector.Parameters parameters = trackSelector.buildUponParameters()
                .setMaxVideoBitrate(LO_BITRATE)
                .setForceHighestSupportedBitrate(true)
                .build();

        trackSelector.setParameters(parameters);

        player = new SimpleExoPlayer.Builder(ReelsActivity.this).setTrackSelector(trackSelector).build();

//        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        SimpleCache simpleCache = MyAppClass.simpleCache;
        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(simpleCache, new DefaultHttpDataSourceFactory(Util.getUserAgent(ReelsActivity.this, "Primeplay"))
                , CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
        ProgressiveMediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(Uri.parse(videoUrl));
        binding.playerView.setPlayer(player);
        player.setPlayWhenReady(true);
        player.seekTo(0, 0);
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.addListener(this);
        binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        MediaSource mediaSource = hlsMediaSource(Uri.parse(videoUrl), this);
        player.prepare(mediaSource, true, false);


        if (userId != null)
            if (!userId.equals(""))
                watchVideo(videoId, binding, model, pos);
    }

    private void likeVideo(String videoId, ItemReelsBinding reelsBinding, String likeStatus, int pos) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        ReelsApi subscriptionApi = retrofit.create(ReelsApi.class);
        Call<ResponseBody> call = subscriptionApi.likeUnlikeReels(AppConfig.API_KEY, userId, videoId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    ReelsModel model = reelsModelList.get(pos);
                    int like;
                    if (likeStatus.equals("1")) {
                        like = Integer.parseInt(model.getTotalLikeCount()) + 1;
                    } else {
                        like = Integer.parseInt(model.getTotalLikeCount()) - 1;
                    }
                    if (model.getLikeReels().equals("1")) {
                        reelsBinding.likebtn.setLiked(true);
                    } else {
                        reelsBinding.likebtn.setLiked(false);
                    }

                    reelsBinding.tvLikeCount.setText(String.valueOf(like));

                    if (likeStatus.equals("1")) {
                        model.setLikeReels("1");
                    } else {
                        model.setLikeReels("0");
                    }

                    model.setTotalLikeCount(String.valueOf(like));
                    reelsAdapter.notifyItemChanged(pos, model);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });

    }

    private void watchVideo(String videoId, ItemReelsBinding reelsBinding, ReelsModel model, int pos) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        ReelsApi subscriptionApi = retrofit.create(ReelsApi.class);
        Call<ResponseBody> call = subscriptionApi.videoView(AppConfig.API_KEY, userId, videoId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
//                    int viewCount = Integer.parseInt(model.getTotalView()) + 1;
//
//                    reelsBinding.tvViewCount.setText(String.valueOf(viewCount));
//                    model.setTotalView(String.valueOf(viewCount));
//                    reelsAdapter.notifyItemChanged(pos, model);

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });

    }


    private MediaSource hlsMediaSource(Uri uri, Context context) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "primeplay"), bandwidthMeter);

        MediaSource videoSource = new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
        return videoSource;
    }
}