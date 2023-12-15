package ott.hunter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import io.branch.referral.Branch;
import ott.hunter.database.DatabaseHelper;
import ott.hunter.firebaseservice.Config;
import ott.hunter.network.apis.ConfigurationApi;
import ott.hunter.network.model.config.ApkUpdateInfo;
import ott.hunter.network.model.config.Configuration;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
//import com.splunk.mint.Mint;

import org.json.JSONException;

import ott.hunter.R;
import ott.hunter.network.RetrofitClient;
import ott.hunter.reels.ReelsActivity;
import ott.hunter.utils.HelperUtils;
import ott.hunter.utils.PreferenceUtils;
import ott.hunter.utils.ApiResources;
import ott.hunter.utils.Constants;
import ott.hunter.utils.ToastMsg;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreen";
    private final int PERMISSION_REQUEST_CODE = 100;
    private int SPLASH_TIME = 1000;
    private Thread timer;
    private DatabaseHelper db;
    private boolean isRestricted = false;
    private boolean isUpdate = false;
    private boolean vpnStatus = false;
    private HelperUtils helperUtils;
    public boolean isDark;
    ImageView img_logo;


    VideoView myvideoview;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private static final String VERSION_CODE_KEY = "version_code";
    private AlertDialog updateDailog;
//    MediaPlayer mp;

    @Override
    protected void onStart() {
        super.onStart();
//        //check any restricted app is available or not
//       /* ApplicationInfo restrictedApp = helperUtils.getRestrictApp();
//        if (restrictedApp != null){
//            boolean isOpenInBackground = helperUtils.isForeground(restrictedApp.packageName);
//            if (isOpenInBackground){
//                Log.e(TAG, restrictedApp.loadLabel(this.getPackageManager()).toString() + ", is open in background.");
//            }else {
//                Log.e(TAG, "No restricted app is running in background.");
//            }
//        }else {
//            Log.e(TAG, "No restricted app installed!!");
//        }*/

        Branch branch = Branch.getInstance();


        // Branch init
        branch.initSession((referringParams, error) -> {
            if (error == null) {
                Log.i("BRANCH SDK1", referringParams.toString());
                try {
                    boolean isLinkClicked = referringParams.getBoolean("+clicked_branch_link");
                    Log.d(TAG, "onStart:is link clicked   " + isLinkClicked);

                    if (isLinkClicked) {
                        String branchData = referringParams.getString(Config.DATA);
//                            type = referringParams.getString(Const.TYPE);
//                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
//
//                                Intent intent = new Intent(SplashScreenActivity.this, ReelsListActivity.class);
//                                intent.putExtra("reelId", branchData);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                startActivity(intent);
//                                finish();
//                            }, SPLASH_TIME);


                        Log.d(TAG, "onStart:is link clicked111  " + branchData);

                        Intent intent = new Intent(SplashScreenActivity.this, ReelsActivity.class);
                        intent.putExtra("reelId", branchData);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d(TAG, "onStart:is link clicked111 in else");
//                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
//
//                                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                startActivity(intent);
//                                finish();
//                            }, SPLASH_TIME);

                        getConfigurationData();
//                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        startActivity(intent);
//                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } else {
                Log.i("BRANCH SDK2", error.getMessage());
                Log.d(TAG, "onStart:is link clicked111 in error " + error.getMessage());

                getConfigurationData();

//                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivity(intent);
//                finish();
            }
        }, this.getIntent().getData(), this);

        //check VPN connection is set or not
        vpnStatus = new HelperUtils(SplashScreenActivity.this).isVpnConnectionAvailable();
        //belo line test for firebase crashlist report
        //   throw new RuntimeException("Test Crash"); // Force a crash

//        printHashKey(this);

    }


    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        setContentView(R.layout.activity_splashscreen);
        myvideoview = findViewById(R.id.myvideoview);

        db = new DatabaseHelper(SplashScreenActivity.this);
        helperUtils = new HelperUtils(SplashScreenActivity.this);
        vpnStatus = new HelperUtils(SplashScreenActivity.this).isVpnConnectionAvailable();


//        try {
//            VideoView myvideoview = findViewById(R.id.myvideoview);
////            myvideoview.setLayoutParams(new LinearLayout.LayoutParams(getDisplayWidthPixels(getApplicationContext()) + 500, getDisplayHeightPixels(getApplicationContext()) + 200));
//            Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro);
//            myvideoview.setVideoURI(video);
//
//            myvideoview.setOnCompletionListener(mp -> getConfigurationData());
//            myvideoview.start();
//        } catch (Exception ex) {
//        getConfigurationData();
//        }

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
            helperUtils.showWarningDialog(SplashScreenActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
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


/*

//added splash video
            Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.huntersplash);
            myvideoview.setVideoURI(video);

            myvideoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {

                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                }
            });
            myvideoview.start();
*/


           Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            //Intent intent = new Intent(SplashScreenActivity.this, ChooseLanguageActivity.class);
            intent.putExtra("from", "splash");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();

//            Branch.sessionBuilder(this).withCallback((referringParams, error) -> {
//                if (error == null) {
//                    Log.i("BRANCH SDK", referringParams.toString());
//
//                    try {
//                        boolean isLinkClicked = referringParams.getBoolean("+clicked_branch_link");
//                        Log.d(TAG, "onStart:is link clicked   " + isLinkClicked);
//
//                        if (isLinkClicked) {
//                            String branchData = referringParams.getString(Config.DATA);
////                            type = referringParams.getString(Const.TYPE);
////                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
////
////                                Intent intent = new Intent(SplashScreenActivity.this, ReelsListActivity.class);
////                                intent.putExtra("reelId", branchData);
////                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
////                                startActivity(intent);
////                                finish();
////                            }, SPLASH_TIME);
//
//                            Intent intent = new Intent(SplashScreenActivity.this, ReelsActivity.class);
//                            intent.putExtra("reelId", branchData);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                            startActivity(intent);
//                            finish();
//                        } else {
////                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
////
////                                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
////                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
////                                startActivity(intent);
////                                finish();
////                            }, SPLASH_TIME);
//
//
//                        }
//
//                    } catch (JSONException e) {
//                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        startActivity(intent);
//                        finish();
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    Log.i("BRANCH SDK", error.getMessage());
//                    Log.e("DeepLink", "Error initializing Branch session: " + error.getMessage());
//
////                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
////                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
////                    startActivity(intent);
////                    finish();
//
//                }
//            }).withData(this.getIntent().getData()).init();
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

    public boolean isLoginMandatory() {
        return db.getConfigurationData().getAppConfig().getMandatoryLogin();
    }

    public void getConfigurationData() {
        if (!vpnStatus) {
            Retrofit retrofit = RetrofitClient.getRetrofitInstance();
            ConfigurationApi api = retrofit.create(ConfigurationApi.class);
            Call<Configuration> call = api.getConfigurationData(AppConfig.API_KEY);
            call.enqueue(new Callback<Configuration>() {
                @Override

                public void onResponse(Call<Configuration> call, Response<Configuration> response) {
                    if (response.code() == 200) {
                        Configuration configuration = response.body();
                        if (configuration != null) {
                            configuration.setId(1);

                            ApiResources.CURRENCY = configuration.getPaymentConfig().getCurrency();
                            ApiResources.PAYPAL_CLIENT_ID = configuration.getPaymentConfig().getPaypalClientId();
                            ApiResources.EXCHSNGE_RATE = configuration.getPaymentConfig().getExchangeRate();
                            ApiResources.RAZORPAY_EXCHANGE_RATE = configuration.getPaymentConfig().getRazorpayExchangeRate();
                            //save genre, country and tv category list to constants
                            Constants.genreList = configuration.getGenre();
                            Constants.countryList = configuration.getCountry();
                            Constants.tvCategoryList = configuration.getTvCategory();

                            db.deleteAllDownloadData();
                            //db.deleteAllAppConfig();
                            if (db.getConfigurationCount() != 1) {
                                db.deleteAllAppConfig();
                                //db.insertConfigurationData(configuration);
                                db.insertConfigurationData(configuration);
                            }
                            //db.updateConfigurationData(configuration, 1);
                            db.updateConfigurationData(configuration, 1);

                            //apk update check
                            if (isNeedUpdate(configuration.getApkUpdateInfo().getVersionCode())) {
                                showAppUpdateDialog(configuration.getApkUpdateInfo());
                                return;
                            }

                            initRemoteConfig();

//                            if (db.getConfigurationData() != null) {
//                                timer.start();
//                            } else {
//                                showErrorDialog(getString(R.string.error_toast), getString(R.string.no_configuration_data_found));
//                            }
                        } else {
                            showErrorDialog(getString(R.string.error_toast), getString(R.string.failed_to_communicate));
                        }
                    } else {
                        showErrorDialog(getString(R.string.error_toast), getString(R.string.failed_to_communicate));
                    }
                }

                @Override
                public void onFailure(Call<Configuration> call, Throwable t) {
                    Log.e("ConfigError", t.getLocalizedMessage());

//                    Toast.makeText(SplashScreenActivity.this, "no internet", Toast.LENGTH_SHORT).show();
//                    showErrorDialogForNoInternet(getString(R.string.error_toast), getString(R.string.failed_to_communicate));
                    Intent intent = new Intent(SplashScreenActivity.this, NoInternet.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            helperUtils.showWarningDialog(SplashScreenActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));

        }
    }

    private void showAppUpdateDialog(final ApkUpdateInfo info) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("New version: " + info.getVersionName())
                .setMessage(info.getWhatsNew())
                .setPositiveButton("Update Now", (dialog, which) -> {
                    //update clicked
                    dialog.dismiss();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.getApkUrl()));
                    startActivity(browserIntent);
                    finish();
                })
                .setNegativeButton("Later", (dialog, which) -> {
                    //exit clicked
                    if (info.isSkipable()) {
                        if (db.getConfigurationData() != null) {
                            timer.start();
                        } else {
                            new ToastMsg(SplashScreenActivity.this).toastIconError(getString(R.string.error_toast));
                            finish();
                        }
                    } else {
                        System.exit(0);
                    }
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }

    private void showErrorDialog(String title, String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                        finish();
                    }
                })
                .show();
    }

    private void showErrorDialogForNoInternet(String title, String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(SplashScreenActivity.this, NoInternet.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }


    private boolean isNeedUpdate(String versionCode) {
        return Integer.parseInt(versionCode) > BuildConfig.VERSION_CODE;
    }

    // ------------------ checking storage permission ------------
    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                Log.v(TAG, "Permission is granted");
                return true;

            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            //resume tasks needing this permission
            getConfigurationData();

//            initRemoteConfig();
        }
    }

    public static void createKeyHash(Activity activity, String yourPackage) {
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(yourPackage, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        vpnStatus = helperUtils.isVpnConnectionAvailable();
        if (vpnStatus) {
            helperUtils.showWarningDialog(SplashScreenActivity.this, getString(R.string.vpn_detected), getString(R.string.close_vpn));
        }


//        initRemoteConfig();
    }
}
