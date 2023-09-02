package ott.hunter;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import ott.hunter.adapters.NavigationAdapter;
import ott.hunter.database.DatabaseHelper;
import ott.hunter.models.NavigationModel;
import ott.hunter.more.HelpActivity;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.LoginApi;
import ott.hunter.network.apis.SubscriptionApi;
import ott.hunter.network.apis.UserDataApi;
import ott.hunter.network.model.ActiveStatus;
import ott.hunter.network.model.ActiveSubscription;
import ott.hunter.network.model.SubscriptionHistory;
import ott.hunter.network.model.User;
import ott.hunter.utils.Constants;
import ott.hunter.utils.HelperUtils;
import ott.hunter.utils.PreferenceUtils;
import ott.hunter.utils.RtlUtils;
import ott.hunter.utils.SpacingItemDecoration;
import ott.hunter.utils.ToastMsg;
import ott.hunter.utils.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MoreActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private LinearLayout navHeaderLayout;

    private RecyclerView recyclerView;
    private NavigationAdapter mAdapter;
    private List<NavigationModel> list = new ArrayList<>();
    private NavigationView navigationView;
    private String[] navItemImage;

    private String[] navItemName2;
    private String[] navItemImage2;
    private boolean status = false;
    private FirebaseAnalytics mFirebaseAnalytics;
    public boolean isDark = true;
    private String navMenuStyle;
    private Switch themeSwitch;
    private final int PERMISSION_REQUEST_CODE = 100;
    private String searchType;
    private boolean[] selectedtype = new boolean[3]; // 0 for movie, 1 for series, 2 for live tv....
    private DatabaseHelper db;
    private boolean vpnStatus;
    private HelperUtils helperUtils;
    private String id;
    private ProgressBar progressBar;
    ImageView back_iv;
    private String userId;
    private List<ActiveSubscription> activeSubscriptions = new ArrayList<>();
    //    CardView cv;
    LinearLayout ll_profiledetails, lnr_buyPlan;
    TextView txt_premium;
    ImageView imageAvtar, editProfile;
    TextView textName, textEmail, planstatus, planname, txtmobile, textLogout;
    private Toolbar mToolbar;
    RelativeLayout relNotLogin;
    LinearLayout lnr_userdata;

    /*Navigation layout*/
    ImageView imgProfile, imgWatchLater, imgSubscription, imgSupport, imgSettings, imgHelp, imgRateUs, imgPrivacyPolicy, imgTermsCondition, imgRefundPolicy, imgSignout;
    TextView txtProfile, txtWatchLater, txtSubscription, txtSupport, txtSettings, txtHelp, txtRateUs, txtPrivacyPolicy, txtTermsCondition, txtRefundPolicy, txtSignout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RtlUtils.setScreenDirection(this);
        db = new DatabaseHelper(MoreActivity.this);

        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("dark", false);
        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_more);
        //check vpn connection
        helperUtils = new HelperUtils(MoreActivity.this);
        vpnStatus = helperUtils.isVpnConnectionAvailable();

        userId = PreferenceUtils.getUserId(MoreActivity.this);

        if (vpnStatus) {
            helperUtils.showWarningDialog(MoreActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
            return;
        }
        // To resolve cast button visibility problem. Check Cast State when app is open.
        CastContext castContext = CastContext.getSharedInstance(this);
        castContext.getCastState();

        navMenuStyle = db.getConfigurationData().getAppConfig().getMenu();

        //---analytics-----------
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "more_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        //update subscription
       /* if (PreferenceUtils.isLoggedIn(MoreActivity.this)) {
            PreferenceUtils.updateSubscriptionStatus(MoreActivity.this);
        }

        // checking storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkStoragePermission()) {
                createDownloadDir();
            } else {
                requestPermission();
            }
        } else {
            createDownloadDir();
        }*/

        //----init---------------------------
        /*navigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);*/
        navHeaderLayout = findViewById(R.id.nav_head_layout);
        progressBar = findViewById(R.id.progress_bar);
        themeSwitch = findViewById(R.id.theme_switch);
//        cv = findViewById(R.id.cv);
        imageAvtar = findViewById(R.id.imageAvtar);
        textName = findViewById(R.id.textName);
        ll_profiledetails = findViewById(R.id.ll_profiledetails);
        lnr_buyPlan = findViewById(R.id.lnr_buyPlan);
        txt_premium = findViewById(R.id.txt_premium);
        planstatus = findViewById(R.id.planstatus);
        planname = findViewById(R.id.planname);
        txtmobile = findViewById(R.id.txtmobile);
        editProfile = findViewById(R.id.editProfile);
        textEmail = findViewById(R.id.textEmail);
        textLogout = findViewById(R.id.textLogout);
        back_iv = findViewById(R.id.back_iv);
        mToolbar = findViewById(R.id.subscription_toolbar);
        relNotLogin = findViewById(R.id.relNotLogin);
        lnr_userdata = findViewById(R.id.lnr_userdata);

        /*Navigation Layout*/
        imgProfile = findViewById(R.id.imgProfile);
        imgWatchLater = findViewById(R.id.imgWatchLater);
        imgSubscription = findViewById(R.id.imgSubscription);
        imgSupport = findViewById(R.id.imgSupport);
        imgSettings = findViewById(R.id.imgSettings);
        imgHelp = findViewById(R.id.imgHelp);
        imgRateUs = findViewById(R.id.imgRateUs);
        imgPrivacyPolicy = findViewById(R.id.imgPrivacyPolicy);
        imgTermsCondition = findViewById(R.id.imgTermsCondition);
        imgRefundPolicy = findViewById(R.id.imgRefundPolicy);
        imgSignout = findViewById(R.id.imgSignout);

        txtProfile = findViewById(R.id.txtProfile);
        txtWatchLater = findViewById(R.id.txtWatchLater);
        txtSubscription = findViewById(R.id.txtSubscription);
        txtSupport = findViewById(R.id.txtSupport);
        txtSettings = findViewById(R.id.txtSettings);
        txtHelp = findViewById(R.id.txtHelp);
        txtRateUs = findViewById(R.id.txtRateUs);
        txtPrivacyPolicy = findViewById(R.id.txtPrivacyPolicy);
        txtTermsCondition = findViewById(R.id.txtTermsCondition);
        txtRefundPolicy = findViewById(R.id.txtRefundPolicy);
        txtSignout = findViewById(R.id.txtSignout);

        themeSwitch.setChecked(isDark);

        if (isDark) {
            mToolbar.setBackgroundColor(getResources().getColor(R.color.black_window_light));

        }
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        //----navDrawer------------------------
        //toolbar = findViewById(R.id.toolbar);

//        navigationView.setNavigationItemSelectedListener(this);

        //----fetch array------------
        String[] navItemName = getResources().getStringArray(R.array.nav_item_name);
        navItemImage = getResources().getStringArray(R.array.nav_item_image);

        navItemImage2 = getResources().getStringArray(R.array.nav_item_image_2);
        navItemName2 = getResources().getStringArray(R.array.nav_item_name_2);

        //----navigation view items---------------------
        recyclerView = findViewById(R.id.recyclerView);
        if (navMenuStyle == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));


        } else if (navMenuStyle.equals("grid")) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 15), true));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        }
        recyclerView.setHasFixedSize(true);

        status = PreferenceUtils.isLoggedIn(this);
        if (status) {
            PreferenceUtils.updateSubscriptionStatus(MoreActivity.this);

            getSubscriptionHistory();
            getActiveSubscriptionFromDatabase();

            imgProfile.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            imgSubscription.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            imgWatchLater.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            imgHelp.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            imgSignout.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

            txtProfile.setTextColor(getResources().getColor(R.color.white));
            txtSubscription.setTextColor(getResources().getColor(R.color.white));
            txtWatchLater.setTextColor(getResources().getColor(R.color.white));
            txtHelp.setTextColor(getResources().getColor(R.color.white));
            txtSignout.setTextColor(getResources().getColor(R.color.white));

            lnr_userdata.setVisibility(View.VISIBLE);
            relNotLogin.setVisibility(View.GONE);


//            for (int i = 0; i < navItemName.length; i++) {
//                NavigationModel models = new NavigationModel(navItemImage[i], navItemName[i]);
//                list.add(models);
//            }
        } else {
//            for (int i = 0; i < navItemName2.length; i++) {
//                NavigationModel models = new NavigationModel(navItemImage2[i], navItemName2[i]);
//                list.add(models);
//            }

            imgProfile.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.default_text), android.graphics.PorterDuff.Mode.SRC_IN);
            imgSubscription.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.default_text), android.graphics.PorterDuff.Mode.SRC_IN);
            imgWatchLater.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.default_text), android.graphics.PorterDuff.Mode.SRC_IN);
            imgHelp.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.default_text), android.graphics.PorterDuff.Mode.SRC_IN);
            imgSignout.setColorFilter(ContextCompat.getColor(MoreActivity.this, R.color.default_text), android.graphics.PorterDuff.Mode.SRC_IN);

            txtProfile.setTextColor(getResources().getColor(R.color.default_text));
            txtSubscription.setTextColor(getResources().getColor(R.color.default_text));
            txtWatchLater.setTextColor(getResources().getColor(R.color.default_text));
            txtHelp.setTextColor(getResources().getColor(R.color.default_text));
            txtSignout.setTextColor(getResources().getColor(R.color.default_text));

            lnr_userdata.setVisibility(View.GONE);
            relNotLogin.setVisibility(View.VISIBLE);
        }

        //set data and list adapter
