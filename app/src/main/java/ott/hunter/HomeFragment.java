package ott.hunter;


import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static ott.hunter.MoreActivity.familycontent;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.viewpagerindicator.LinePageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import ott.hunter.adapters.ContinueWatchingAdapter;
import ott.hunter.adapters.CountryAdapter;
import ott.hunter.adapters.GenreAdapter;
import ott.hunter.adapters.GenreHomeAdapter;
import ott.hunter.adapters.HomeGoldPageAdapter;
import ott.hunter.adapters.HomePageAdapter;
import ott.hunter.adapters.LiveTvHomeAdapter;
import ott.hunter.adapters.PopularStarAdapter;
import ott.hunter.adapters.SliderListAdapter;
import ott.hunter.database.DatabaseHelper;
import ott.hunter.database.continueWatching.ContinueWatchingModel;
import ott.hunter.database.continueWatching.ContinueWatchingViewModel;
import ott.hunter.database.homeContent.HomeContentViewModel;
import ott.hunter.models.CommonModels;
import ott.hunter.models.GenreModel;
import ott.hunter.models.GoldModel;
import ott.hunter.models.home_content.AllCountry;
import ott.hunter.models.home_content.AllGenre;
import ott.hunter.models.home_content.FeaturedTvChannel;
import ott.hunter.models.home_content.FeaturesGenreAndMovie;
import ott.hunter.models.home_content.HomeContent;
import ott.hunter.models.home_content.LatestTvseries;
import ott.hunter.models.home_content.PopularMovie;
import ott.hunter.models.home_content.PopularStars;
import ott.hunter.models.home_content.Slide;
import ott.hunter.models.home_content.Slider;
import ott.hunter.models.home_content.Video;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.HomeContentApi;
import ott.hunter.network.apis.LoginApi;
import ott.hunter.network.apis.UserDataApi;
import ott.hunter.network.model.User;
import ott.hunter.utils.Constants;
import ott.hunter.utils.NetworkInst;
import ott.hunter.utils.PreferenceUtils;
import ott.hunter.utils.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class HomeFragment extends Fragment {

    //    CardSliderViewPager cViewPager;
    ViewPager viewPager;
    private ArrayList<CommonModels> listSlider = new ArrayList<>();
    private Timer timer;
    final long DELAY_MS = 1800;
    final long PERIOD_MS = 7000;
    int currentPage = 0;


    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerViewMovie, recyclerViewTv, recyclerViewTvSeries, recyclerViewGenre, recyclerViewContinueWatching;
    private LinearLayout continueWatchingLayout;
    private RecyclerView genreRv, lang_rv;
    private RecyclerView countryRv;
    private RecyclerView popularStarsRv;
    private GenreAdapter genreAdapter;
    private LangAdapter langAdapter;
    private CountryAdapter countryAdapter;
    private PopularStarAdapter popularStarAdapter;
    private RelativeLayout genreLayout, countryLayout, tvGoldLayout;
    private HomePageAdapter adapterMovie, adapterSeries;
    private HomeGoldPageAdapter adapterGold;
    private LiveTvHomeAdapter adapterTv;
    private List<CommonModels> listMovie = new ArrayList<>();
    private List<CommonModels> listTv = new ArrayList<>();
    private List<CommonModels> listSeries = new ArrayList<>();
    private List<CommonModels> genreList = new ArrayList<>();

    private List<LangModels> langList = new ArrayList<>();
    private List<CommonModels> countryList = new ArrayList<>();
    private List<PopularStars> popularStarsList = new ArrayList<>();
    private Button btnMoreMovie, btnMoreTv, btnMoreSeries, btnContinueWatchingClear;

    private TextView tvNoItem;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView scrollView;

    //    private RelativeLayout adView, adView1, startappNativeAdView, admobNativeAdContainer;
//    private TemplateView admobNativeAdView;
    private List<GenreModel> listGenre = new ArrayList<>();
    private List<GoldModel> listGold = new ArrayList<>();

    private GenreHomeAdapter genreHomeAdapter;
    private View sliderLayout;

    private MainActivity activity;
    private LinearLayout searchRootLayout;

    private CardView searchBar;

    private ImageView menuIv, searchIv;
    private TextView pageTitle, tvseries;
    private DatabaseHelper db = new DatabaseHelper(getContext());
    private HomeContentViewModel homeContentViewModel;
    private HomeContent homeContent = null;
    private RelativeLayout popular_stars_layout, featuredTVLayout, movieLayout, tvSeriesLayout, slider_Rlayout;
    private ContinueWatchingViewModel continueWatchingViewModel;
    private LinePageIndicator circleIndicator;
    private Handler mHandler;
    private Runnable mRunnable;
    ImageView my_account;
    LinearLayout tvGoldTitleLayout;
    Button btn_more_Gold;
    RecyclerView recyclerViewGold;
    ImageView imgFree;
    ImageButton fblink, instalink, youtubelink, twitterlink;
    Slider slider;

    SliderListAdapter sliderAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();

        return inflater.inflate(R.layout.fragment_home, null);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHelper(getContext());


        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.FAMILYCONTENTSTATUS, MODE_PRIVATE);
        familycontent = sharedPreferences.getBoolean("familycontent", false);


        btnMoreSeries = view.findViewById(R.id.btn_more_series);

        fblink = view.findViewById(R.id.fblink);
        instalink = view.findViewById(R.id.instalink);
        youtubelink = view.findViewById(R.id.youtubelink);
        twitterlink = view.findViewById(R.id.twitterlink);

        btnMoreTv = view.findViewById(R.id.btn_more_tv);
        tvseries = view.findViewById(R.id.tvseries);
        btnMoreMovie = view.findViewById(R.id.btn_more_movie);
        btnContinueWatchingClear = view.findViewById(R.id.continue_watching_clear_btn);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        tvNoItem = view.findViewById(R.id.tv_noitem);
        coordinatorLayout = view.findViewById(R.id.coordinator_lyt);
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        scrollView = view.findViewById(R.id.scrollView);
        sliderLayout = view.findViewById(R.id.slider_layout);
        genreRv = view.findViewById(R.id.genre_rv);

        countryRv = view.findViewById(R.id.country_rv);
        genreLayout = view.findViewById(R.id.genre_layout);
        popularStarsRv = view.findViewById(R.id.popular_stars_rv);
        countryLayout = view.findViewById(R.id.country_layout);
        tvGoldLayout = view.findViewById(R.id.tvGoldLayout);
        viewPager = view.findViewById(R.id.viewPager);
        searchRootLayout = view.findViewById(R.id.search_root_layout);
        searchBar = view.findViewById(R.id.search_bar);
        menuIv = view.findViewById(R.id.bt_menu);
        pageTitle = view.findViewById(R.id.page_title_tv);
        searchIv = view.findViewById(R.id.search_iv);
        continueWatchingLayout = view.findViewById(R.id.continueWatchingLayout);
        recyclerViewContinueWatching = view.findViewById(R.id.recyclerViewContinueWatching);
        popular_stars_layout = view.findViewById(R.id.popular_stars_layout);
        featuredTVLayout = view.findViewById(R.id.featuredTvLayout);
        movieLayout = view.findViewById(R.id.movieLayout);
        tvSeriesLayout = view.findViewById(R.id.tvSeriesLayout);
        circleIndicator = view.findViewById(R.id.indicator_unselected_background);
        my_account = view.findViewById(R.id.my_account);
        imgFree = view.findViewById(R.id.imgFree);
        tvGoldTitleLayout = view.findViewById(R.id.tvGoldTitleLayout);
        btn_more_Gold = view.findViewById(R.id.btn_more_Gold);
        recyclerViewGold = view.findViewById(R.id.recyclerViewGold);
        slider_Rlayout = view.findViewById(R.id.lytSlider);


        imgFree.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ItemMovieActivity.class);
            intent.putExtra("id", "17");
            intent.putExtra("title", "Watch Free");
            intent.putExtra("type", "genre");
            getContext().startActivity(intent);
        });


        fblink.setOnClickListener(v -> {

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://www.facebook.com/profile.php?id=100088118545564"));
            getContext().startActivity(i);


        });


        twitterlink.setOnClickListener(v -> {

            Intent i = new Intent(Intent.ACTION_VIEW);
            //i.setData(Uri.parse("https://www.youtube.com/channel/UC2U1WBF9g47zkb3ENy4O5jA"));
            i.setData(Uri.parse("https://twitter.com/HuntersottApp"));
            getContext().startActivity(i);

        });

        instalink.setOnClickListener(v -> {

            Intent i = new Intent(Intent.ACTION_VIEW);
            //i.setData(Uri.parse("https://www.youtube.com/channel/UC2U1WBF9g47zkb3ENy4O5jA"));
            i.setData(Uri.parse("https://instagram.com/huntersott?igshid=MTg0ZDhmNDA="));
            getContext().startActivity(i);

        });


        youtubelink.setOnClickListener(v -> {

            Intent i = new Intent(Intent.ACTION_VIEW);
            //i.setData(Uri.parse("https://www.youtube.com/channel/UC2U1WBF9g47zkb3ENy4O5jA"));
            i.setData(Uri.parse("https://www.youtube.com/channel/UCzVg4Lz_aD75ue_-0X3dspA"));
            getContext().startActivity(i);


        });

        tvseries.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ItemSeriesActivity.class);
            intent.putExtra("title", "TV Series");
            getActivity().startActivity(intent);
        });

        if (db.getConfigurationData().getAppConfig().getGenreVisible()) {
            genreLayout.setVisibility(View.VISIBLE);
        }
        if (db.getConfigurationData().getAppConfig().getCountryVisible()) {
            countryLayout.setVisibility(View.VISIBLE);
        }

        pageTitle.setText(getResources().getString(R.string.home));

        if (activity.isDark) {
            pageTitle.setTextColor(activity.getResources().getColor(R.color.white));
            searchBar.setCardBackgroundColor(activity.getResources().getColor(R.color.black_window_light));
            menuIv.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_menu));
            searchIv.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_search_white));
        }

        //----init timer slider--------------------
        timer = new Timer();

        //----btn click-------------
        btnClick();

        /*----continue Watching view----*/
        continueWatchingViewModel = ViewModelProviders.of(getActivity()).get(ContinueWatchingViewModel.class);
        continueWatchingViewModel.getAllContents().observe(getActivity(), new Observer<List<ContinueWatchingModel>>() {
            @Override
            public void onChanged(List<ContinueWatchingModel> items) {
                if (items.size() > 0) {
                    Collections.reverse(items);
                    continueWatchingLayout.setVisibility(View.VISIBLE);
                    ContinueWatchingAdapter adapter = new ContinueWatchingAdapter(getContext(), items);
                    recyclerViewContinueWatching.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
                    recyclerViewContinueWatching.setHasFixedSize(true);
                    recyclerViewContinueWatching.setNestedScrollingEnabled(false);
                    recyclerViewContinueWatching.setAdapter(adapter);

                } else {
                    recyclerViewContinueWatching.removeAllViews();
                    continueWatchingLayout.setVisibility(View.GONE);
                }
            }
        });


        // --- genre recycler view ---------
        genreRv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        genreRv.setHasFixedSize(true);
        genreRv.setNestedScrollingEnabled(false);
        // genreAdapter = new GenreAdapter(getActivity(), genreList, "genre", "home");
        genreAdapter = new GenreAdapter(getActivity(), genreList, "genre", "home");
        genreRv.setAdapter(genreAdapter);


        // --- country recycler view ---------
        countryRv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        countryRv.setHasFixedSize(true);
        countryRv.setNestedScrollingEnabled(false);
        countryAdapter = new CountryAdapter(getActivity(), countryList, "home");
        countryRv.setAdapter(countryAdapter);

        // --- popular stars recycler view ---------
        popularStarsRv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        popularStarsRv.setHasFixedSize(true);
        popularStarsRv.setNestedScrollingEnabled(false);
        popularStarAdapter = new PopularStarAdapter(activity, popularStarsList);
        popularStarsRv.setAdapter(popularStarAdapter);

        //----featured tv recycler view-----------------
        recyclerViewTv = view.findViewById(R.id.recyclerViewTv);
        recyclerViewTv.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTv.setHasFixedSize(true);
        recyclerViewTv.setNestedScrollingEnabled(false);
        adapterTv = new LiveTvHomeAdapter(getActivity(), listTv, "MainActivity");
        recyclerViewTv.setAdapter(adapterTv);

        //----movie's recycler view-----------------
        recyclerViewMovie = view.findViewById(R.id.recyclerView);
        recyclerViewMovie.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMovie.setHasFixedSize(true);
        recyclerViewMovie.setNestedScrollingEnabled(false);
        adapterMovie = new HomePageAdapter(getContext(), listMovie);
        recyclerViewMovie.setAdapter(adapterMovie);

        //----series's recycler view-----------------
        recyclerViewTvSeries = view.findViewById(R.id.recyclerViewTvSeries);
        recyclerViewTvSeries.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTvSeries.setHasFixedSize(true);
        recyclerViewTvSeries.setNestedScrollingEnabled(false);
        adapterSeries = new HomePageAdapter(getActivity(), listSeries);
        recyclerViewTvSeries.setAdapter(adapterSeries);


        //----genre's recycler view--------------------
        recyclerViewGenre = view.findViewById(R.id.recyclerView_by_genre);
        recyclerViewGenre.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewGenre.setHasFixedSize(true);
        recyclerViewGenre.setNestedScrollingEnabled(false);
        genreHomeAdapter = new GenreHomeAdapter(getContext(), listGenre);
        recyclerViewGenre.setAdapter(genreHomeAdapter);

        //----gold recycler view--------------------
        recyclerViewGold.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewGold.setHasFixedSize(true);
        recyclerViewGold.setNestedScrollingEnabled(false);
        adapterGold = new HomeGoldPageAdapter(getContext(), listGold);
        recyclerViewGold.setAdapter(adapterGold);

        shimmerFrameLayout.startShimmer();

        //home content live data
        homeContentViewModel = new ViewModelProvider(getActivity()).get(HomeContentViewModel.class);
        homeContentViewModel.getAllContents().observe(getActivity(), new Observer<HomeContent>() {
            @Override
            public void onChanged(HomeContent data) {
                if (data != null) {
                    //populate screen with data
                    homeContent = data;
                    populateViews();
                    Log.e("HomeContentDatabase", "onChanged");
                }
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerViewMovie.removeAllViews();
                recyclerViewTv.removeAllViews();
                recyclerViewTvSeries.removeAllViews();
                recyclerViewGenre.removeAllViews();
                genreRv.removeAllViews();
                countryRv.removeAllViews();
                popularStarsRv.removeAllViews();
                genreList.clear();
                countryList.clear();
                listMovie.clear();
                listSeries.clear();
                listGold.clear();
                listSlider.clear();
                listTv.clear();
                listGenre.clear();
                popularStarsList.clear();


                if (new NetworkInst(getContext()).isNetworkAvailable()) {
//                    getHomeContentDataFromServer();

                    if (familycontent) {

                        try {
                            slider.getSlideArrayList().clear();
                            sliderAdapter.notifyDataSetChanged();
                            slider_Rlayout.setVisibility(GONE);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getFamilyContententFromServer();
                    } else {
                        try {
                            slider.getSlideArrayList().clear();
                            sliderAdapter.notifyDataSetChanged();
                            slider_Rlayout.setVisibility(GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getHomeContentDataFromServer();
                    }


                    if (PreferenceUtils.getUserId(activity) != null)
                        if (!PreferenceUtils.getUserId(activity).equals(""))
                            getProfile(PreferenceUtils.getUserId(activity));

                } else {
                    tvNoItem.setText(getString(R.string.no_internet));
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                }






               /* if (new NetworkInst(getContext()).isNetworkAvailable()) {
                    getHomeContentDataFromServer();
                    if (PreferenceUtils.isLoggedIn(activity))
                        getProfile(PreferenceUtils.getUserId(activity));

                } else {
                    tvNoItem.setText(getString(R.string.no_internet));
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                }*/
            }
        });


        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    animateSearchBar(false);
                }
                if (scrollY > oldScrollY) { // down
                    animateSearchBar(true);
                }
            }
        });

