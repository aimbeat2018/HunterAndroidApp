package ott.hunter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.mediarouter.app.MediaRouteButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.cast.CastPlayer;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import okhttp3.ResponseBody;
import ott.hunter.adapters.CastCrewAdapter;
import ott.hunter.adapters.CommentsAdapter;
import ott.hunter.adapters.DownloadAdapter;
import ott.hunter.adapters.GoldEpisodeAdapter;
import ott.hunter.adapters.HomePageAdapter;
import ott.hunter.adapters.ProgramAdapter;
import ott.hunter.adapters.RelatedTvAdapter;
import ott.hunter.adapters.ServerAdapter;
import ott.hunter.database.DatabaseHelper;
import ott.hunter.database.continueWatching.ContinueWatchingModel;
import ott.hunter.database.continueWatching.ContinueWatchingViewModel;
import ott.hunter.database.downlaod.DownloadViewModel;
import ott.hunter.models.CastCrew;
import ott.hunter.models.CommonModels;
import ott.hunter.models.EpiModel;
import ott.hunter.models.GetCommentsModel;
import ott.hunter.models.PostCommentModel;
import ott.hunter.models.Program;
import ott.hunter.models.SubtitleModel;
import ott.hunter.models.single_details.Cast;
import ott.hunter.models.single_details.Director;
import ott.hunter.models.single_details.DownloadLink;
import ott.hunter.models.single_details.Episode;
import ott.hunter.models.single_details.Genre;
import ott.hunter.models.single_details.RelatedMovie;
import ott.hunter.models.single_details.Season;
import ott.hunter.models.single_details.SingleDetails;
import ott.hunter.models.single_details.Subtitle;
import ott.hunter.models.single_details.Video;
import ott.hunter.models.single_details.VideoQuality;
import ott.hunter.models.single_details_tv.AdditionalMediaSource;
import ott.hunter.models.single_details_tv.AllTvChannel;
import ott.hunter.models.single_details_tv.ProgramGuide;
import ott.hunter.models.single_details_tv.SingleDetailsTV;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.CommentApi;
import ott.hunter.network.apis.FavouriteApi;
import ott.hunter.network.apis.ReportApi;
import ott.hunter.network.apis.SingleDetailsApi;
import ott.hunter.network.apis.SingleDetailsTVApi;
import ott.hunter.network.apis.SubscriptionApi;
import ott.hunter.network.apis.UserDataApi;
import ott.hunter.network.model.ActiveStatus;
import ott.hunter.network.model.FavoriteModel;
import ott.hunter.network.model.User;
import ott.hunter.utils.Constants;
import ott.hunter.utils.HelperUtils;
import ott.hunter.utils.PreferenceUtils;
import ott.hunter.utils.RtlUtils;
import ott.hunter.utils.ToastMsg;
import ott.hunter.utils.Tools;
import ott.hunter.utils.TrackSelectionDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@SuppressLint("StaticFieldLeak")
@SuppressWarnings("unchecked")
public class GoldDetailsActivity extends AppCompatActivity implements CastPlayer.SessionAvailabilityListener, ProgramAdapter.OnProgramClickListener, GoldEpisodeAdapter.OnTVSeriesEpisodeItemClickListener,
        RelatedTvAdapter.RelatedTvClickListener {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PRELOAD_TIME_S = 20;
    public static final String TAG = GoldDetailsActivity.class.getSimpleName();
    private TextView tvName, tvDirector, tvRelease, tvDes, tvGenre, tvRelated;
    private RecyclerView rvServer, rvServerForTV, rvRelated, rvComment, castRv;
    private Spinner seasonSpinner;
    private LinearLayout seasonSpinnerContainer;
    public static RelativeLayout lPlay;
    private RelativeLayout contentDetails;
    private LinearLayout subscriptionLayout, topbarLayout;
    private AppCompatButton subscribeBt;
    private ImageView backIv, subBackIv;
    LinearLayoutCompat lnr_buy_gold;
    AppCompatButton rent_bt;

    private ServerAdapter serverAdapter;
    private HomePageAdapter relatedAdapter;
    private RelatedTvAdapter relatedTvAdapter;
    private CastCrewAdapter castCrewAdapter;

    private String V_URL = "";
    public static WebView webView;
    public static ProgressBar progressBar;
    private boolean isFav = false;
    private TextView chromeCastTv;
    private final List<CommonModels> listServer = new ArrayList<>();
    private final List<CommonModels> listRelated = new ArrayList<>();
    private final List<GetCommentsModel> listComment = new ArrayList<>();
    //private final List<CommonModels> listDownload = new ArrayList<>();
    private final List<CommonModels> listInternalDownload = new ArrayList<>();
    private final List<CommonModels> listExternalDownload = new ArrayList<>();
    private final List<CastCrew> castCrews = new ArrayList<>();
    private String strDirector = "", strGenre = "";
    public static LinearLayout llBottom, llBottomParent;
    public static RelativeLayout llcomment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String categoryType = "", id = "", is_gold = "", amt = "";
    private ImageButton imgAddFav, shareIv2, reportIv;
    public static ImageView imgBack, serverIv;
    private Button watchNowBt;
    TextView trailerBt;
    ImageView downloadBt;
    private LinearLayout downloadAndTrailerBtContainer;
    private ImageView posterIv, thumbIv;

    private ShimmerFrameLayout shimmerFrameLayout;
    private Button btnComment;
    private EditText etComment;
    private CommentsAdapter commentsAdapter;
    private RelativeLayout adView;

    public static SimpleExoPlayer player;
    public static PlayerView simpleExoPlayerView;
    private boolean isShowingTrackSelectionDialog;
    public static FrameLayout youtubePlayerView;
    private static RelativeLayout exoplayerLayout;
    public PlayerControlView castControlView;
    public static SubtitleView subtitleView;
    private DefaultTrackSelector trackSelector;

    public static ImageView imgFull;
    public ImageView aspectRatioIv, externalPlayerIv, volumControlIv;
    private LinearLayout volumnControlLayout;
    private SeekBar volumnSeekbar;
    public MediaRouteButton mediaRouteButton;

    public static boolean isPlaying, isFullScr;
    public static View playerLayout;

    private int playerHeight;
    public static boolean isVideo = true;
    private final String strSubtitle = "Null";
    public static MediaSource mediaSource = null;
    public static ImageView imgSubtitle, imgAudio, img_quality;
    private final List<SubtitleModel> listSub = new ArrayList<>();
    private AlertDialog alertDialog;
    private String mediaUrl;
    private boolean isFromContinueWatching = false;
    private boolean tv = false;
    private String download_check = "";
    private String trailerUrl = "";

    private String season;
    private String episod;
    private String movieTitle;
    private String seriesTitle;
    private int checkdItem2 = 1;

    private CastPlayer castPlayer;
    private boolean castSession;
    private String title;
    String castImageUrl;
    private String url;

    private LinearLayout tvLayout, sheduleLayout, tvTopLayout;
    private TextView tvTitleTv, watchStatusTv, timeTv, programTv, proGuideTv, watchLiveTv;
    private ProgramAdapter programAdapter;
    List<Program> programs = new ArrayList<>();
    private RecyclerView programRv;
    private ImageView tvThumbIv, shareIv, tvReportIV;

    private LinearLayout exoRewind, exoForward, seekbarLayout;
    ImageView exoDownloadIv;
    private TextView liveTv;

    boolean isDark;
    private OrientationEventListener myOrientationEventListener;
    private static String serverType;

    private boolean fullScreenByClick;
    private String currentProgramTime;
    private String currentProgramTitle;
    private String userId;

    private String urlType = "";
    private RelativeLayout descriptionLayout;
    //    private MaterialRippleLayout descriptionContatainer;
    private RelativeLayout rel_back;
    private TextView dGenryTv;
    private RecyclerView internalServerRv, externalServerRv, serverRv;
    private LinearLayout internalDownloadLayout, externalDownloadLayout;
    private boolean activeMovie;

    private TextView sereisTitleTv;
    private RelativeLayout seriestLayout;
    private ImageView favIv;

    private RelativeLayout mRlTouch;
    private boolean intLeft, intRight;
    private int sWidth, sHeight;
    private long diffX, diffY;
    private Display display;
    private Point size;
    private float downX, downY;
    private AudioManager mAudioManager;
    private int aspectClickCount = 1;

    private DatabaseHelper db;
    private HelperUtils helperUtils;
    private boolean vpnStatus;
    private ContinueWatchingViewModel viewModel;
    private long resumePosition = 0L;
    private static long playerCurrentPosition = 0L;
    private static long mediaDuration = 0L;
    //season download
    private LinearLayout seasonDownloadLayout;
    private Spinner seasonDownloadSpinner;
    private RecyclerView seasonDownloadRecyclerView;
    private DownloadViewModel downloadViewModel;

    private int checkdItem = 2;
    EpiModel epiModel;
    private final Handler handler1 = new Handler();
    Runnable mToastRunnable;
    LinearLayoutCompat lnr_buy_subsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RtlUtils.setScreenDirection(this);
        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("dark", false);
        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //check vpn connection
        helperUtils = new HelperUtils(GoldDetailsActivity.this);
        vpnStatus = helperUtils.isVpnConnectionAvailable();
        if (vpnStatus) {
            helperUtils.showWarningDialog(GoldDetailsActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
            return;
        }

        db = new DatabaseHelper(GoldDetailsActivity.this);

        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        //---analytics-----------
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "details_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        initViews();

        if (isDark) {
            tvTopLayout.setBackgroundColor(getResources().getColor(R.color.black_window_light));
            sheduleLayout.setBackground(getResources().getDrawable(R.drawable.rounded_black_transparent));
            etComment.setBackground(getResources().getDrawable(R.drawable.round_grey_transparent));
            btnComment.setTextColor(getResources().getColor(R.color.grey_20));
            topbarLayout.setBackgroundColor(getResources().getColor(R.color.dark));
            subscribeBt.setBackground(getResources().getDrawable(R.drawable.btn_rounded_dark));

            rel_back.setBackground(getResources().getDrawable(R.drawable.gradient_black_transparent));
//            descriptionContatainer.setBackground(getResources().getDrawable(R.drawable.gradient_black_transparent));
        }
        // chrome cast
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), mediaRouteButton);
        CastContext castContext = CastContext.getSharedInstance(this);
        castPlayer = new CastPlayer(castContext);
        castPlayer.setSessionAvailabilityListener(this);


        // cast button will show if the cast device will be available
        if (castContext.getCastState() != CastState.NO_DEVICES_AVAILABLE)
            mediaRouteButton.setVisibility(View.VISIBLE);
        // start the shimmer effect
        shimmerFrameLayout.setVisibility(VISIBLE);
        shimmerFrameLayout.startShimmer();
        playerHeight = lPlay.getLayoutParams().height;
        progressBar.setMax(100); // 100 maximum value for the progress value
        progressBar.setProgress(50);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updateContinueWatchingData();
                if (activeMovie) {
                    setPlayerNormalScreen();
                    if (player != null) {
                        player.setPlayWhenReady(false);
                        player.stop();
                    }
                    showDescriptionLayout();
                    activeMovie = false;
                } else {
                    //finish();
                    onBackPressed();
                }
            }
        });

        categoryType = getIntent().getStringExtra("vType");
        id = getIntent().getStringExtra("id");
        is_gold = getIntent().getStringExtra("is_gold");
        amt = getIntent().getStringExtra("amt");
        castSession = getIntent().getBooleanExtra("castSession", false);

        //handle Continue watching task
        isFromContinueWatching = getIntent().getBooleanExtra(Constants.IS_FROM_CONTINUE_WATCHING, false);
        try {
            if (isFromContinueWatching) {
                id = getIntent().getStringExtra(Constants.CONTENT_ID);
                title = getIntent().getStringExtra(Constants.CONTENT_TITLE);
                castImageUrl = getIntent().getStringExtra(Constants.IMAGE_URL);
                categoryType = getIntent().getStringExtra(Constants.CATEGORY_TYPE);
                serverType = getIntent().getStringExtra(Constants.SERVER_TYPE);
                mediaUrl = getIntent().getStringExtra(Constants.STREAM_URL);
                playerCurrentPosition = getIntent().getLongExtra(Constants.POSITION, 0);
                resumePosition = playerCurrentPosition;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        // getting user login info for favourite button visibility
        userId = db.getUserData().getUserId();
        //ContinueWatching State
        viewModel = ViewModelProviders.of(this).get(ContinueWatchingViewModel.class);
        /*initialize view model and pass it to adapter*/
        downloadViewModel = ViewModelProviders.of(this).get(DownloadViewModel.class);


        if (PreferenceUtils.isLoggedIn(GoldDetailsActivity.this)) {
            imgAddFav.setVisibility(VISIBLE);
        } else {
            imgAddFav.setVisibility(GONE);
        }

        commentsAdapter = new CommentsAdapter(this, listComment);
        rvComment.setLayoutManager(new LinearLayoutManager(this));
        rvComment.setHasFixedSize(true);
        rvComment.setNestedScrollingEnabled(false);
        rvComment.setAdapter(commentsAdapter);
        getComments();
        imgFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlFullScreenPlayer();
            }
        });
        imgSubtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubtitleDialog(GoldDetailsActivity.this, listSub);
            }
        });

        imgAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MappingTrackSelector.MappedTrackInfo mappedTrackInfo;
                DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
                isShowingTrackSelectionDialog = true;
                TrackSelectionDialog trackSelectionDialog =
                        TrackSelectionDialog.createForTrackSelector(
                                trackSelector,
                                /* onDismissListener= */ dismissedDialog -> {
                                    isShowingTrackSelectionDialog = false;
                                });
                trackSelectionDialog.show(getSupportFragmentManager(), null);
            }
        });

        img_quality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheet();
            }
        });
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PreferenceUtils.isLoggedIn(GoldDetailsActivity.this)) {
                    startActivity(new Intent(GoldDetailsActivity.this, LoginActivity.class));
                    new ToastMsg(GoldDetailsActivity.this).toastIconError(getString(R.string.login_first));
                } else if (etComment.getText().toString().equals("")) {
                    new ToastMsg(GoldDetailsActivity.this).toastIconError(getString(R.string.comment_empty));
                } else {
                    String comment = etComment.getText().toString();
                    addComment(id, PreferenceUtils.getUserId(GoldDetailsActivity.this), comment);
                }
            }
        });

        imgAddFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFav) {
                    removeFromFav();
                } else {
                    addToFav();

                }
            }
        });

        // its for tv series only when description layout visibility gone.
        favIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFav) {
                    removeFromFav();
                } else {
                    addToFav();
                }
            }
        });


        if (!isNetworkAvailable()) {
            new ToastMsg(GoldDetailsActivity.this).toastIconError(getString(R.string.no_internet));
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clear_previous();
                initGetData();
            }
        });