//        mAdapter = new NavigationAdapter(this, list, navMenuStyle);
//        recyclerView.setAdapter(mAdapter);

//        final NavigationAdapter.OriginalViewHolder[] viewHolder = {null};

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreActivity.this, ProfileActivity.class);
                intent.putExtra("from", "more");
                startActivity(intent);
            }
        });


        planstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreActivity.this, SubscriptionActivity.class);
                startActivity(intent);
            }
        });


        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreActivity.this, MainActivity.class);

                startActivity(intent);
            }
        });

        lnr_buyPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status) {
                    startActivity(new Intent(MoreActivity.this, SubscriptionActivity.class));
                } else {
                    startActivity(new Intent(MoreActivity.this, LoginActivity.class));
                }
            }
        });


//        mAdapter.setOnItemClickListener(new NavigationAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, NavigationModel obj, int position, NavigationAdapter.OriginalViewHolder holder) {
//
//                //----------------------action for click items nav---------------------
//
//                if (position == 0) {
//                    Intent intent = new Intent(MoreActivity.this, ProfileActivity.class);
//                    intent.putExtra("from", "more");
//                    startActivity(intent);
//                } else {
//                    if (status) {
//                      /*  if (position == 1) {
//                            startActivity(new Intent(MoreActivity.this, MyTransactionActivity.class));
//                        } else*/
//                        if (position == 1) {
//
//                            startActivity(new Intent(MoreActivity.this, SubscriptionActivity.class));
//                        }
//                       /* if (position == 2) {
//                            Intent intent = new Intent(MoreActivity.this, ApplyPromoCodeActivity.class);
//                            startActivity(intent);
//                        }*/ /*else if (position == 2) {
//                            Intent intent = new Intent(MoreActivity.this, DownloadActivity.class);
//                            startActivity(intent);
//                        }*/
//                        else if (position == 2) {
//                            Intent intent = new Intent(MoreActivity.this, SettingsActivity.class);
//                            startActivity(intent);
//                        } else if (position == 3) {
//                            Intent intent = new Intent(MoreActivity.this, HelpActivity.class);
//                            startActivity(intent);
//                        } else if (position == 4) {
//                            Intent intent = new Intent(MoreActivity.this, TermsActivity.class);
//                            intent.putExtra("from", "more");
//                            startActivity(intent);
//                        } else if (position == 5) {
//
//                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MoreActivity.this);
//
//                            LayoutInflater inflater = MoreActivity.this.getLayoutInflater();
//                            View dialogView = inflater.inflate(R.layout.logout_dialog_layout, null);
//                            dialogBuilder.setView(dialogView);
//                            TextView txt_cancel = dialogView.findViewById(R.id.txt_cancel);
//                            TextView txt_logout = dialogView.findViewById(R.id.txt_logout);
//                            AlertDialog alertDialog = dialogBuilder.create();
//
//                            txt_logout.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    logoutUser();
//                                    alertDialog.cancel();
//                                }
//                            });
//
//                            txt_cancel.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    alertDialog.cancel();
//                                }
//                            });
//
//
//                            alertDialog.show();
//                        }
//
//                    } /*else {
//                        if (position == 6) {
//                            Intent intent = new Intent(MoreActivity.this, LoginActivity.class);
//                            startActivity(intent);
//                        } else if (position == 7) {
//                            Intent intent = new Intent(MoreActivity.this, SettingsActivity.class);
//                            startActivity(intent);
//                        }
//
//                    }*/
//
//                }
//
//
//                //----behaviour of bg nav items-----------------
//               /* if (!obj.getTitle().equals("Settings") && !obj.getTitle().equals("Login") && !obj.getTitle().equals("Sign Out")) {
//
//                    if (isDark) {
//                        mAdapter.chanColor(viewHolder[0], position, R.color.nav_bg);
//                    } else {
//                        mAdapter.chanColor(viewHolder[0], position, R.color.white);
//                    }
//
//
//                    if (navMenuStyle.equals("grid")) {
//                        holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
//                        holder.name.setTextColor(getResources().getColor(R.color.white));
//                    } else {
//                        holder.selectedLayout.setBackground(getResources().getDrawable(R.drawable.round_grey_transparent));
//                        holder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
//                    }
//
//                    viewHolder[0] = holder;
//                }*/
//
//
//            }
//        });

        //----external method call--------------
