package ott.hunter;

import static ott.hunter.MoreActivity.familycontent;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.branch.referral.Branch;
import okhttp3.ResponseBody;
import ott.hunter.adapters.NavigationAdapter;
import ott.hunter.database.DatabaseHelper;
import ott.hunter.firebaseservice.Config;
import ott.hunter.models.NavigationModel;
import ott.hunter.nav_fragments.MainHomeFragment;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.LoginApi;
import ott.hunter.network.apis.UserDataApi;
import ott.hunter.network.model.User;
import ott.hunter.reels.ReelsActivity;
import ott.hunter.utils.Constants;
import ott.hunter.utils.HelperUtils;
import ott.hunter.utils.PreferenceUtils;
import ott.hunter.utils.RtlUtils;
import ott.hunter.utils.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Serializable {
    //    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
//    private LinearLayout navHeaderLayout;

    private RecyclerView recyclerView;
    private NavigationAdapter mAdapter;
    private List<NavigationModel> list = new ArrayList<>();
    //    private NavigationView navigationView;
    private String[] navItemImage;

    private String[] navItemName2;
    private String[] navItemImage2;
    private boolean status = false;

    private FirebaseAnalytics mFirebaseAnalytics;
    public boolean isDark;
    private String navMenuStyle;

    private Switch themeSwitch;
    private final int PERMISSION_REQUEST_CODE = 100;
    private String searchType;
    private boolean[] selectedtype = new boolean[3]; // 0 for movie, 1 for series, 2 for live tv....
    private DatabaseHelper db;
    private boolean vpnStatus;
    private HelperUtils helperUtils;
    private final Handler handler = new Handler();
    Runnable mToastRunnable;
    String userProfileStatus = "";

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private static final String VERSION_CODE_KEY = "version_code";
    private AlertDialog updateDailog;
    CleverTapAPI clevertapDefaultInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RtlUtils.setScreenDirection(this);
        db = new DatabaseHelper(MainActivity.this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("dark", false);
        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //to  get user  city location
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        clevertapDefaultInstance.enableDeviceNetworkInfoReporting(true);
        //for debug  logcat
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);

        //check vpn connection
        helperUtils = new HelperUtils(MainActivity.this);
        vpnStatus = helperUtils.isVpnConnectionAvailable();
        if (vpnStatus) {
            helperUtils.showWarningDialog(MainActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
            return;
        }
        // To resolve cast button visibility problem. Check Cast State when app is open.
        CastContext castContext = CastContext.getSharedInstance(this);
        castContext.getCastState();

//        navMenuStyle = db.getConfigurationData().getAppConfig().getMenu();

        //---analytics-----------
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "main_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        if (sharedPreferences.getBoolean("firstTime", true)) {
            showTermServicesDialog();
        }


//for notification permission
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission();
        }


        //update subscription
        if (PreferenceUtils.isLoggedIn(MainActivity.this)) {
            PreferenceUtils.updateSubscriptionStatus(MainActivity.this);
        }

        //----external method call--------------
        loadFragment(new MainHomeFragment());

        if (!PreferenceUtils.okClicked) {
            getImage("");
        }
    }


    private boolean checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED/*
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED*/) {

//                Log.v(TAG, "Permission is granted");
                return true;

            } else {
//                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS/*, Manifest.permission.READ_EXTERNAL_STORAGE*/}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
//            Log.v(TAG, "Permission is granted");
            return true;
        }
    }


    private void initRemoteConfig() {
        if (!vpnStatus) {
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            HashMap<String, Object> firebaseDefaultMap = new HashMap<>();
            firebaseDefaultMap.put(VERSION_CODE_KEY, getCurrentVersionCode());
            mFirebaseRemoteConfig.setDefaultsAsync(firebaseDefaultMap);
            mFirebaseRemoteConfig.setConfigSettingsAsync(
                    new FirebaseRemoteConfigSettings.Builder()
                            .setMinimumFetchIntervalInSeconds(TimeUnit.HOURS.toSeconds(0))
                            .build());
            mFirebaseRemoteConfig.fetch().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    mFirebaseRemoteConfig.activate().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            final int latestAppVersion = (int) mFirebaseRemoteConfig.getDouble(VERSION_CODE_KEY);
                            runOnUiThread(() ->
                                    checkForUpdate(latestAppVersion));
                        }
                    });
                }
            });
        } else {
            helperUtils.showWarningDialog(MainActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
        }
    }

    private void checkForUpdate(int latestAppVersion) {
        int version = getCurrentVersionCode();
        if (latestAppVersion > version) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Update App");
            builder.setMessage("New Version Available On Play store.\n" + "Please Update Your App");
            builder.setPositiveButton("Update", (dialog, which) -> {
                goToPlayStore();
                updateDailog.dismiss();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                updateDailog.dismiss();
                finishAffinity();
            });
            // create and show the alert dialog
            updateDailog = builder.create();
            updateDailog.setCancelable(false);
            updateDailog.show();

        } else {
        }
    }

    private int getCurrentVersionCode() {
        int versionCode = 1;
        try {
            final PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                versionCode = (int) pInfo.getLongVersionCode();
            } else {
                versionCode = pInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            //log exception
        }
        return versionCode;
    }

    private void goToPlayStore() {
        try {
            Intent appStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
            appStoreIntent.setPackage("com.android.vending");
            startActivity(appStoreIntent);
        } catch (android.content.ActivityNotFoundException exception) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        return true;
    }


    private boolean loadFragment(Fragment fragment) {

        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_search:

                final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {

                        Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
                        intent.putExtra("q", s);
                        startActivity(intent);

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                });

                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(MainActivity.this).setMessage("Do you want to exit ?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finishAffinity();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();

//        }
    }

    //----nav menu item click---------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // set item as selected to persist highlight
        menuItem.setChecked(true);
        return true;
    }

    private void showTermServicesDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_term_of_services);
        dialog.setCancelable(true);

        dialog.show();


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        WebView webView = dialog.findViewById(R.id.webView);
        Button declineBt = dialog.findViewById(R.id.bt_decline);
        Button acceptBt = dialog.findViewById(R.id.bt_accept);
        //populate webView with data
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        webView.loadUrl(AppConfig.TERMS_URL);

        if (isDark) {
            declineBt.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey_outline));
            acceptBt.setBackground(getResources().getDrawable(R.drawable.btn_rounded_dark));
        }

        dialog.findViewById(R.id.bt_close).setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        acceptBt.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
            editor.putBoolean("firstTime", false);
            editor.apply();
            dialog.dismiss();

            if (PreferenceUtils.getUserId(MainActivity.this) != null) {
                getUserProfileData(PreferenceUtils.getUserId(MainActivity.this));
            }

        });

        declineBt.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });


    }

    public void goToSearchActivity() {
        startActivity(new Intent(MainActivity.this, SearchActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check vpn connection
        helperUtils = new HelperUtils(MainActivity.this);
        vpnStatus = helperUtils.isVpnConnectionAvailable();
        if (vpnStatus) {
            helperUtils.showWarningDialog(MainActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
        }


        if (PreferenceUtils.getUserId(MainActivity.this) == null || PreferenceUtils.getUserId(MainActivity.this).equals("")) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseAuth.getInstance().signOut();
            }

            SharedPreferences.Editor editor = getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
            editor.putBoolean(Constants.USER_LOGIN_STATUS, false);
            editor.apply();
            editor.commit();

            DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
            databaseHelper.deleteUserData();

            PreferenceUtils.clearSubscriptionSavedData(MainActivity.this);

//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//            finish();
        } else {
            getProfile(PreferenceUtils.getUserId(MainActivity.this));
        }

    }

    private void doTheAutoRefresh() {
        mToastRunnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 3000);
                getProfile(PreferenceUtils.getUserId(MainActivity.this));
            }
        };
        mToastRunnable.run();

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        handler.removeCallbacks(mToastRunnable);
//    }

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
                        if (user != null)
                            if (user.getLogout_status().equals("1")) {
                                String deviceNoDynamic = user.getDevice_no();
                                String deviceNo = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                                if (deviceNoDynamic != null) {
                                    if (!deviceNoDynamic.equals("")) {
                                        if (!deviceNo.equals(deviceNoDynamic)) {
                                            Toast.makeText(MainActivity.this, "Logged in other device", Toast.LENGTH_SHORT).show();
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


        SharedPreferences sharedPreferences = getSharedPreferences(Constants.FAMILYCONTENTSTATUS, MODE_PRIVATE);
        familycontent = sharedPreferences.getBoolean("familycontent", false);
        if (familycontent == false) {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.new_update_dialog_layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setDimAmount(0.3F);

        AppCompatImageView img_image = dialog.findViewById(R.id.img_image);
        Glide.with(MainActivity.this).load(image).into(img_image);

        CardView card_cancel = dialog.findViewById(R.id.card_cancel);
        card_cancel.setOnClickListener(v -> {
            dialog.dismiss();

            PreferenceUtils.okClicked = true;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            String currentDate = sdf.format(new Date());
            SharedPreferences sharedPref = getSharedPreferences("dialog", 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("LAST_LAUNCH_DATE", currentDate);
            editor.commit();
        });

        dialog.show();
        }
    }

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

                        SharedPreferences.Editor editor = getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
                        editor.putBoolean(Constants.USER_LOGIN_STATUS, false);
                        editor.apply();
                        editor.commit();

                        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                        databaseHelper.deleteUserData();

                        PreferenceUtils.clearSubscriptionSavedData(MainActivity.this);

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        new ToastMsg(MainActivity.this).toastIconError(response.body().getData());

                    }
                } else {

                    new ToastMsg(MainActivity.this).toastIconError(getString(R.string.error_toast));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                new ToastMsg(MainActivity.this).toastIconError(getString(R.string.error_toast));
            }
        });
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
                        userProfileStatus = user.getProfile_status();

                        if (userProfileStatus != null)
                            if (userProfileStatus.equals("0")) {
                                Toast.makeText(MainActivity.this, "Verify your mobile number for better service.", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                intent.putExtra("from", "main");
                                startActivity(intent);

                            }
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });


    }
}