//        getAdDetails();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // getHomeContentDataFromServer();

                if (familycontent) {

                    try {
                        slider.getSlideArrayList().clear();
                        sliderAdapter.notifyDataSetChanged();
                        slider_Rlayout.setVisibility(GONE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getFamilyContententFromServer();
                } else {

                    try {
                        slider.getSlideArrayList().clear();
                        sliderAdapter.notifyDataSetChanged();
                        slider_Rlayout.setVisibility(GONE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getHomeContentDataFromServer();
                }

            }
        }, 1000);

//        if (!PreferenceUtils.okClicked) {
//            getImage("");
//        }
    }


    private void getFamilyContententFromServer() {

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        HomeContentApi api = retrofit.create(HomeContentApi.class);
        Call<HomeContent> call = api.getFamilyContent(AppConfig.API_KEY, PreferenceUtils.getUserId(activity));

        call.enqueue(new Callback<HomeContent>() {
            @Override
            public void onResponse(Call<HomeContent> call, Response<HomeContent> response) {

                if (response.code() == 200) {

                    //insert data to room database
                    // homeContentViewModel = ViewModelProviders.of(getActivity()).get(HomeContentViewModel.class);
                    homeContent = response.body();
                    homeContent.setHomeContentId(1);
                    homeContentViewModel.insert(homeContent);

                    populateViews();
                    swipeRefreshLayout.setRefreshing(false);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    coordinatorLayout.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);

                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<HomeContent> call, Throwable t) {

            }
        });
    }

    private void setAutoSwipable(ArrayList<Slide> slideArrayList) {
        mHandler = new Handler();
        mRunnable = () -> {
            if (currentPage == slideArrayList.size()) {
                currentPage = 0;
            }
            viewPager.setCurrentItem(currentPage++, true);

            //  viewPager.setTranslationX(-2 * viewPager.getWidth() * viewPager.getCurrentItem() );

        };


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(mRunnable);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    private void populateViews() {
        if (homeContent == null) {
            //tvNoItem.setText(getString(R.string.no_internet));
            shimmerFrameLayout.startShimmer();
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            return;
        }
        swipeRefreshLayout.setRefreshing(false);
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        coordinatorLayout.setVisibility(View.GONE);

        //clear all array list
        recyclerViewMovie.removeAllViews();
        recyclerViewTv.removeAllViews();
        recyclerViewTvSeries.removeAllViews();
        recyclerViewGenre.removeAllViews();
        genreRv.removeAllViews();
        countryRv.removeAllViews();
        popularStarsRv.removeAllViews();
        genreList.clear();
        countryList.clear();
        listMovie.clear();
        listSeries.clear();
        listGold.clear();
        listSlider.clear();
        listTv.clear();
        listGenre.clear();
        popularStarsList.clear();

        //slider data
        Slider slider = homeContent.getSlider();
        if (slider.getSliderType().equalsIgnoreCase("disable")) {
            sliderLayout.setVisibility(View.GONE);
        } else if (slider.getSliderType().equalsIgnoreCase("movie")) {

        } else if (slider.getSliderType().equalsIgnoreCase("image")) {

        }

    /*    SliderAdapter sliderAdapter = new SliderAdapter(slider.getSlideArrayList());
        cViewPager.setAdapter(sliderAdapter);
        circleIndicator.setMinimumHeight(2);
        circleIndicator.setViewPager(cViewPager);
        sliderAdapter.notifyDataSetChanged();*/

        if (!slider.getSlideArrayList().isEmpty()) {

            //   sliderLayout.setVisibility(View.VISIBLE);

            slider_Rlayout.setVisibility(VISIBLE);

            SliderListAdapter sliderAdapter = new SliderListAdapter(activity, slider.getSlideArrayList());

            viewPager.setAdapter(sliderAdapter);
            viewPager.setOffscreenPageLimit(1);
            viewPager.setClipToPadding(false);

/*
            viewPager.setPadding(-10, 0, -10, 0);
*/

            //  viewPager.setPageMargin(25);
            // viewPager.setClipToPadding(false);
            // viewPager.setPadding(60, 0, 60, 0);
            // viewPager.setPadding(30, 0, 30, 0);


            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                    itemChannel = sliderList.get(position);
                }

                @Override
                public void onPageSelected(int position) {
//                    sliderAdapter.playVideo();
                    currentPage = position;
//                    itemChannel = sliderList.get(position);
                }


                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            circleIndicator.setMinimumHeight(2);
            circleIndicator.setViewPager(viewPager);
            setAutoSwipable(slider.getSlideArrayList());
        } else {
            sliderLayout.setVisibility(View.GONE);
        }


        //genre data
        if (db.getConfigurationData().getAppConfig().getGenreVisible()) {
            for (int i = 0; i < homeContent.getAllGenre().size(); i++) {
                AllGenre genre = homeContent.getAllGenre().get(i);
                CommonModels models = new CommonModels();
                models.setId(genre.getGenreId());
                models.setTitle(genre.getName());
                models.setImageUrl(genre.getImageUrl());
                genreList.add(models);
            }
            genreAdapter.notifyDataSetChanged();
        }


        //country data
        if (db.getConfigurationData().getAppConfig().getCountryVisible()) {
            for (int i = 0; i < homeContent.getAllCountry().size(); i++) {
                AllCountry country = homeContent.getAllCountry().get(i);
                CommonModels models = new CommonModels();
                models.setId(country.getCountryId());
                models.setTitle(country.getName());
                models.setImageUrl(country.getImageUrl());
                countryList.add(models);
            }
            countryAdapter.notifyDataSetChanged();
        }

        //popular stars data
      /*  if (homeContent.getPopularStarsList() != null) {
            if (homeContent.getPopularStarsList().size() > 0) {
                popular_stars_layout.setVisibility(View.VISIBLE);
                popularStarsList.addAll(homeContent.getPopularStarsList());
                popularStarAdapter.notifyDataSetChanged();
            }
        }*/



        try {
            //tv channel data
            if (homeContent.getFeaturedTvChannel().size() > 0) {
                for (int i = 0; i < homeContent.getFeaturedTvChannel().size(); i++) {
                    FeaturedTvChannel tvChannel = homeContent.getFeaturedTvChannel().get(i);
                    CommonModels models = new CommonModels();
                    models.setImageUrl(tvChannel.getPosterUrl());
                    models.setTitle(tvChannel.getTvName());
                    models.setVideoType("tv");
                    models.setId(tvChannel.getLiveTvId());
                    models.setIsPaid(tvChannel.getIsPaid());
                    listTv.add(models);
                }
                featuredTVLayout.setVisibility(View.VISIBLE);
                adapterTv.notifyDataSetChanged();
            }
        } catch (Exception e) {

        }




       /* //latest movies data
        if (homeContent.getLatestMovies().size() > 0) {
            for (int i = 0; i < homeContent.getLatestMovies().size(); i++) {
                LatestMovie movie = homeContent.getLatestMovies().get(i);
                CommonModels models = new CommonModels();
                models.setImageUrl(movie.getThumbnailUrl());
                models.setTitle(movie.getTitle());
                models.setVideoType("movie");
                models.setReleaseDate(movie.getRelease());
                models.setQuality(movie.getVideoQuality());
                models.setId(movie.getVideosId());
                models.setIsPaid(movie.getIsPaid());
                listMovie.add(models);
            }
            movieLayout.setVisibility(View.VISIBLE);
            adapterMovie.notifyDataSetChanged();
        }*/


        //popular movies data
        if (homeContent.getPapularmovie().size() > 0) {
            for (int i = 0; i < homeContent.getPapularmovie().size(); i++) {
                PopularMovie movie = homeContent.getPapularmovie().get(i);
                CommonModels models = new CommonModels();
                models.setImageUrl(movie.getThumbnailUrl());
                models.setTitle(movie.getTitle());
                models.setVideoType("tvseries");
                models.setReleaseDate(movie.getRelease());
                models.setQuality(movie.getVideoQuality());
                models.setId(movie.getVideosId());
                models.setIsPaid(movie.getIsPaid());
                listMovie.add(models);
            }

            //  movieLayout.setVisibility(View.VISIBLE);
            movieLayout.setVisibility(GONE);
            adapterMovie.notifyDataSetChanged();
        }


        //latest tv series
        if (homeContent.getLatestTvseries().size() > 0) {
            for (int i = 0; i < homeContent.getLatestTvseries().size(); i++) {
                LatestTvseries tvSeries = homeContent.getLatestTvseries().get(i);
                CommonModels models = new CommonModels();
                models.setImageUrl(tvSeries.getThumbnailUrl());
                models.setTitle(tvSeries.getTitle());
                models.setVideoType("tvseries");
                models.setReleaseDate(tvSeries.getRelease());
                models.setQuality(tvSeries.getVideoQuality());
                models.setId(tvSeries.getVideosId());
                models.setIsPaid(tvSeries.getIsPaid());
                listSeries.add(models);
            }
            // tvSeriesLayout.setVisibility(View.VISIBLE);
            tvSeriesLayout.setVisibility(GONE);
            adapterSeries.notifyDataSetChanged();
        }


        //rent on watch
        //latest Gold series
     /*   if (homeContent.getGoldList().size() > 0) {
            for (int i = 0; i < homeContent.getGoldList().size(); i++) {
                LatestGoldType tvSeries = homeContent.getGoldList().get(i);
                GoldModel models = new GoldModel();
                models.setImageUrl(tvSeries.getThumbnailUrl());
                models.setTitle(tvSeries.getTitle());
                models.setVideoType("tvseries");
                models.setReleaseDate(tvSeries.getRelease());
                models.setQuality(tvSeries.getVideoQuality());
                models.setId(tvSeries.getVideosId());
                models.setIsPaid(tvSeries.getIsPaid());
                models.setVideo_flag(tvSeries.getVideoFlag());
                models.setVideo_plan(tvSeries.getVideoPlan());
                models.setIs_gold_paid(tvSeries.getIsGoldPaid());
                listGold.add(models);
            }
            tvGoldLayout.setVisibility(View.VISIBLE);
            adapterGold.notifyDataSetChanged();
        }
*/


        //get data by genre
        for (int i = 0; i < homeContent.getFeaturesGenreAndMovie().size(); i++) {
            FeaturesGenreAndMovie genreAndMovie = homeContent.getFeaturesGenreAndMovie().get(i);
            GenreModel models = new GenreModel();

            models.setName(genreAndMovie.getName());
            models.setId(genreAndMovie.getGenreId());
            List<CommonModels> listGenreMovie = new ArrayList<>();
            for (int j = 0; j < genreAndMovie.getVideos().size(); j++) {
                Video video = genreAndMovie.getVideos().get(j);
                CommonModels commonModels = new CommonModels();

                commonModels.setId(video.getVideosId());
                commonModels.setTitle(video.getTitle());
                commonModels.setIsPaid(video.getIsPaid());

                if (video.getIsTvseries().equals("0")) {
                    commonModels.setVideoType("movie");
                } else {
                    commonModels.setVideoType("tvseries");
                }

                commonModels.setReleaseDate(video.getRelease());
                commonModels.setQuality(video.getVideoQuality());
                commonModels.setImageUrl(video.getThumbnailUrl());

                listGenreMovie.add(commonModels);
            }
            models.setList(listGenreMovie);

            listGenre.add(models);
            genreHomeAdapter.notifyDataSetChanged();
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
                        if (user.getLogout_status().equals("1")) {
                            String deviceNoDynamic = user.getDevice_no();
                            String deviceNo = Settings.Secure.getString(activity.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                            if (deviceNoDynamic != null) {
                                if (!deviceNoDynamic.equals("")) {
                                    if (!deviceNo.equals(deviceNoDynamic)) {
                                        Toast.makeText(activity, "Logged in other device", Toast.LENGTH_SHORT).show();
                                        logoutUser(uid);
                                    }
                                }
                            }
                        } else {
                            logoutUser(uid);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });


    }

   /* private void logoutUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseAuth.getInstance().signOut();
        }

        SharedPreferences.Editor editor = activity.getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
        editor.putBoolean(Constants.USER_LOGIN_STATUS, false);
        editor.apply();
        editor.commit();

        DatabaseHelper databaseHelper = new DatabaseHelper(activity);
        databaseHelper.deleteUserData();

        PreferenceUtils.clearSubscriptionSavedData(activity);

        Intent intent = new Intent(activity, LoginActivity.class);
        startActivity(intent);
        activity.finish();
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

                        SharedPreferences.Editor editor = activity.getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
                        editor.putBoolean(Constants.USER_LOGIN_STATUS, false);
                        editor.apply();
                        editor.commit();

                        DatabaseHelper databaseHelper = new DatabaseHelper(activity);
                        databaseHelper.deleteUserData();

                        PreferenceUtils.clearSubscriptionSavedData(activity);

                        Intent intent = new Intent(activity, LoginActivity.class);
                        startActivity(intent);
                        activity.finish();
                    } else {
                        new ToastMsg(activity).toastIconError(response.body().getData());

                    }
                } else {

                    new ToastMsg(activity).toastIconError(getString(R.string.error_toast));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                new ToastMsg(activity).toastIconError(getString(R.string.error_toast));
            }
        });
    }


    private void getHomeContentDataFromServer() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        HomeContentApi api = retrofit.create(HomeContentApi.class);
        Call<HomeContent> call = api.getHomeContent(AppConfig.API_KEY, PreferenceUtils.getUserId(activity));
        call.enqueue(new Callback<HomeContent>() {
            @Override
            public void onResponse(Call<HomeContent> call, retrofit2.Response<HomeContent> response) {
                if (response.code() == 200) {

                    //insert data to room database
                    // homeContentViewModel = ViewModelProviders.of(getActivity()).get(HomeContentViewModel.class);
                    homeContent = response.body();
                    homeContent.setHomeContentId(1);
                    homeContentViewModel.insert(homeContent);

                    populateViews();
                    swipeRefreshLayout.setRefreshing(false);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    coordinatorLayout.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);

                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<HomeContent> call, Throwable t) {
                //              swipeRefreshLayout.setRefreshing(false);
//                shimmerFrameLayout.stopShimmer();
//                shimmerFrameLayout.setVisibility(View.GONE);
//                coordinatorLayout.setVisibility(View.VISIBLE);
//                scrollView.setVisibility(View.GONE);

            }
        });
    }
//
//    private void loadAd() {
//        AdsConfig adsConfig = new DatabaseHelper(getActivity()).getConfigurationData().getAdsConfig();
//        if (adsConfig.getAdsEnable().equals("1")) {
//
//            if (adsConfig.getMobileAdsNetwork().equalsIgnoreCase(Constants.ADMOB)) {
//                BannerAds.ShowAdmobBannerAds(getActivity(), adView);
//                //BannerAds.ShowAdmobBannerAds(getContext(), adView1);
//
//                //code for native adview
//                admobNativeAdView.setVisibility(View.VISIBLE);
//                NativeAds.showAdmobNativeAds(getActivity(), admobNativeAdView);
//
//            } else if (adsConfig.getMobileAdsNetwork().equalsIgnoreCase(Constants.START_APP)) {
//                BannerAds.showStartAppBanner(getActivity(), adView);
//                admobNativeAdView.setVisibility(View.GONE);
//                NativeAds nativeAds = new NativeAds();
//                nativeAds.showStartAppNativeAds(getActivity(), startappNativeAdView);
//
//            } else if (adsConfig.getMobileAdsNetwork().equalsIgnoreCase(Constants.NETWORK_AUDIENCE)) {
//                BannerAds.showFANBanner(getActivity(), adView);
//                //BannerAds.showFANBanner(getContext(), adView1);
//                admobNativeAdView.setVisibility(View.GONE);
//                NativeAds.showFANNativeBannerAd(getActivity(), adView1);
//            }
//        }else {
//            admobNativeAdView.setVisibility(View.GONE);
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        NativeAds.releaseAdmobNativeAd();
    }


    private void btnClick() {
        my_account.setOnClickListener(v -> startActivity(new Intent(activity, MoreActivity.class)));

        btnMoreMovie.setOnClickListener(v -> {
            /*Intent intent = new Intent(getContext(), ItemMovieActivity.class);
            intent.putExtra("title", "Movies");
            getActivity().startActivity(intent);*/
            Intent intent = new Intent(getContext(), ItemPopularActivity.class);
            intent.putExtra("title", "TV Series");
            getActivity().startActivity(intent);
        });
        btnMoreTv.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ItemTVActivity.class);
            intent.putExtra("title", "Live TV");
            getActivity().startActivity(intent);
        });

        btnMoreSeries.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ItemSeriesActivity.class);
            intent.putExtra("title", "TV Series");
            getActivity().startActivity(intent);
        });


        btn_more_Gold.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ItemGoldSeriesActivity.class);
            intent.putExtra("title", "CinePrime Gold");
            getActivity().startActivity(intent);
        });

        btnContinueWatchingClear.setOnClickListener(v -> continueWatchingViewModel.deleteAllContent());

    }