//        loadAd();
//        doTheAutoRefresh();

    }

    public void openBottomSheet() {
        View dialogView = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_subtitle, null);
        BottomSheetDialog dialog = new BottomSheetDialog(Objects.requireNonNull(GoldDetailsActivity.this));
        dialog.setContentView(dialogView);

        View parent = (View) dialogView.getParent();
        parent.setFitsSystemWindows(true);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(parent);
        dialogView.measure(0, 0);
        bottomSheetBehavior.setPeekHeight(dialogView.getMeasuredHeight());

        TextView tv_video_setting = dialog.findViewById(R.id.tv_video_setting);
        LinearLayout ll_video_quality = dialog.findViewById(R.id.ll_video_quality);
        LinearLayout ll_video_subtitle = dialog.findViewById(R.id.ll_video_subtitle);


        ll_video_quality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (myItemMovie != null) {
                    if (myItemMovie.getLstResolution().size() > 0)
                        showTrackDialog(true, myItemMovie.getLstResolution());
                    else
                        showTrackDialog(false, new ArrayList<>());
                } else*/
                if (epiModel != null) {
                    if (epiModel.getVideoQuality().size() > 0)
                        showTrackDialogSeries(true, epiModel.getVideoQuality());
                    else
                        showTrackDialogSeries(false, new ArrayList<>());
                }
//                showAlertDialog();
            }
        });


        ll_video_subtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });


        dialog.show();
    }