//        loadFragment(new MainHomeFragment());

        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
                    editor.putBoolean("dark", true);
                    editor.apply();

                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
                    editor.putBoolean("dark", false);
                    editor.apply();
                }

//                mDrawerLayout.closeDrawers();
                startActivity(new Intent(MoreActivity.this, MoreActivity.class));
                finish();
            }
        });

        textLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MoreActivity.this);

                LayoutInflater inflater = MoreActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.logout_dialog_layout, null);
                dialogBuilder.setView(dialogView);
                TextView txt_cancel = dialogView.findViewById(R.id.txt_cancel);
                TextView txt_logout = dialogView.findViewById(R.id.txt_logout);
                AlertDialog alertDialog = dialogBuilder.create();

                txt_logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logoutUser();
                        alertDialog.cancel();
                    }
                });

                txt_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }
                });


                alertDialog.show();

//                new MaterialAlertDialogBuilder(MoreActivity.this, R.layout.logout_dialog_layout)
//                        .setMessage("Are you sure to logout ?")
//                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                logoutUser();
//                            }
//                        })
//                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        }).create().show();
            }
        });

    }

    private void getActiveSubscriptionFromDatabase() {
        DatabaseHelper db = new DatabaseHelper(MoreActivity.this);
        if (db.getActiveStatusCount() > 0 && db.getUserDataCount() > 0) {
            //   activePlanLayout.setVisibility(View.VISIBLE);
            ActiveStatus activeStatus = db.getActiveStatusData();
            User user = db.getUserData();
           /* activeUserName.setText(user.getName());
            activeEmail.setText(user.getEmail());*/
            planname.setText(activeStatus.getPackageTitle() + " " + activeStatus.getExpireDate());
            //    activeExpireDate.setText(activeStatus.getExpireDate());

        } else {
            // activePlanLayout.setVisibility(View.GONE);
        }

    }

    private void getUserProfileData(String uid) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        UserDataApi api = retrofit.create(UserDataApi.class);
        Call<User> call = api.getUserData(AppConfig.API_KEY, uid);
        call.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        User user = response.body();
                        String userProfileStatus = user.getProfile_status();

                        if (userProfileStatus != null) {
                            if (userProfileStatus.equals("0")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MoreActivity.this);
//                            builder.setMessage("Update your profile for better service.")
                                builder.setMessage("Verify your mobile number for better service.")
                                        .setCancelable(false)
                                        .setNegativeButton("Cancel", (dialog, id) -> {
                                            finishAffinity();
                                            dialog.dismiss();
                                        })
                                        .setPositiveButton("OK", (dialog1, id) -> {
                                            dialog1.dismiss();
                                            Intent intent = new Intent(MoreActivity.this, ProfileActivity.class);
                                            intent.putExtra("from", "main");
                                            startActivity(intent);
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
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

                        SharedPreferences.Editor editor = getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
                        editor.putBoolean(Constants.USER_LOGIN_STATUS, false);
                        editor.apply();
                        editor.commit();

                        DatabaseHelper databaseHelper = new DatabaseHelper(MoreActivity.this);
                        databaseHelper.deleteUserData();

                        PreferenceUtils.clearSubscriptionSavedData(MoreActivity.this);

                        Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        new ToastMsg(MoreActivity.this).toastIconError(response.body().getData());

                    }
                } else {

                    new ToastMsg(MoreActivity.this).toastIconError(getString(R.string.error_toast));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                new ToastMsg(MoreActivity.this).toastIconError(getString(R.string.error_toast));
            }
        });
    }

    private void getSubscriptionHistory() {


        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);
        Call<SubscriptionHistory> call = subscriptionApi.getSubscriptionHistory(AppConfig.API_KEY, userId);
        call.enqueue(new Callback<SubscriptionHistory>() {
            @Override
            public void onResponse(Call<SubscriptionHistory> call, Response<SubscriptionHistory> response) {
                SubscriptionHistory subscriptionHistory = response.body();
                if (response.code() == 200) {


                  /*  shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);*/

                    activeSubscriptions = subscriptionHistory.getActiveSubscription();
                    if (subscriptionHistory.getActiveSubscription().size() > 0) {


                        txt_premium.setText("Premium Member");
                        lnr_buyPlan.setVisibility(View.GONE);
                        planstatus.setText("Subscribed");
                        // planname.setText(subscriptionHistory.getActiveSubscription().get(0).getPlanTitle());
                        planstatus.setBackgroundColor(getResources().getColor(R.color.green));

                    } else {
                        txt_premium.setText("Non-Premium Member");
                        lnr_buyPlan.setVisibility(View.VISIBLE);
                        planstatus.setText("Subscribe Now");
                        // planname.setText(subscriptionHistory.getActiveSubscription().get(0).getPlanTitle());
                        planstatus.setBackgroundColor(getResources().getColor(R.color.black));
                        // planname.setVisibility(View.GONE);

                    }

                    /*if (subscriptionHistory.getInactiveSubscription().size() > 0) {
                        mNoHistoryTv.setVisibility(View.GONE);
                        mSubHistoryLayout.setVisibility(View.VISIBLE);
                        inactiveSubscriptionAdapter = new InactiveSubscriptionAdapter(subscriptionHistory.getInactiveSubscription(),
                                SubscriptionActivity.this);
                        mInactiveRv.setAdapter(inactiveSubscriptionAdapter);

                    } else {
                        mNoHistoryTv.setVisibility(View.VISIBLE);
                        mSubHistoryLayout.setVisibility(View.GONE);
                    }*/

                /*    if (subscriptionHistory.getActiveSubscription().size() > 0) {
                        mNoHistoryTv.setVisibility(View.GONE);
                        mSubHistoryLayout.setVisibility(View.VISIBLE);
                        inactiveSubscriptionAdapter = new InactiveSubscriptionAdapter(subscriptionHistory.getActiveSubscription(),
                                SubscriptionActivity.this);
                        mInactiveRv.setAdapter(inactiveSubscriptionAdapter);

                    } else {
                        mNoHistoryTv.setVisibility(View.VISIBLE);
                        mSubHistoryLayout.setVisibility(View.GONE);
                    }
                    progressBar.setVisibility(View.GONE);*/
                }
            }

            @Override
            public void onFailure(Call<SubscriptionHistory> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });

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
        id = PreferenceUtils.getUserId(MoreActivity.this);
        if (PreferenceUtils.isLoggedIn(MoreActivity.this)) {
            getProfile();
            getUserProfileData(id);
        } else {

        }
    }

    private void getProfile() {

        showLoader();

        progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        UserDataApi api = retrofit.create(UserDataApi.class);
        Call<User> call = api.getUserData(AppConfig.API_KEY, id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        progressBar.setVisibility(View.GONE);
                        User user = response.body();
                       /* Picasso.get()
                                .load(user.getImageUrl())
                                .placeholder(R.drawable.ic_account_circle_black)
                                .error(R.drawable.ic_account_circle_black)
                                .into(userIv);*/

                     /*   Glide.with(MoreActivity.this)
                                .load(user.getImageUrl())
                                .into(imageAvtar);
*/


                        Glide.with(MoreActivity.this)
                                //.load(getResources().getDrawable(R.drawable.ppapplogo))
//                                .load(getResources().getDrawable(R.drawable.useraccount))
                                .load(getResources().getDrawable(R.drawable.ic_baseline_account_circle_24))
                                .into(imageAvtar);

                        textName.setText(user.getName());
                        if (user.getCountry_code() != null) {
                            txtmobile.setText(String.format("%s%s", user.getCountry_code(), user.getPhone()));
                        } else {
                            txtmobile.setText(user.getPhone());
                        }
                        textEmail.setText(user.getEmail());

                       /* cv.setVisibility(View.VISIBLE);
                        editProfile.setVisibility(View.VISIBLE);*/
                        ll_profiledetails.setVisibility(View.VISIBLE);
                        planstatus.setVisibility(View.VISIBLE);
                        hideLoader();
                    }
                }
            }


            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });


    }

    private void showLoader() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideLoader() {
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void onProfileClick(View view) {
        if (status) {
            Intent intent = new Intent(MoreActivity.this, ProfileActivity.class);
            intent.putExtra("from", "more");
            startActivity(intent);
        }
    }

    public void onNotLoginClick(View view) {
        Intent intent = new Intent(MoreActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void onSubscriptionClick(View view) {
        if (status) {
            Intent intent = new Intent(MoreActivity.this, SubscriptionActivity.class);
            startActivity(intent);
        }
    }

    public void onWatchLaterClick(View view) {
        if (status) {
            Intent intent = new Intent(MoreActivity.this, FavActivity.class);
            startActivity(intent);
        }
    }

    public void onOnSupportClick(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + "8377003507"));
        startActivity(intent);

    }

    public void onSettingsClick(View view) {
        Intent intent = new Intent(MoreActivity.this, SettingsActivity.class);
        startActivity(intent);

    }

    public void onHelpClick(View view) {
        if (status) {
            Intent intent = new Intent(MoreActivity.this, HelpActivity.class);
            startActivity(intent);
        }

    }

    public void onRateUsClick(View view) {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

    }

    public void onPrivacyClick(View view) {
        Intent intent = new Intent(MoreActivity.this, TermsActivity.class);
        intent.putExtra("from", "privacy");
        intent.putExtra("url", "https://hunters.co.in/privacy-policy.php");
        intent.putExtra("title", "Privacy Policy");
        startActivity(intent);

    }

    public void onTermsClick(View view) {
        Intent intent = new Intent(MoreActivity.this, TermsActivity.class);
        intent.putExtra("from", "terms");
        intent.putExtra("url", "https://hunters.co.in/term-condition.php");
        intent.putExtra("title", "Terms & Condition");
        startActivity(intent);

    }

    public void onRefundClick(View view) {
        Intent intent = new Intent(MoreActivity.this, TermsActivity.class);
        intent.putExtra("from", "refund");
        intent.putExtra("url", "https://hunters.co.in/term-condition.php");
        intent.putExtra("title", "Refund Policy");
        startActivity(intent);

    }

    public void onSignOutClick(View view) {
        if (status) {
            new MaterialAlertDialogBuilder(MoreActivity.this)
                    .setMessage("Are you sure to logout ?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logoutUser();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();
        }

    }
}