/*
    private void getAdDetails() {
        DatabaseHelper db = new DatabaseHelper(getContext());
//        AdsConfig adsConfig = db.getConfigurationData().getAdsConfig();

//        new GDPRChecker()
//                .withContext(activity)
//                .withPrivacyUrl(AppConfig.TERMS_URL) // your privacy url
//                .withPublisherIds(adsConfig.getAdmobAppId()) // your admob account Publisher id
//                //.withTestMode("9424DF76F06983D1392E609FC074596C") // remove this on real project
//                .check();

//        loadAd();
    }*/

    @Override
    public void onStart() {
        super.onStart();

        menuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                activity.openDrawer();
            }
        });


        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.goToSearchActivity();
            }
        });

        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
        timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    boolean isSearchBarHide = false;

    private void animateSearchBar(final boolean hide) {
        if (isSearchBarHide && hide || !isSearchBarHide && !hide) return;
        isSearchBarHide = hide;
        int moveY = hide ? -(2 * searchRootLayout.getHeight()) : 0;
        searchRootLayout.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    private void getImage(String uid) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        UserDataApi api = retrofit.create(UserDataApi.class);
        Call<ResponseBody> call = api.getImage(AppConfig.API_KEY, "");
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                            String imageUrl = jsonObject.getString("image_url");
                            if (!imageUrl.equals("")) {
                                newUpdateDialog(imageUrl);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });

    }

    public void newUpdateDialog(String image) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.new_update_dialog_layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setDimAmount(0.3F);

        AppCompatImageView img_image = dialog.findViewById(R.id.img_image);
        Glide.with(activity).load(image).into(img_image);

        CardView card_cancel = dialog.findViewById(R.id.card_cancel);
        card_cancel.setOnClickListener(v -> {
            dialog.dismiss();

            PreferenceUtils.okClicked = true;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            String currentDate = sdf.format(new Date());
            SharedPreferences sharedPref = activity.getSharedPreferences("dialog", 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("LAST_LAUNCH_DATE", currentDate);
            editor.commit();
        });

        dialog.show();
    }
}