/*
    private void showTrackDialog(boolean show, ArrayList<ItemMovie.Resolution> resolution) {
        if (show) {
            androidx.appcompat.app.AlertDialog.Builder builderSingle = new androidx.appcompat.app.AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            builderSingle.setTitle("Video");
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_singlechoice);
            if (resolution.size() > 0) {
                for (int i = 0; i < resolution.size(); i++) {
                    arrayAdapter.add(resolution.get(i).getMovie_resolution());
                }
            }

            builderSingle.setNegativeButton("cancel", (dialog, which) -> dialog.dismiss());

            builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
                checkdItem = which;
                url = resolution.get(which).getMovie_url();
                dialog.dismiss();
                currentVideoPosition = player.getCurrentPosition();
                setPlayerWithUri2(currentVideoPosition);
                btnTryAgain.setVisibility(View.GONE);
                txtPremium.setVisibility(View.GONE);

            });
            builderSingle.setSingleChoiceItems(arrayAdapter, checkdItem, (dialog, item) -> {
                checkdItem = item;
                url = resolution.get(item).getMovie_url();
                dialog.dismiss();
                currentVideoPosition = player.getCurrentPosition();
                setPlayerWithUri2(currentVideoPosition);
                btnTryAgain.setVisibility(View.GONE);
                txtPremium.setVisibility(View.GONE);
            });
            builderSingle.show();
        } else {
            if (!isShowingTrackSelectionDialog
                    && TrackSelectionDialog.willHaveContent(trackSelector)) {
                isShowingTrackSelectionDialog = true;
                TrackSelectionDialog trackSelectionDialog =
                        TrackSelectionDialog.createForTrackSelector(
                                trackSelector, dismissedDialog -> isShowingTrackSelectionDialog = false);
                trackSelectionDialog.show(getChildFragmentManager(), null);
            }
        }

    }

    */

    private void showTrackDialogSeries(boolean show, List<VideoQuality> resolution) {
        if (show) {
            androidx.appcompat.app.AlertDialog.Builder builderSingle = new androidx.appcompat.app.AlertDialog.Builder(Objects.requireNonNull(GoldDetailsActivity.this));
            builderSingle.setTitle("Video");
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(GoldDetailsActivity.this, android.R.layout.select_dialog_singlechoice);
            if (resolution.size() > 0) {
                for (int i = 0; i < resolution.size(); i++) {
                    arrayAdapter.add(resolution.get(i).getQualilty());
                }
            }


            builderSingle.setNegativeButton("cancel", (dialog, which) -> dialog.dismiss());

            builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
                checkdItem = which;
                url = resolution.get(which).getUrl();
                isPlaying = false;
                dialog.dismiss();
                playerCurrentPosition = player.getCurrentPosition();
                mediaDuration = player.getDuration();
                mediaSource = hlsMediaSource(Uri.parse(url), GoldDetailsActivity.this);
//                setPlayerNormalScreen(playerCurrentPosition);
                setPlayerNormalScreen();
                /*btnTryAgain.setVisibility(View.GONE);
                /*btnTryAgain.setVisibility(View.GONE);
                txtPremium.setVisibility(View.GONE);*/

            });
            builderSingle.setSingleChoiceItems(arrayAdapter, checkdItem, (dialog, item) -> {
                /*checkdItem = item;
                url = resolution.get(item).getMovie_url();
                dialog.dismiss();
                currentVideoPosition = player.getCurrentPosition();
                setPlayerWithUri2(currentVideoPosition);
                btnTryAgain.setVisibility(View.GONE);
                txtPremium.setVisibility(View.GONE);*/

                checkdItem = item;
                url = resolution.get(item).getUrl();
                isPlaying = false;
                dialog.dismiss();
                playerCurrentPosition = player.getCurrentPosition();
                mediaDuration = player.getDuration();
                mediaSource = hlsMediaSource(Uri.parse(url), GoldDetailsActivity.this);
//                setPlayerNormalScreen(playerCurrentPosition);
                setPlayerNormalScreen();
            });

            builderSingle.show();
        } else {
            if (!isShowingTrackSelectionDialog
                    && TrackSelectionDialog.willHaveContent(trackSelector)) {
                isShowingTrackSelectionDialog = true;
                TrackSelectionDialog trackSelectionDialog =
                        TrackSelectionDialog.createForTrackSelector(
                                trackSelector, dismissedDialog -> isShowingTrackSelectionDialog = false);
                trackSelectionDialog.show(getSupportFragmentManager(), null);
            }
        }

    }

    private void showAlertDialog() {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(GoldDetailsActivity.this);
        alertDialog.setTitle("Set Subtitle: ");
        String[] items = {"ON", "OFF"};
        alertDialog.setSingleChoiceItems(items, checkdItem2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        showSubtitle(true);
                        checkdItem2 = which;
                        break;
                    case 1:
                        showSubtitle(false);
                        checkdItem2 = which;
                        break;
                }
            }
        });
        androidx.appcompat.app.AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    public void showSubtitle(boolean show) {
        if (simpleExoPlayerView != null && simpleExoPlayerView.getSubtitleView() != null)
            if (show) {
                simpleExoPlayerView.getSubtitleView().setBackgroundColor(Color.TRANSPARENT);
                simpleExoPlayerView.getSubtitleView().setVisibility(View.VISIBLE);

            } else {
                simpleExoPlayerView.getSubtitleView().setVisibility(View.GONE);

            }
    }

    public void initViews() {
        adView = findViewById(R.id.adView);
        llBottom = findViewById(R.id.llbottom);
        tvDes = findViewById(R.id.tv_details);
        //tvCast = findViewById(R.id.tv_cast);
        tvRelease = findViewById(R.id.tv_release_date);
        tvName = findViewById(R.id.text_name);
        tvDirector = findViewById(R.id.tv_director);
        tvGenre = findViewById(R.id.tv_genre);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        imgAddFav = findViewById(R.id.add_fav);
        imgBack = findViewById(R.id.img_back);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        llBottomParent = findViewById(R.id.llbottomparent);
        lPlay = findViewById(R.id.play);
        rvRelated = findViewById(R.id.rv_related);
        tvRelated = findViewById(R.id.tv_related);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        btnComment = findViewById(R.id.btn_comment);
        etComment = findViewById(R.id.et_comment);
        rvComment = findViewById(R.id.recyclerView_comment);
        llcomment = findViewById(R.id.llcomments);
        simpleExoPlayerView = findViewById(R.id.video_view);
        youtubePlayerView = findViewById(R.id.youtubePlayerView);
        exoplayerLayout = findViewById(R.id.exoPlayerLayout);
        subtitleView = findViewById(R.id.subtitle);
        playerLayout = findViewById(R.id.player_layout);
        imgFull = findViewById(R.id.img_full_scr);
        aspectRatioIv = findViewById(R.id.aspect_ratio_iv);
        externalPlayerIv = findViewById(R.id.external_player_iv);
        volumControlIv = findViewById(R.id.volumn_control_iv);
        volumnControlLayout = findViewById(R.id.volumn_layout);
        lnr_buy_gold = findViewById(R.id.lnr_buy_gold);
        rent_bt = findViewById(R.id.rent_bt);
        volumnSeekbar = findViewById(R.id.volumn_seekbar);
        TextView volumnTv = findViewById(R.id.volumn_tv);
        rvServer = findViewById(R.id.rv_server_list);
        rvServerForTV = findViewById(R.id.rv_server_list_for_tv);
        seasonSpinner = findViewById(R.id.season_spinner);
        seasonSpinnerContainer = findViewById(R.id.spinner_container);
        imgSubtitle = findViewById(R.id.img_subtitle);
        imgAudio = findViewById(R.id.img_audio);
        img_quality = findViewById(R.id.img_quality);
        mediaRouteButton = findViewById(R.id.media_route_button);
        chromeCastTv = findViewById(R.id.chrome_cast_tv);
        castControlView = findViewById(R.id.cast_control_view);
        tvLayout = findViewById(R.id.tv_layout);
        sheduleLayout = findViewById(R.id.p_shedule_layout);
        tvTitleTv = findViewById(R.id.tv_title_tv);
        programRv = findViewById(R.id.program_guide_rv);
        tvTopLayout = findViewById(R.id.tv_top_layout);
        tvThumbIv = findViewById(R.id.tv_thumb_iv);
        shareIv = findViewById(R.id.share_iv);
        tvReportIV = findViewById(R.id.tv_report_iv);
        watchStatusTv = findViewById(R.id.watch_status_tv);
        timeTv = findViewById(R.id.time_tv);
        programTv = findViewById(R.id.program_type_tv);
        exoRewind = findViewById(R.id.rewind_layout);
        exoForward = findViewById(R.id.forward_layout);
        seekbarLayout = findViewById(R.id.seekbar_layout);
        liveTv = findViewById(R.id.live_tv);
        castRv = findViewById(R.id.cast_rv);
        proGuideTv = findViewById(R.id.pro_guide_tv);
        watchLiveTv = findViewById(R.id.watch_live_tv);

        contentDetails = findViewById(R.id.content_details);
        subscriptionLayout = findViewById(R.id.subscribe_layout);
        subscribeBt = findViewById(R.id.subscribe_bt);
        backIv = findViewById(R.id.des_back_iv);
        subBackIv = findViewById(R.id.back_iv);
        topbarLayout = findViewById(R.id.topbar);

        descriptionLayout = findViewById(R.id.description_layout);
//        descriptionContatainer = findViewById(R.id.lyt_parent);
        rel_back = findViewById(R.id.rel_back);
        watchNowBt = findViewById(R.id.watch_now_bt);
        downloadBt = findViewById(R.id.download_bt);
        trailerBt = findViewById(R.id.trailer_bt);
        downloadAndTrailerBtContainer = findViewById(R.id.downloadBt_container);
        posterIv = findViewById(R.id.poster_iv);
        thumbIv = findViewById(R.id.image_thumb);
        //descriptionBackIv = findViewById(R.id.back_iv);
        dGenryTv = findViewById(R.id.genre_tv);
        serverIv = findViewById(R.id.img_server);

        seriestLayout = findViewById(R.id.series_layout);
        favIv = findViewById(R.id.add_fav2);
        sereisTitleTv = findViewById(R.id.seriest_title_tv);
        shareIv2 = findViewById(R.id.share_iv2);
        reportIv = findViewById(R.id.report_iv);
        //season download
        seasonDownloadLayout = findViewById(R.id.seasonDownloadLayout);
        seasonDownloadSpinner = findViewById(R.id.seasonSpinnerField);
        seasonDownloadRecyclerView = findViewById(R.id.seasonDownloadRecyclerView);


    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void controlFullScreenPlayer() {
        if (isFullScr) {
            fullScreenByClick = false;
            isFullScr = false;
            swipeRefreshLayout.setVisibility(VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            if (isVideo) {
                lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, playerHeight));
            } else {
                lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, playerHeight));
            }

            // reset the orientation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        } else {

            fullScreenByClick = true;
            isFullScr = true;
            swipeRefreshLayout.setVisibility(GONE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            // reset the orientation
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!AppConfig.ENABLE_EXTERNAL_PLAYER) {
            externalPlayerIv.setVisibility(GONE);
        }
        try {
            if (isFromContinueWatching) {
                releasePlayer();
                resetCastPlayer();
                setPlayerFullScreen();
                progressBar.setVisibility(VISIBLE);
                swipeRefreshLayout.setVisibility(GONE);
                lPlay.setVisibility(VISIBLE);
                imgFull.setVisibility(GONE);
                initVideoPlayer(mediaUrl, GoldDetailsActivity.this, serverType);

            } else {
                initGetData();
            }

        } catch (NullPointerException e) {
            initGetData();
        }

        if (mAudioManager != null) {
            volumnSeekbar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            int currentVolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            volumnSeekbar.setProgress(currentVolumn);
        }

        volumnSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    //volumnTv.setText(i+"");
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, AudioManager.ADJUST_SAME);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        volumControlIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volumnControlLayout.setVisibility(VISIBLE);
            }
        });

        aspectRatioIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aspectClickCount == 1) {
                    simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                    aspectClickCount = 2;
                } else if (aspectClickCount == 2) {
                    simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                    aspectClickCount = 3;
                } else if (aspectClickCount == 3) {
                    simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                    aspectClickCount = 1;
                }

            }
        });

        externalPlayerIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaUrl != null) {
                    if (!tv) {
                        // set player normal/ portrait screen if not tv
                        descriptionLayout.setVisibility(VISIBLE);
                        setPlayerNormalScreen();
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(mediaUrl), "video/*");
                    startActivity(Intent.createChooser(intent, "Complete action using"));
                }

            }
        });

        watchNowBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!listServer.isEmpty()) {
                    if (listServer.size() == 1) {
                        releasePlayer();
                        //resetCastPlayer();
                        preparePlayer(listServer.get(0));
                        descriptionLayout.setVisibility(GONE);
                        lPlay.setVisibility(VISIBLE);
                    } else {
                        openServerDialog();
                    }
                } else {
                    Toast.makeText(GoldDetailsActivity.this, R.string.no_video_found, Toast.LENGTH_SHORT).show();
                }
            }
        });

        downloadBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!listInternalDownload.isEmpty() || !listExternalDownload.isEmpty()) {
                    if (AppConfig.ENABLE_DOWNLOAD_TO_ALL) {
                        openDownloadServerDialog();
                    } else {
                        if (PreferenceUtils.isLoggedIn(GoldDetailsActivity.this) && PreferenceUtils.isActivePlan(GoldDetailsActivity.this)) {
                            openDownloadServerDialog();
                        } else {
                            Toast.makeText(GoldDetailsActivity.this, R.string.download_not_permitted, Toast.LENGTH_SHORT).show();
                            Log.e("Download", "not permitted");
                        }
                    }
                } else {
                    Toast.makeText(GoldDetailsActivity.this, R.string.no_download_server_found, Toast.LENGTH_SHORT).show();
                }
            }
        });

        trailerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trailerUrl != null && !trailerUrl.equalsIgnoreCase("")) {
                    serverType = "";
                    mediaUrl = trailerUrl;
                    CommonModels commonModels = new CommonModels();
                    commonModels.setStremURL(trailerUrl);
                    commonModels.setServerType("");
                    descriptionLayout.setVisibility(GONE);
                    lPlay.setVisibility(VISIBLE);
                    releasePlayer();
                    preparePlayer(commonModels);
                }

            }
        });

        watchLiveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideExoControlForTv();
                initMoviePlayer(mediaUrl, serverType, GoldDetailsActivity.this);

                watchStatusTv.setText(getString(R.string.watching_on) + " " + getString(R.string.app_name));
                watchLiveTv.setVisibility(GONE);

                timeTv.setText(currentProgramTime);
                programTv.setText(currentProgramTitle);
            }
        });

        shareIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.share(GoldDetailsActivity.this, title);
            }
        });

        tvReportIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportMovie();
            }
        });

        shareIv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title == null) {
                    new ToastMsg(GoldDetailsActivity.this).toastIconError("Title should not be empty.");
                    return;
                }
                Tools.share(GoldDetailsActivity.this, title);
            }
        });

        //report icon
        reportIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportMovie();
            }
        });

        castPlayer.addListener(new Player.Listener() {


            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playWhenReady && playbackState == CastPlayer.STATE_READY) {
                    progressBar.setVisibility(View.GONE);

                    Log.e("STATE PLAYER:::", String.valueOf(isPlaying));

                } else if (playbackState == CastPlayer.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("STATE PLAYER:::", String.valueOf(isPlaying));
                } else if (playbackState == CastPlayer.STATE_BUFFERING) {
                    progressBar.setVisibility(VISIBLE);

                    Log.e("STATE PLAYER:::", String.valueOf(isPlaying));
                } else {
                    Log.e("STATE PLAYER:::", String.valueOf(isPlaying));
                }

            }
        });

        serverIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openServerDialog();
            }
        });

        simpleExoPlayerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if (visibility == 0) {
                    imgBack.setVisibility(VISIBLE);

                    if (categoryType.equals("tv") || categoryType.equals("tvseries")) {
                        imgFull.setVisibility(VISIBLE);
                    } else {
                        imgFull.setVisibility(GONE);
                    }

                    // invisible download icon for live tv
                    if (download_check.equals("1")) {
                        if (!tv) {
                            if (activeMovie) {
                                serverIv.setVisibility(VISIBLE);
                            }
                        } else {
                        }
                    } else {
                    }

                    if (listSub.size() != 0) {
                        imgSubtitle.setVisibility(VISIBLE);
                    }
                    //imgSubtitle.setVisibility(VISIBLE);
                } else {
                    imgBack.setVisibility(GONE);
                    imgFull.setVisibility(GONE);
                    imgSubtitle.setVisibility(GONE);
                    volumnControlLayout.setVisibility(GONE);
                }
            }
        });

        subscribeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userId == null) {
                    new ToastMsg(GoldDetailsActivity.this).toastIconError(getResources().getString(R.string.subscribe_error));
                    startActivity(new Intent(GoldDetailsActivity.this, LoginActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(GoldDetailsActivity.this, PurchasePlanActivity.class));
                }

            }
        });
        rent_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if (userId == null) {
                    new ToastMsg(GoldDetailsActivity.this).toastIconError(getResources().getString(R.string.subscribe_error));
                    startActivity(new Intent(GoldDetailsActivity.this, LoginActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(GoldDetailsActivity.this, PurchasePlanActivity.class));
                }*/

                Intent intent = new Intent(GoldDetailsActivity.this, GoldRazorPayActivity.class);
                intent.putExtra("videoId", id);
                intent.putExtra("videoAmount", amt);
                startActivity(intent);

            }
        });
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        subBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    String videoReport = "", audioReport = "", subtitleReport = "", messageReport = "";

    private void reportMovie() {
        //open movie report dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.movie_report_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        TextView movieTitle = view.findViewById(R.id.movie_title);
        RadioGroup videoGroup = view.findViewById(R.id.radio_group_video);
        RadioGroup audioGroup = view.findViewById(R.id.radio_group_audio);
        RadioGroup subtitleGroup = view.findViewById(R.id.radio_group_subtitle);
        //EditText message = view.findViewById(R.id.report_message_et);
        TextInputEditText message = view.findViewById(R.id.report_message_et);
        Button submitButton = view.findViewById(R.id.submit_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        LinearLayout subtitleLayout = view.findViewById(R.id.subtitleLayout);
        if (this.categoryType.equalsIgnoreCase("tv")) {
            subtitleLayout.setVisibility(GONE);
        }

        movieTitle.setText("Report for: " + title);
        if (!isDark) {
            movieTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
        }


        videoGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find the radiobutton by returned id
                RadioButton radioButton = (RadioButton) view.findViewById(checkedId);
                videoReport = radioButton.getText().toString();
            }
        });

        audioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find the radiobutton by returned id
                RadioButton radioButton = (RadioButton) view.findViewById(checkedId);
                audioReport = radioButton.getText().toString();
            }
        });

        subtitleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find the radiobutton by returned id
                RadioButton radioButton = (RadioButton) view.findViewById(checkedId);
                subtitleReport = radioButton.getText().toString();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageReport = message.getText().toString().trim();
                Retrofit retrofit = RetrofitClient.getRetrofitInstance();
                ReportApi api = retrofit.create(ReportApi.class);
                Call<ResponseBody> call = api.submitReport(AppConfig.API_KEY, categoryType, id, videoReport, audioReport, subtitleReport, messageReport);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            new ToastMsg(getApplicationContext()).toastIconSuccess("Report submitted");
                        } else {
                            new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.something_went_text));
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        new ToastMsg(getApplicationContext()).toastIconError(getResources().getString(R.string.something_went_text));
                        dialog.dismiss();
                    }
                });
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void updateContinueWatchingData() {
        if (!categoryType.equals("tv")) {
            try {
                long position = playerCurrentPosition;
                long duration = mediaDuration;
                float progress = 0;
                if (position != 0 && duration != 0) {
                    progress = calculateProgress(position, duration);
                }

                //---update into continueWatching------
                ContinueWatchingModel model = new ContinueWatchingModel(id, title,
                        castImageUrl, progress, position, mediaUrl,
                        categoryType, serverType);
                viewModel.update(model);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void setPlayerNormalScreen() {
        swipeRefreshLayout.setVisibility(VISIBLE);
        lPlay.setVisibility(GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //close embed link playing
        if (webView.getVisibility() == VISIBLE) {
            if (webView != null) {
                Intent intent = new Intent(GoldDetailsActivity.this, GoldDetailsActivity.class);
                intent.putExtra("vType", categoryType);
                intent.putExtra("id", id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        } else {
            lPlay.setVisibility(VISIBLE);
        }

        lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, playerHeight));
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void setPlayerFullScreen() {
        swipeRefreshLayout.setVisibility(GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (isVideo) {
            lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        } else {
            lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        }
    }

    private void openDownloadServerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_download_server_dialog, null);
        internalDownloadLayout = view.findViewById(R.id.internal_download_layout);
        externalDownloadLayout = view.findViewById(R.id.external_download_layout);
        if (listExternalDownload.isEmpty()) {
            externalDownloadLayout.setVisibility(GONE);
        }
        if (listInternalDownload.isEmpty()) {
            internalDownloadLayout.setVisibility(GONE);
        }
        internalServerRv = view.findViewById(R.id.internal_download_rv);
        externalServerRv = view.findViewById(R.id.external_download_rv);
        DownloadAdapter internalDownloadAdapter = new DownloadAdapter(this, listInternalDownload, true, downloadViewModel);
        internalServerRv.setLayoutManager(new LinearLayoutManager(this));
        internalServerRv.setHasFixedSize(true);
        internalServerRv.setAdapter(internalDownloadAdapter);

        DownloadAdapter externalDownloadAdapter = new DownloadAdapter(this, listExternalDownload, true, downloadViewModel);
        externalServerRv.setLayoutManager(new LinearLayoutManager(this));
        externalServerRv.setHasFixedSize(true);
        externalServerRv.setAdapter(externalDownloadAdapter);

        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openServerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_server_dialog, null);
        serverRv = view.findViewById(R.id.serverRv);
        serverAdapter = new ServerAdapter(this, listServer, "movie");
        serverRv.setLayoutManager(new LinearLayoutManager(this));
        serverRv.setHasFixedSize(true);
        serverRv.setAdapter(serverAdapter);

        ImageView closeIv = view.findViewById(R.id.close_iv);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final ServerAdapter.OriginalViewHolder[] viewHolder = {null};
        serverAdapter.setOnItemClickListener(new ServerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, CommonModels obj, int position, ServerAdapter.OriginalViewHolder holder) {
                releasePlayer();
                //resetCastPlayer();
                preparePlayer(obj);

                //serverAdapter.chanColor(viewHolder[0], position);
                //holder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
                //viewHolder[0] = holder;
            }

            @Override
            public void getFirstUrl(String url) {
                mediaUrl = url;
            }

            @Override
            public void hideDescriptionLayout() {
                descriptionLayout.setVisibility(GONE);
                lPlay.setVisibility(VISIBLE);
                dialog.dismiss();

            }
        });

    }

    public void preparePlayer(CommonModels obj) {
        activeMovie = true;
        setPlayerFullScreen();
        mediaUrl = obj.getStremURL();
        if (!castSession) {
            initMoviePlayer(obj.getStremURL(), obj.getServerType(), GoldDetailsActivity.this);

            listSub.clear();
            if (obj.getListSub() != null) {
                listSub.addAll(obj.getListSub());
            }

            if (listSub.size() != 0) {
                imgSubtitle.setVisibility(VISIBLE);
            } else {
                imgSubtitle.setVisibility(GONE);
            }

        } else {
            if (obj.getServerType().toLowerCase().equals("embed")) {

                castSession = false;
                castPlayer.setSessionAvailabilityListener(null);
                castPlayer.release();

                // invisible control ui of exoplayer
                player.setPlayWhenReady(true);
                simpleExoPlayerView.setUseController(true);

                // invisible control ui of casting
                castControlView.setVisibility(GONE);
                chromeCastTv.setVisibility(GONE);


            } else {
                showQueuePopup(GoldDetailsActivity.this, null, getMediaInfo());
            }
        }
    }

    void clear_previous() {
        //strCast = "";
        strDirector = "";
        strGenre = "";
        //listDownload.clear();
        listInternalDownload.clear();
        listExternalDownload.clear();
        programs.clear();
        castCrews.clear();
    }

    private void prepareSubtitleList(Context context, List<SubtitleModel> list) {
    }

    public void showSubtitleDialog(Context context, List<SubtitleModel> list) {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_subtitle, viewGroup, false);
        ImageView cancel = dialogView.findViewById(R.id.cancel);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
        SubtitleAdapter adapter = new SubtitleAdapter(context, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        alertDialog = builder.create();
        alertDialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

    }

    @Override
    public void onCastSessionAvailable() {
        castSession = true;
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, title);
        //movieMetadata.putString(MediaMetadata.KEY_ALBUM_ARTIST, "Test Artist");
        movieMetadata.addImage(new WebImage(Uri.parse(castImageUrl)));
        MediaInfo mediaInfo = new MediaInfo.Builder(mediaUrl)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(MimeTypes.VIDEO_UNKNOWN)
                .setMetadata(movieMetadata).build();

        //array of media sources
        final MediaQueueItem[] mediaItems = {new MediaQueueItem.Builder(mediaInfo).build()};

        castPlayer.loadItems(mediaItems, 0, 3000, Player.REPEAT_MODE_OFF);

        // visible control ui of casting
        castControlView.setVisibility(VISIBLE);
        castControlView.setPlayer(castPlayer);
        castControlView.addVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if (visibility == GONE) {
                    castControlView.setVisibility(VISIBLE);
                    chromeCastTv.setVisibility(VISIBLE);
                }
            }
        });

        // invisible control ui of exoplayer
        player.setPlayWhenReady(false);
        simpleExoPlayerView.setUseController(false);
    }

    @Override
    public void onCastSessionUnavailable() {
        // make cast session false
        castSession = false;
        // invisible control ui of exoplayer
        player.setPlayWhenReady(true);
        simpleExoPlayerView.setUseController(true);

        // invisible control ui of casting
        castControlView.setVisibility(GONE);
        chromeCastTv.setVisibility(GONE);
    }

    public void initServerTypeForTv(String serverType) {
        this.serverType = serverType;
    }

    @Override
    public void onProgramClick(Program program) {
        if (program.getProgramStatus().equals("onaired") && program.getVideoUrl() != null) {
            showExoControlForTv();
            initMoviePlayer(program.getVideoUrl(), "tv", this);
            timeTv.setText(program.getTime());
            programTv.setText(program.getTitle());
        } else {
            new ToastMsg(GoldDetailsActivity.this).toastIconError("Not Yet");
        }
    }

    //this method will be called when related tv channel is clicked
    @Override
    public void onRelatedTvClicked(CommonModels obj) {
        categoryType = obj.getVideoType();
        id = obj.getId();
        initGetData();
    }

    // this will call when any episode is clicked
    //if it is embed player will go full screen
    @Override
    public void onEpisodeItemClickTvSeries(String type, View view, EpiModel obj, int position, GoldEpisodeAdapter.OriginalViewHolder holder) {
        if (type.equalsIgnoreCase("embed")) {
            CommonModels model = new CommonModels();
            model.setStremURL(obj.getStreamURL());
            model.setServerType(obj.getServerType());
            model.setListSub(null);
            releasePlayer();
            // resetCastPlayer();
            preparePlayer(model);
        } else {
            if (obj != null) {
                if (obj.getSubtitleList().size() != 0) {
                    listSub.clear();
                    listSub.addAll(obj.getSubtitleList());
                    imgSubtitle.setVisibility(VISIBLE);
                } else {
                    listSub.clear();
                    imgSubtitle.setVisibility(GONE);
                }
                epiModel = obj;
                initMoviePlayer(obj.getStreamURL(), obj.getServerType(), GoldDetailsActivity.this);
            }
        }
    }

    private class SubtitleAdapter extends RecyclerView.Adapter<SubtitleAdapter.OriginalViewHolder> {
        private List<SubtitleModel> items = new ArrayList<>();
        private Context ctx;

        public SubtitleAdapter(Context context, List<SubtitleModel> items) {
            this.items = items;
            ctx = context;
        }

        @Override
        public OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            OriginalViewHolder vh;
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_subtitle, parent, false);
            vh = new OriginalViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(OriginalViewHolder holder, final int position) {
            final SubtitleModel obj = items.get(position);
            holder.name.setText(obj.getLanguage());

            holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectedSubtitle(mediaSource, obj.getUrl(), ctx);
                    alertDialog.cancel();
                }
            });

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class OriginalViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            private View lyt_parent;

            public OriginalViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.name);
                lyt_parent = v.findViewById(R.id.lyt_parent);
            }
        }

    }

//    private void loadAd() {
//        AdsConfig adsConfig = db.getConfigurationData().getAdsConfig();
//        if (adsConfig.getAdsEnable().equals("1")) {
//
//            if (adsConfig.getMobileAdsNetwork().equalsIgnoreCase(Constants.ADMOB)) {
//                BannerAds.ShowAdmobBannerAds(this, adView);
//                PopUpAds.ShowAdmobInterstitialAds(this);
//
//            } else if (adsConfig.getMobileAdsNetwork().equalsIgnoreCase(Constants.START_APP)) {
//                //BannerAds.showStartAppBanner(DetailsActivity.this, adView);
//                PopUpAds.showStartappInterstitialAds(DetailsActivity.this);
//
//            } else if (adsConfig.getMobileAdsNetwork().equalsIgnoreCase(Constants.NETWORK_AUDIENCE)) {
//                BannerAds.showFANBanner(this, adView);
//                PopUpAds.showFANInterstitialAds(DetailsActivity.this);
//            }
//
//        }
//
//    }

    private void initGetData() {
        //check vpn connection
        helperUtils = new HelperUtils(GoldDetailsActivity.this);
        vpnStatus = helperUtils.isVpnConnectionAvailable();
        if (vpnStatus) {
            helperUtils.showWarningDialog(GoldDetailsActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
        } else {
            if (!categoryType.equals("tv")) {

                //----related rv----------
                relatedAdapter = new HomePageAdapter(this, listRelated);
                rvRelated.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                        false));
                rvRelated.setHasFixedSize(true);
                rvRelated.setAdapter(relatedAdapter);

                if (categoryType.equals("tvseries")) {

                    seasonSpinnerContainer.setVisibility(VISIBLE);
                    rvServer.setVisibility(VISIBLE);
                    serverIv.setVisibility(GONE);

                    rvRelated.removeAllViews();
                    listRelated.clear();
                    rvServer.removeAllViews();
                    listServer.clear();
                    listServer.clear();

                    downloadBt.setVisibility(GONE);
                    watchNowBt.setVisibility(GONE);
                    trailerBt.setVisibility(GONE);

                    // cast & crew adapter
                    castCrewAdapter = new CastCrewAdapter(this, castCrews);
                    castRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                    castRv.setHasFixedSize(true);
                    castRv.setAdapter(castCrewAdapter);

                    getSeriesData(categoryType, id);

                    if (listSub.size() == 0) {
                        imgSubtitle.setVisibility(GONE);
                    }

                } else {
                    imgFull.setVisibility(GONE);
                    listServer.clear();
                    rvRelated.removeAllViews();
                    listRelated.clear();
                    if (listSub.size() == 0) {
                        imgSubtitle.setVisibility(GONE);
                    }

                    // cast & crew adapter
                    castCrewAdapter = new CastCrewAdapter(this, castCrews);
                    castRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                    castRv.setHasFixedSize(true);
                    castRv.setAdapter(castCrewAdapter);

                    getMovieData(categoryType, id);

                    final ServerAdapter.OriginalViewHolder[] viewHolder = {null};
                }

                if (PreferenceUtils.isLoggedIn(GoldDetailsActivity.this)) {
                    getFavStatus();
                }

            } else {
                tv = true;
                imgSubtitle.setVisibility(GONE);
                llcomment.setVisibility(GONE);
                serverIv.setVisibility(GONE);

                rvServer.setVisibility(VISIBLE);
                descriptionLayout.setVisibility(GONE);
                lPlay.setVisibility(VISIBLE);

                // hide exo player some control
                hideExoControlForTv();

                tvLayout.setVisibility(VISIBLE);

                // hide program guide if its disable from api
                if (!PreferenceUtils.isProgramGuideEnabled(GoldDetailsActivity.this)) {
                    proGuideTv.setVisibility(GONE);
                    programRv.setVisibility(GONE);

                }

                watchStatusTv.setText(getString(R.string.watching_on) + " " + getString(R.string.app_name));

                tvRelated.setText(getString(R.string.all_tv_channel));

                rvServer.removeAllViews();
                listServer.clear();
                rvRelated.removeAllViews();
                listRelated.clear();

                programAdapter = new ProgramAdapter(programs, this);
                programRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                programRv.setHasFixedSize(true);
                programRv.setAdapter(programAdapter);
                programAdapter.setOnProgramClickListener(this);

                //----related rv----------
                //relatedTvAdapter = new LiveTvHomeAdapter(this, listRelated, TAG);
                relatedTvAdapter = new RelatedTvAdapter(listRelated, GoldDetailsActivity.this);
                rvRelated.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                rvRelated.setHasFixedSize(true);
                rvRelated.setAdapter(relatedTvAdapter);
                relatedTvAdapter.setListener(GoldDetailsActivity.this);

                imgAddFav.setVisibility(GONE);

                serverAdapter = new ServerAdapter(this, listServer, "tv");
                rvServerForTV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                rvServerForTV.setHasFixedSize(true);
                rvServerForTV.setAdapter(serverAdapter);
                Log.e(TAG, "initGetData: TV");
                getTvData(categoryType, id);
                llBottom.setVisibility(GONE);

                final ServerAdapter.OriginalViewHolder[] viewHolder = {null};
                serverAdapter.setOnItemClickListener(new ServerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, CommonModels obj, int position, ServerAdapter.OriginalViewHolder holder) {
                        mediaUrl = obj.getStremURL();

                        if (!castSession) {
                            initMoviePlayer(obj.getStremURL(), obj.getServerType(), GoldDetailsActivity.this);

                        } else {

                            if (obj.getServerType().toLowerCase().equals("embed")) {

                                castSession = false;
                                castPlayer.setSessionAvailabilityListener(null);
                                castPlayer.release();

                                // invisible control ui of exoplayer
                                player.setPlayWhenReady(true);
                                simpleExoPlayerView.setUseController(true);

                                // invisible control ui of casting
                                castControlView.setVisibility(GONE);
                                chromeCastTv.setVisibility(GONE);
                            } else {
                                showQueuePopup(GoldDetailsActivity.this, null, getMediaInfo());
                            }
                        }

                        serverAdapter.chanColor(viewHolder[0], position);
                        holder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
                        viewHolder[0] = holder;
                    }

                    @Override
                    public void getFirstUrl(String url) {
                        mediaUrl = url;
                    }

                    @Override
                    public void hideDescriptionLayout() {

                    }
                });


            }
        }
    }

    private void openWebActivity(String s, Context context, String videoType) {

        if (isPlaying) {
            player.release();
        }
        progressBar.setVisibility(GONE);
        playerLayout.setVisibility(GONE);

        webView.loadUrl(s);
        webView.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setVisibility(VISIBLE);

    }

    public void initMoviePlayer(String url, String type, Context context) {
        serverType = type;
        urlType = type;
        if (type.equals("embed") || type.equals("vimeo") || type.equals("gdrive") /*|| type.equals("youtube-live")*/) {
            isVideo = false;
            openWebActivity(url, context, type);
        } else {
            isVideo = true;
            initVideoPlayer(url, context, type);
        }
    }

    public void initVideoPlayer(String url, Context context, String type) {
        progressBar.setVisibility(VISIBLE);
        Log.e(TAG, "initVideoPlayer: type: " + type);
        if (!categoryType.equals("tv")) {
            ContinueWatchingModel model = new ContinueWatchingModel(id, title, castImageUrl, 0, 0, url, categoryType, type);
            viewModel.insert(model);
        }

        if (player != null) {
            player.stop();
            player.release();
        }

        webView.setVisibility(GONE);
        playerLayout.setVisibility(VISIBLE);
        exoplayerLayout.setVisibility(VISIBLE);
        youtubePlayerView.setVisibility(GONE);
        swipeRefreshLayout.setVisibility(VISIBLE);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        trackSelector = new DefaultTrackSelector(GoldDetailsActivity.this);
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this);
        renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
        player = new SimpleExoPlayer.Builder(context, renderersFactory)
                .setTrackSelector(trackSelector)
                .build();

        Uri uri = Uri.parse(url);
        switch (type) {
            case "hls":
                mediaSource = hlsMediaSource(uri, context);
                break;
            case Constants.YOUTUBE:
                /**tag 18 : 360p, tag: 22 : 720p, 133: live*/
                extractYoutubeUrl(url, context, 18);
                // initYoutubePlayer(url);
                break;
            case Constants.YOUTUBE_LIVE:
                /**play Youtube-live video**/
                initYoutubePlayer(url);
                break;
            case "rtmp":
                mediaSource = rtmpMediaSource(uri);
                break;
            default:
//                mediaSource = mediaSource(uri, context);
                mediaSource = hlsMediaSource(uri, context);
                break;
        }

        if (!type.equalsIgnoreCase(Constants.YOUTUBE) &&
                !type.equalsIgnoreCase(Constants.YOUTUBE_LIVE)) {
            try {
                player.prepare(mediaSource, true, false);
                simpleExoPlayerView.setPlayer(player);
                player.setPlayWhenReady(true);

                if (resumePosition > 0) {
                    player.seekTo(resumePosition);
                    player.setPlayWhenReady(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //add listener to player
        if (player != null) {
            player.addListener(playerListener);
        }
    }

    private void initYoutubePlayer(String url) {
        Log.e(TAG, "youtube_live: " + url);
        progressBar.setVisibility(GONE);

        playerLayout.setVisibility(GONE);
        exoplayerLayout.setVisibility(GONE);
        youtubePlayerView.setVisibility(VISIBLE);
        swipeRefreshLayout.setVisibility(VISIBLE);
        releasePlayer();
        String[] separated = url.split("=");

        YouTubePlayerFragment fragment = YouTubePlayerFragment.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.youtubePlayerView, fragment);
        transaction.commit();
        fragment.initialize("AIzaSyBURBj1a4kTjmOJzaZ3naLOJ7x66vEb_KI"
                , new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                        if (!wasRestored) {
                            youTubePlayer.cueVideo(separated[1]);
                            youTubePlayer.play();
                        }
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
    }

    private Handler handler = new Handler();

    private Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onTimelineChanged(Timeline timeline, int reason) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playWhenReady && playbackState == Player.STATE_READY) {
                isPlaying = true;
                progressBar.setVisibility(View.GONE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!categoryType.equals("tv") && player != null) {
                            playerCurrentPosition = player.getCurrentPosition();
                            mediaDuration = player.getDuration();
                            updateContinueWatchingData();
                        }
                        handler.postDelayed(this, 1000);
                    }
                }, 1000);
            } else if (playbackState == Player.STATE_READY) {
                progressBar.setVisibility(View.GONE);
                isPlaying = false;
            } else if (playbackState == Player.STATE_BUFFERING) {
                isPlaying = false;
                progressBar.setVisibility(VISIBLE);
            } else if (playbackState == Player.STATE_ENDED) {
                //---delete into continueWatching------
                ContinueWatchingModel model = new ContinueWatchingModel(id, title,
                        castImageUrl, 0, 0, mediaUrl,
                        categoryType, serverType);
                viewModel.delete(model);

            } else {
                // player paused in any state
                isPlaying = false;
                playerCurrentPosition = player.getCurrentPosition();
                mediaDuration = player.getDuration();
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            isPlaying = false;
            progressBar.setVisibility(VISIBLE);
        }
    };

    private long calculateProgress(long position, long duration) {
        return (position * 100 / duration);
    }


    @SuppressLint("StaticFieldLeak")
    private void extractYoutubeUrl(String url, final Context context, final int tag) {
        Log.e("Trailer", "onExtractUrl");
        new YouTubeExtractor(context) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles != null) {
                    int itag = tag;

                    try {
                        Log.e("Trailer", "onPlayUrl");
                        String extractedUrl = ytFiles.get(itag).getUrl();
                        //youtubeUrl = extractedUrl;
                        MediaSource source = mediaSource(Uri.parse(extractedUrl), context);
                        player.prepare(source, true, false);
                        simpleExoPlayerView.setPlayer(player);
                        player.setPlayWhenReady(true);
                        if (resumePosition > 0) {
                            player.seekTo(resumePosition);
                            player.setPlayWhenReady(true);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.extract(url, true, true);
    }

    private MediaSource rtmpMediaSource(Uri uri) {
        MediaSource videoSource = null;
        RtmpDataSourceFactory dataSourceFactory = new RtmpDataSourceFactory();
        videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);

        return videoSource;
    }

    private MediaSource hlsMediaSource(Uri uri, Context context) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "oxoo"), bandwidthMeter);

        MediaSource videoSource = new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
        return videoSource;
    }

    private MediaSource mediaSource(Uri uri, Context context) {
        return new DefaultMediaSourceFactory(
                new DefaultHttpDataSourceFactory("exoplayer")).
                createMediaSource(uri);
    }

    public void setSelectedSubtitle(MediaSource mediaSource, String subtitle, Context context) {
        MergingMediaSource mergedSource;
        if (subtitle != null) {
            Uri subtitleUri = Uri.parse(subtitle);

            Format subtitleFormat = Format.createTextSampleFormat(
                    null, // An identifier for the track. May be null.
                    MimeTypes.TEXT_VTT, // The mime type. Must be set correctly.
                    Format.NO_VALUE, // Selection flags for the track.
                    "en"); // The subtitle language. May be null.

//            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context,
//                    Util.getUserAgent(context, CLASS_NAME), new DefaultBandwidthMeter());


//            MediaSource subtitleSource = new SingleSampleMediaSource
//                    .Factory(dataSourceFactory)
//                    .createMediaSource(subtitleUri, subtitleFormat, C.TIME_UNSET);


//            mergedSource = new MergingMediaSource(mediaSource, subtitleSource);
//            player.prepare(mergedSource, false, false);
//            player.setPlayWhenReady(true);
            //resumePlayer();

        } else {
            Toast.makeText(context, "there is no subtitle", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToFav() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        FavouriteApi api = retrofit.create(FavouriteApi.class);
        Call<FavoriteModel> call = api.addToFavorite(AppConfig.API_KEY, userId, id);
        call.enqueue(new Callback<FavoriteModel>() {
            @Override
            public void onResponse(Call<FavoriteModel> call, Response<FavoriteModel> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        new ToastMsg(GoldDetailsActivity.this).toastIconSuccess(response.body().getMessage());
                        isFav = true;
                        //imgAddFav.setBackgroundResource(R.drawable.ic_favorite_white);
                        imgAddFav.setImageResource(R.drawable.ic_favorite_white);
                    } else {
                        new ToastMsg(GoldDetailsActivity.this).toastIconError(response.body().getMessage());
                    }
                } else {
                    new ToastMsg(GoldDetailsActivity.this).toastIconError(getString(R.string.error_toast));
                }
            }

            @Override
            public void onFailure(Call<FavoriteModel> call, Throwable t) {
                new ToastMsg(GoldDetailsActivity.this).toastIconError(getString(R.string.error_toast));

            }
        });

    }

    private void paidControl(String isPaid) {
        if (isPaid.equals("1")) {
            if (PreferenceUtils.isLoggedIn(GoldDetailsActivity.this)) {
                if (is_gold.equals("1")) {
                    if (PreferenceUtils.isValid(GoldDetailsActivity.this)) {
                        seasonSpinnerContainer.setVisibility(VISIBLE);
//                        contentDetails.setVisibility(VISIBLE);
                        lnr_buy_gold.setVisibility(GONE);
                    } else {
                        PreferenceUtils.updateSubscriptionStatus(GoldDetailsActivity.this);
                    }
                } else {
                    seasonSpinnerContainer.setVisibility(GONE);
//                    contentDetails.setVisibility(GONE);
                    lnr_buy_gold.setVisibility(VISIBLE);
                    releasePlayer();
                }
            } else {
                startActivity(new Intent(GoldDetailsActivity.this, LoginActivity.class));
                finish();
            }
        } else {
            //free content
            seasonSpinnerContainer.setVisibility(VISIBLE);
//            contentDetails.setVisibility(VISIBLE);
            lnr_buy_gold.setVisibility(GONE);
        }
    }

    private void getActiveStatus(String userId) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);

        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(AppConfig.API_KEY, userId);
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(Call<ActiveStatus> call, Response<ActiveStatus> response) {
                ActiveStatus activeStatus = response.body();
                if (!activeStatus.getStatus().equals("active")) {
                    contentDetails.setVisibility(GONE);
                    subscriptionLayout.setVisibility(VISIBLE);
                } else {
                    contentDetails.setVisibility(VISIBLE);
                    subscriptionLayout.setVisibility(GONE);
                }

                PreferenceUtils.updateSubscriptionStatus(GoldDetailsActivity.this);
            }

            @Override
            public void onFailure(Call<ActiveStatus> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void getTvData(final String vtype, final String vId) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SingleDetailsTVApi api = retrofit.create(SingleDetailsTVApi.class);
        Call<SingleDetailsTV> call = api.getSingleDetails(AppConfig.API_KEY, vtype, vId);
        call.enqueue(new Callback<SingleDetailsTV>() {
            @Override
            public void onResponse(Call<SingleDetailsTV> call, Response<SingleDetailsTV> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        swipeRefreshLayout.setRefreshing(false);
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(GONE);
                        if (response.body().getIsPaid().equalsIgnoreCase("1")) {
                            paidControl(response.body().getIsPaid());
                        }

                        SingleDetailsTV detailsModel = response.body();

                        title = detailsModel.getTvName();
                        tvName.setText(title);
                        tvName.setVisibility(GONE);
                        tvTitleTv.setText(title);

                        tvDes.setText(detailsModel.getDescription());
                        V_URL = detailsModel.getStreamUrl();
                        castImageUrl = detailsModel.getThumbnailUrl();

                        Picasso.get().load(detailsModel.getThumbnailUrl()).placeholder(R.drawable.album_art_placeholder)
                                .into(tvThumbIv);

                        CommonModels model = new CommonModels();
                        model.setTitle("HD");
                        model.setStremURL(V_URL);
                        model.setServerType(detailsModel.getStreamFrom());
                        listServer.add(model);

                        initMoviePlayer(detailsModel.getStreamUrl(), detailsModel.getStreamFrom(), GoldDetailsActivity.this);

                        currentProgramTime = detailsModel.getCurrentProgramTime();
                        currentProgramTitle = detailsModel.getCurrentProgramTitle();

                        timeTv.setText(currentProgramTime);
                        programTv.setText(currentProgramTitle);
                        if (PreferenceUtils.isProgramGuideEnabled(GoldDetailsActivity.this)) {
                            List<ProgramGuide> programGuideList = response.body().getProgramGuide();
                            for (int i = 0; i < programGuideList.size(); i++) {
                                ProgramGuide programGuide = programGuideList.get(i);
                                Program program = new Program();
                                program.setId(programGuide.getId());
                                program.setTitle(programGuide.getTitle());
                                program.setProgramStatus(programGuide.getProgramStatus());
                                program.setTime(programGuide.getTime());
                                program.setVideoUrl(programGuide.getVideoUrl());

                                programs.add(program);
                            }

                            if (programs.size() <= 0) {
                                proGuideTv.setVisibility(GONE);
                                programRv.setVisibility(GONE);
                            } else {
                                proGuideTv.setVisibility(VISIBLE);
                                programRv.setVisibility(VISIBLE);
                                programAdapter.notifyDataSetChanged();
                            }
                        }
                        //all tv channel data
                        List<AllTvChannel> allTvChannelList = response.body().getAllTvChannel();
                        for (int i = 0; i < allTvChannelList.size(); i++) {
                            AllTvChannel allTvChannel = allTvChannelList.get(i);
                            CommonModels models = new CommonModels();
                            models.setImageUrl(allTvChannel.getPosterUrl());
                            models.setTitle(allTvChannel.getTvName());
                            models.setVideoType("tv");
                            models.setIsPaid(allTvChannel.getIsPaid());
                            models.setId(allTvChannel.getLiveTvId());
                            listRelated.add(models);
                        }
                        if (listRelated.size() == 0) {
                            tvRelated.setVisibility(GONE);
                        }
                        relatedTvAdapter.notifyDataSetChanged();

                        //additional media source data
                        List<AdditionalMediaSource> serverArray = response.body().getAdditionalMediaSource();
                        for (int i = 0; i < serverArray.size(); i++) {
                            AdditionalMediaSource jsonObject = serverArray.get(i);
                            CommonModels models = new CommonModels();
                            models.setTitle(jsonObject.getLabel());
                            models.setStremURL(jsonObject.getUrl());
                            models.setServerType(jsonObject.getSource());

                            listServer.add(models);
                        }
                        serverAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<SingleDetailsTV> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void getSeriesData(String vtype, String vId) {
        Log.e(TAG, "getSeriesData: " + vId + ", userId: " + userId);
        final List<String> seasonList = new ArrayList<>();
        final List<String> seasonListForDownload = new ArrayList<>();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SingleDetailsApi api = retrofit.create(SingleDetailsApi.class);
        Call<SingleDetails> call = api.getSingleDetails(AppConfig.API_KEY, vtype, vId);
        call.enqueue(new Callback<SingleDetails>() {
            @Override
            public void onResponse(Call<SingleDetails> call, Response<SingleDetails> response) {
                if (response.code() == 200) {
                    swipeRefreshLayout.setRefreshing(false);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);

                    SingleDetails singleDetails = response.body();
                    String isPaid = singleDetails.getIsPaid();
                    paidControl(isPaid);

                    title = singleDetails.getTitle();
                    sereisTitleTv.setText(title);
                    castImageUrl = singleDetails.getThumbnailUrl();
                    seriesTitle = title;
                    tvName.setText(title);
                    tvRelease.setText("Release On " + singleDetails.getRelease());
                    tvDes.setText(singleDetails.getDescription());

                    Picasso.get().load(singleDetails.getPosterUrl()).placeholder(R.drawable.album_art_placeholder_large)
                            .into(posterIv);
                    Picasso.get().load(singleDetails.getThumbnailUrl()).placeholder(R.drawable.poster_placeholder)
                            .into(thumbIv);

                    download_check = singleDetails.getEnableDownload();
                    trailerUrl = singleDetails.getTrailerUrl();
                    castImageUrl = singleDetails.getThumbnailUrl();
                    downloadBt.setVisibility(GONE);
                    trailerBt.setVisibility(GONE);
                    downloadAndTrailerBtContainer.setVisibility(GONE);
                    if (trailerUrl != null && !trailerUrl.equalsIgnoreCase("")) {
                        downloadAndTrailerBtContainer.setVisibility(VISIBLE);
                        trailerBt.setVisibility(VISIBLE);
                        downloadBt.setVisibility(GONE);
                    }

                    //----director---------------
                    for (int i = 0; i < singleDetails.getDirector().size(); i++) {
                        Director director = singleDetails.getDirector().get(i);
                        if (i == singleDetails.getDirector().size() - 1) {
                            strDirector = strDirector + director.getName();
                        } else {
                            strDirector = strDirector + director.getName() + ", ";
                        }
                    }
                    tvDirector.setText(strDirector);

                    //----cast---------------
                    for (int i = 0; i < singleDetails.getCast().size(); i++) {
                        Cast cast = singleDetails.getCast().get(i);

                        CastCrew castCrew = new CastCrew();
                        castCrew.setId(cast.getStarId());
                        castCrew.setName(cast.getName());
                        castCrew.setUrl(cast.getUrl());
                        castCrew.setImageUrl(cast.getImageUrl());
                        castCrews.add(castCrew);
                    }
                    castCrewAdapter.notifyDataSetChanged();
                    //---genre---------------
                    for (int i = 0; i < singleDetails.getGenre().size(); i++) {
                        Genre genre = singleDetails.getGenre().get(i);
                        if (i == singleDetails.getCast().size() - 1) {
                            strGenre = strGenre + genre.getName();
                        } else {
                            if (i == singleDetails.getGenre().size() - 1) {
                                strGenre = strGenre + genre.getName();
                            } else {
                                strGenre = strGenre + genre.getName() + ", ";
                            }
                        }
                    }
                    setGenreText();

                    //----related tv series---------------
                    for (int i = 0; i < singleDetails.getRelatedTvseries().size(); i++) {
                        RelatedMovie relatedTvSeries = singleDetails.getRelatedTvseries().get(i);

                        CommonModels models = new CommonModels();
                        models.setTitle(relatedTvSeries.getTitle());
                        models.setImageUrl(relatedTvSeries.getThumbnailUrl());
                        models.setId(relatedTvSeries.getVideosId());
                        models.setVideoType("tvseries");
                        models.setIsPaid(relatedTvSeries.getIsPaid());
                        listRelated.add(models);
                    }

                    if (listRelated.size() == 0) {
                        tvRelated.setVisibility(GONE);
                    }
                    relatedAdapter.notifyDataSetChanged();

                    //----season and episode download------------
                    for (int i = 0; i < singleDetails.getSeason().size(); i++) {
                        Season season = singleDetails.getSeason().get(i);
                        CommonModels models = new CommonModels();
                        String season_name = season.getSeasonsName();
                        models.setTitle(season.getSeasonsName());
                        seasonList.add("Season: " + season.getSeasonsName());
                        seasonListForDownload.add(season.getSeasonsName());

                        //----episode------
                        List<EpiModel> epList = new ArrayList<>();
                        epList.clear();
                        for (int j = 0; j < singleDetails.getSeason().get(i).getEpisodes().size(); j++) {
                            Episode episode = singleDetails.getSeason().get(i).getEpisodes().get(j);

                            EpiModel model = new EpiModel();
                            model.setSeson(season_name);
                            model.setEpi(episode.getEpisodesName());
                            model.setStreamURL(episode.getFileUrl());
                            model.setServerType(episode.getFileType());
                            model.setImageUrl(episode.getImageUrl());
                            model.setSubtitleList(episode.getSubtitle());
                            model.setVideoQuality(episode.getVideoQuality());
                            epList.add(model);
                        }
                        models.setListEpi(epList);
                        listServer.add(models);
                    }

                     /*if season downloads are enable
                        generate a list of downloads of every season*/
                    //----download list--------
                    if (is_gold.equals("1")) {
//                        if (PreferenceUtils.isActivePlan(GoldDetailsActivity.this)) {
                        if (seasonList.size() > 0) {
                            setSeasonData(seasonList);
                            //check if download is enabled
                            if (singleDetails.getEnableDownload().equalsIgnoreCase("1")) {
                                setSeasonDownloadData(seasonListForDownload, singleDetails.getSeason());
                            } else {
                                seasonDownloadLayout.setVisibility(GONE);
                            }
                        } else {
                            seasonSpinnerContainer.setVisibility(GONE);
                        }
//                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SingleDetails> call, Throwable t) {

            }
        });
    }

    public void setSeasonData(List<String> seasonData) {
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, seasonData);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        seasonSpinner.setAdapter(aa);
        seasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rvServer.removeAllViewsInLayout();
                rvServer.setLayoutManager(new LinearLayoutManager(GoldDetailsActivity.this,
                        RecyclerView.VERTICAL, false));
                GoldEpisodeAdapter episodeAdapter = new GoldEpisodeAdapter(GoldDetailsActivity.this,
                        listServer.get(i).getListEpi());
                rvServer.setAdapter(episodeAdapter);
                episodeAdapter.setOnEmbedItemClickListener(GoldDetailsActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setSeasonDownloadData(List<String> seasonListArray, List<Season> seasonList) {
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, seasonListArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seasonDownloadSpinner.setAdapter(arrayAdapter);

        seasonDownloadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<DownloadLink> selectedSeasonDownloadList = new ArrayList<>();
                selectedSeasonDownloadList.clear();
                selectedSeasonDownloadList.addAll(seasonList.get(position).getDownloadLinks());
                seasonDownloadRecyclerView.removeAllViewsInLayout();
                seasonDownloadRecyclerView.setLayoutManager(new LinearLayoutManager(GoldDetailsActivity.this,
                        RecyclerView.VERTICAL, false));
//                EpisodeDownloadAdapter adapter = new EpisodeDownloadAdapter(GoldDetailsActivity.this, selectedSeasonDownloadList, downloadViewModel);
//                seasonDownloadRecyclerView.setAdapter(adapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setGenreText() {
        tvGenre.setText(strGenre);
        dGenryTv.setText(strGenre);
    }

    private void getMovieData(String vtype, String vId) {
        shimmerFrameLayout.setVisibility(VISIBLE);
        shimmerFrameLayout.startShimmer();
        //strCast = "";
        strDirector = "";
        strGenre = "";

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SingleDetailsApi api = retrofit.create(SingleDetailsApi.class);
        Call<SingleDetails> call = api.getSingleDetails(AppConfig.API_KEY, vtype, vId);
        call.enqueue(new Callback<SingleDetails>() {
            @Override
            public void onResponse(Call<SingleDetails> call, Response<SingleDetails> response) {
                if (response.code() == 200) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    SingleDetails singleDetails = response.body();
                    paidControl(singleDetails.getIsPaid());
                    download_check = singleDetails.getEnableDownload();
                    trailerUrl = singleDetails.getTrailerUrl();
                    castImageUrl = singleDetails.getThumbnailUrl();
                    if (download_check.equals("1")) {
                        downloadBt.setVisibility(VISIBLE);
                    } else {
                        downloadBt.setVisibility(GONE);
                    }
                    if (trailerUrl == null || trailerUrl.equalsIgnoreCase("")) {
                        trailerBt.setVisibility(GONE);
                    } else {
                        trailerBt.setVisibility(VISIBLE);
                    }
                    //check if download and trailer is unable or not
                    // control button container
                    if (!download_check.equalsIgnoreCase("1")) {
                        if (trailerUrl == null || trailerUrl.equalsIgnoreCase(""))
                            downloadAndTrailerBtContainer.setVisibility(GONE);
                    } else {
                        downloadAndTrailerBtContainer.setVisibility(VISIBLE);
                    }
                    title = singleDetails.getTitle();
                    movieTitle = title;

                    tvName.setText(title);
                    tvRelease.setText("Release On " + singleDetails.getRelease());
                    tvDes.setText(singleDetails.getDescription());


                    Picasso.get().load(singleDetails.getPosterUrl()).placeholder(R.drawable.album_art_placeholder_large)
                            .into(posterIv);
                    Picasso.get().load(singleDetails.getThumbnailUrl()).placeholder(R.drawable.poster_placeholder)
                            .into(thumbIv);

                    //----director---------------
                    for (int i = 0; i < singleDetails.getDirector().size(); i++) {
                        Director director = response.body().getDirector().get(i);
                        if (i == singleDetails.getDirector().size() - 1) {
                            strDirector = strDirector + director.getName();
                        } else {
                            strDirector = strDirector + director.getName() + ", ";
                        }
                    }
                    tvDirector.setText(strDirector);

                    //----cast---------------
                    for (int i = 0; i < singleDetails.getCast().size(); i++) {
                        Cast cast = singleDetails.getCast().get(i);

                        CastCrew castCrew = new CastCrew();
                        castCrew.setId(cast.getStarId());
                        castCrew.setName(cast.getName());
                        castCrew.setUrl(cast.getUrl());
                        castCrew.setImageUrl(cast.getImageUrl());

                        castCrews.add(castCrew);
                    }
                    castCrewAdapter.notifyDataSetChanged();

                    //---genre---------------
                    for (int i = 0; i < singleDetails.getGenre().size(); i++) {
                        Genre genre = singleDetails.getGenre().get(i);
                        if (i == singleDetails.getCast().size() - 1) {
                            strGenre = strGenre + genre.getName();
                        } else {
                            if (i == singleDetails.getGenre().size() - 1) {
                                strGenre = strGenre + genre.getName();
                            } else {
                                strGenre = strGenre + genre.getName() + ", ";
                            }
                        }
                    }
                    tvGenre.setText(strGenre);
                    dGenryTv.setText(strGenre);

                    //-----server----------
                    List<Video> serverList = new ArrayList<>();
                    serverList.addAll(singleDetails.getVideos());
                    for (int i = 0; i < serverList.size(); i++) {
                        Video video = serverList.get(i);

                        CommonModels models = new CommonModels();
                        models.setTitle(video.getLabel());
                        models.setStremURL(video.getFileUrl());
                        models.setServerType(video.getFileType());

                        if (video.getFileType().equals("mp4")) {
                            V_URL = video.getFileUrl();
                        }

                        //----subtitle-----------
                        List<Subtitle> subArray = new ArrayList<>();
                        subArray.addAll(singleDetails.getVideos().get(i).getSubtitle());
                        if (subArray.size() != 0) {

                            List<SubtitleModel> list = new ArrayList<>();
                            for (int j = 0; j < subArray.size(); j++) {
                                Subtitle subtitle = subArray.get(j);
                                SubtitleModel subtitleModel = new SubtitleModel();
                                subtitleModel.setUrl(subtitle.getUrl());
                                subtitleModel.setLanguage(subtitle.getLanguage());
                                list.add(subtitleModel);
                            }
                            if (i == 0) {
                                listSub.addAll(list);
                            }
                            models.setListSub(list);
                        } else {
                            models.setSubtitleURL(strSubtitle);
                        }
                        listServer.add(models);
                    }

                    if (serverAdapter != null) {
                        serverAdapter.notifyDataSetChanged();
                    }

                    //----related post---------------
                    for (int i = 0; i < singleDetails.getRelatedMovie().size(); i++) {
                        RelatedMovie relatedMovie = singleDetails.getRelatedMovie().get(i);
                        CommonModels models = new CommonModels();
                        models.setTitle(relatedMovie.getTitle());
                        models.setImageUrl(relatedMovie.getThumbnailUrl());
                        models.setId(relatedMovie.getVideosId());
                        models.setVideoType("movie");
                        models.setIsPaid(relatedMovie.getIsPaid());
                        models.setIsPaid(relatedMovie.getIsPaid());
                        listRelated.add(models);
                    }

                    if (listRelated.size() == 0) {
                        tvRelated.setVisibility(GONE);
                    }
                    relatedAdapter.notifyDataSetChanged();

                    //----download list---------
                    listExternalDownload.clear();
                    listInternalDownload.clear();
                    for (int i = 0; i < singleDetails.getDownloadLinks().size(); i++) {
                        DownloadLink downloadLink = singleDetails.getDownloadLinks().get(i);

                        CommonModels models = new CommonModels();
                        models.setTitle(downloadLink.getLabel());
                        models.setStremURL(downloadLink.getDownloadUrl());
                        models.setFileSize(downloadLink.getFileSize());
                        models.setResulation(downloadLink.getResolution());
                        models.setInAppDownload(downloadLink.isInAppDownload());
                        if (downloadLink.isInAppDownload()) {
                            listInternalDownload.add(models);
                        } else {
                            listExternalDownload.add(models);
                        }
                    }

                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<SingleDetails> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getFavStatus() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        FavouriteApi api = retrofit.create(FavouriteApi.class);
        Call<FavoriteModel> call = api.verifyFavoriteList(AppConfig.API_KEY, userId, id);
        call.enqueue(new Callback<FavoriteModel>() {
            @Override
            public void onResponse(Call<FavoriteModel> call, Response<FavoriteModel> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        isFav = true;
                        //imgAddFav.setBackgroundResource(R.drawable.ic_favorite_white);
                        imgAddFav.setImageResource(R.drawable.ic_favorite_white);
                    } else {
                        isFav = false;
                        //imgAddFav.setBackgroundResource(R.drawable.ic_favorite_border_white);
                        imgAddFav.setImageResource(R.drawable.ic_favorite_border_white);
                    }
                    imgAddFav.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<FavoriteModel> call, @NotNull Throwable t) {

            }
        });

    }

    private void removeFromFav() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        FavouriteApi api = retrofit.create(FavouriteApi.class);
        Call<FavoriteModel> call = api.removeFromFavorite(AppConfig.API_KEY, userId, id);
        call.enqueue(new Callback<FavoriteModel>() {
            @Override
            public void onResponse(Call<FavoriteModel> call, Response<FavoriteModel> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        isFav = false;
                        new ToastMsg(GoldDetailsActivity.this).toastIconSuccess(response.body().getMessage());
                        //imgAddFav.setBackgroundResource(R.drawable.ic_favorite_border_white);
                        imgAddFav.setImageResource(R.drawable.ic_favorite_border_white);
                    } else {
                        isFav = true;
                        new ToastMsg(GoldDetailsActivity.this).toastIconError(response.body().getMessage());
                        //imgAddFav.setBackgroundResource(R.drawable.ic_favorite_white);
                        imgAddFav.setImageResource(R.drawable.ic_favorite_white);
                    }
                }
            }

            @Override
            public void onFailure(Call<FavoriteModel> call, Throwable t) {
                new ToastMsg(GoldDetailsActivity.this).toastIconError(getString(R.string.fetch_error));
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void addComment(String videoId, String userId, final String comments) {

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        CommentApi api = retrofit.create(CommentApi.class);
        Call<PostCommentModel> call = api.postComment(AppConfig.API_KEY, videoId, userId, comments);
        call.enqueue(new Callback<PostCommentModel>() {
            @Override
            public void onResponse(Call<PostCommentModel> call, Response<PostCommentModel> response) {
                if (response.body().getStatus().equals("success")) {
                    rvComment.removeAllViews();
                    listComment.clear();
                    getComments();
                    etComment.setText("");
                    new ToastMsg(GoldDetailsActivity.this).toastIconSuccess(response.body().getMessage());
                } else {
                    new ToastMsg(GoldDetailsActivity.this).toastIconError(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<PostCommentModel> call, Throwable t) {

            }
        });
    }

    private void getComments() {

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        CommentApi api = retrofit.create(CommentApi.class);
        Call<List<GetCommentsModel>> call = api.getAllComments(AppConfig.API_KEY, id);
        call.enqueue(new Callback<List<GetCommentsModel>>() {
            @Override
            public void onResponse(Call<List<GetCommentsModel>> call, Response<List<GetCommentsModel>> response) {
                if (response.code() == 200) {
                    listComment.addAll(response.body());

                    commentsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<GetCommentsModel>> call, Throwable t) {

            }
        });

    }

    public void hideDescriptionLayout() {
        descriptionLayout.setVisibility(GONE);
        lPlay.setVisibility(VISIBLE);
    }

    public void showSeriesLayout() {
        seriestLayout.setVisibility(VISIBLE);
    }

    public void showDescriptionLayout() {
        descriptionLayout.setVisibility(VISIBLE);
        lPlay.setVisibility(GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isPlaying && player != null) {
            //Log.e("PLAY:::","PAUSE");
            player.setPlayWhenReady(false);

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        //castManager.removeProgressWatcher(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //updateContinueWatchingData();
        resetCastPlayer();
        releasePlayer();
        handler1.removeCallbacks(mToastRunnable);

    }


    @Override
    public void onBackPressed() {
        if (activeMovie || isFullScr) {
            setPlayerNormalScreen();
            if (player != null) {
                player.setPlayWhenReady(false);
                player.stop();
            }
            showDescriptionLayout();
            activeMovie = false;
        } else {
            releasePlayer();
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check vpn connection
        helperUtils = new HelperUtils(GoldDetailsActivity.this);
        vpnStatus = helperUtils.isVpnConnectionAvailable();
        if (vpnStatus) {
            helperUtils.showWarningDialog(GoldDetailsActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
            player.setPlayWhenReady(false);
        }
    }

    public void releasePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
            player = null;
            simpleExoPlayerView.setPlayer(null);
            //simpleExoPlayerView = null;

        }
    }

    public void setMediaUrlForTvSeries(String url, String season, String episod) {
        mediaUrl = url;
        this.season = season;
        this.episod = episod;
    }

    public boolean getCastSession() {
        return castSession;
    }

    public void resetCastPlayer() {
        if (castPlayer != null) {
            castPlayer.setPlayWhenReady(false);
            castPlayer.release();
        }
    }

    public void showQueuePopup(final Context context, View view, final MediaInfo mediaInfo) {
        CastSession castSession =
                CastContext.getSharedInstance(context).getSessionManager().getCurrentCastSession();
        if (castSession == null || !castSession.isConnected()) {
            Log.w(TAG, "showQueuePopup(): not connected to a cast device");
            return;
        }
        final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            Log.w(TAG, "showQueuePopup(): null RemoteMediaClient");
            return;
        }
        MediaQueueItem queueItem = new MediaQueueItem.Builder(mediaInfo).setAutoplay(
                true).setPreloadTime(PRELOAD_TIME_S).build();
        MediaQueueItem[] newItemArray = new MediaQueueItem[]{queueItem};
        remoteMediaClient.queueLoad(newItemArray, 0,
                MediaStatus.REPEAT_MODE_REPEAT_OFF, null);

    }

    public void playNextCast(MediaInfo mediaInfo) {

        //simpleExoPlayerView.setPlayer(castPlayer);
        simpleExoPlayerView.setUseController(false);
        castControlView.setVisibility(VISIBLE);
        castControlView.setPlayer(castPlayer);
        //simpleExoPlayerView.setDefaultArtwork();
        castControlView.addVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if (visibility == GONE) {
                    castControlView.setVisibility(VISIBLE);
                    chromeCastTv.setVisibility(VISIBLE);
                }
            }
        });
        CastSession castSession =
                CastContext.getSharedInstance(this).getSessionManager().getCurrentCastSession();

        if (castSession == null || !castSession.isConnected()) {
            Log.w(TAG, "showQueuePopup(): not connected to a cast device");
            return;
        }

        final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();

        if (remoteMediaClient == null) {
            Log.w(TAG, "showQueuePopup(): null RemoteMediaClient");
            return;
        }
        MediaQueueItem queueItem = new MediaQueueItem.Builder(mediaInfo).setAutoplay(
                true).setPreloadTime(PRELOAD_TIME_S).build();
        MediaQueueItem[] newItemArray = new MediaQueueItem[]{queueItem};

        remoteMediaClient.queueLoad(newItemArray, 0,
                MediaStatus.REPEAT_MODE_REPEAT_OFF, null);
        castPlayer.setPlayWhenReady(true);

    }

    public MediaInfo getMediaInfo() {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, title);
        //movieMetadata.putString(MediaMetadata.KEY_ALBUM_ARTIST, "Test Artist");
        movieMetadata.addImage(new WebImage(Uri.parse(castImageUrl)));
        MediaInfo mediaInfo = new MediaInfo.Builder(mediaUrl)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(MimeTypes.VIDEO_UNKNOWN)
                .setMetadata(movieMetadata).build();

        return mediaInfo;

    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new ToastMsg(GoldDetailsActivity.this).toastIconSuccess("Now You can download.");
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
        }
    }

    public void hideExoControlForTv() {
        exoRewind.setVisibility(GONE);
        exoForward.setVisibility(GONE);
        liveTv.setVisibility(VISIBLE);
        seekbarLayout.setVisibility(GONE);
    }

    public void showExoControlForTv() {
        exoRewind.setVisibility(VISIBLE);
        exoForward.setVisibility(VISIBLE);
        liveTv.setVisibility(GONE);
        seekbarLayout.setVisibility(VISIBLE);
        watchLiveTv.setVisibility(VISIBLE);
        liveTv.setVisibility(GONE);
        watchStatusTv.setText(getResources().getString(R.string.watching_catch_up_tv));
    }

    private void getScreenSize() {
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        sWidth = size.x;
        sHeight = size.y;
        //Toast.makeText(this, "fjiaf", Toast.LENGTH_SHORT).show();
    }

    public class RelativeLayoutTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    //touch is start
                    downX = event.getX();
                    downY = event.getY();
                    if (event.getX() < (sWidth / 2)) {

                        //here check touch is screen left or right side
                        intLeft = true;
                        intRight = false;

                    } else if (event.getX() > (sWidth / 2)) {

                        //here check touch is screen left or right side
                        intLeft = false;
                        intRight = true;
                    }
                    break;

                case MotionEvent.ACTION_UP:

                case MotionEvent.ACTION_MOVE:

                    //finger move to screen
                    float x2 = event.getX();
                    float y2 = event.getY();

                    diffX = (long) (Math.ceil(event.getX() - downX));
                    diffY = (long) (Math.ceil(event.getY() - downY));

                    if (Math.abs(diffY) > Math.abs(diffX)) {
                        if (intLeft) {
                            //if left its for brightness

                            if (downY < y2) {
                                //down swipe brightness decrease
                            } else if (downY > y2) {
                                //up  swipe brightness increase
                            }

                        } else if (intRight) {

                            //if right its for audio
                            if (downY < y2) {
                                //down swipe volume decrease
                                mAudioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);

                            } else if (downY > y2) {
                                //up  swipe volume increase
                                mAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                            }
                        }
                    }
            }
            return true;
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void doTheAutoRefresh() {
       /* handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Write code for your refresh logic
                getProfile();
            }
        }, 3000);*/
        mToastRunnable = new Runnable() {
            @Override
            public void run() {
                handler1.postDelayed(this, 3000);
                getProfile(PreferenceUtils.getUserId(GoldDetailsActivity.this));
            }
        };
        mToastRunnable.run();

    }


    /*public final Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // your code while refreshing activity
//            getProfile();
            Toast.makeText(MainActivity.this, "Refreshing data", Toast.LENGTH_SHORT).show();
        }
    };
*/


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
                        String deviceNoDynamic = user.getDevice_no();
                        String deviceNo = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                        if (deviceNoDynamic != null) {
                            if (!deviceNoDynamic.equals("")) {
                                if (!deviceNo.equals(deviceNoDynamic)) {
                                    Toast.makeText(GoldDetailsActivity.this, "Logged in other device", Toast.LENGTH_SHORT).show();
                                    logoutUser();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });


    }

    private void logoutUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseAuth.getInstance().signOut();
        }

        SharedPreferences.Editor editor = getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
        editor.putBoolean(Constants.USER_LOGIN_STATUS, false);
        editor.apply();
        editor.commit();

        DatabaseHelper databaseHelper = new DatabaseHelper(GoldDetailsActivity.this);
        databaseHelper.deleteUserData();

        PreferenceUtils.clearSubscriptionSavedData(GoldDetailsActivity.this);

        Intent intent = new Intent(GoldDetailsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
