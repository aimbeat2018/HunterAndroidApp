package ott.hunter.utils.ads;

import android.app.Activity;
import android.util.Log;

import ott.hunter.database.DatabaseHelper;
import ott.hunter.network.model.config.AdsConfig;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;/*
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;*/
import ott.hunter.utils.PreferenceUtils;
//import com.startapp.sdk.adsbase.StartAppAd;


public class PopUpAds {
//    static InterstitialAd mInterstitial;

    public static void ShowAdmobInterstitialAds(Activity context) {
        if (!PreferenceUtils.isActivePlan(context)) {
            AdsConfig adsConfig = new DatabaseHelper(context).getConfigurationData().getAdsConfig();
           /* AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(context, adsConfig.getAdmobInterstitialAdsId(), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull @NotNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    mInterstitial = interstitialAd;
                    mInterstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull @NotNull com.google.android.gms.ads.AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            mInterstitial = null;
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();
                            Log.e("Admob", "onAdShowedFullScreenContent: ");
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            mInterstitial = null;
                        }

                        @Override
                        public void onAdImpression() {
                            super.onAdImpression();
                            Log.e("Admob", "onAdImpression: ");
                        }
                    });
                    mInterstitial.show(context);
                }

                @Override
                public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mInterstitial = null;
                }
            });*/

        }
    }

    public static void showFANInterstitialAds(Activity context) {
        final String TAG = "FAN";
        if (!PreferenceUtils.isActivePlan(context)) {
            DatabaseHelper db = new DatabaseHelper(context);
            String placementId = db.getConfigurationData().getAdsConfig().getFanInterstitialAdsPlacementId();

            final com.facebook.ads.InterstitialAd interstitialAd = new com.facebook.ads.InterstitialAd(context, placementId);
            InterstitialAdListener listener = new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    // Interstitial ad displayed callback
                    Log.e(TAG, "Interstitial ad displayed.");
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    // Interstitial dismissed callback
                    Log.e(TAG, "Interstitial ad dismissed.");
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    // Ad error callback
                    Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Interstitial ad is loaded and ready to be displayed
                    Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                    // Show the ad
                    interstitialAd.show();
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Ad clicked callback
                    Log.d(TAG, "Interstitial ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Ad impression logged callback
                    Log.d(TAG, "Interstitial ad impression logged!");
                }
            };

            interstitialAd.loadAd(interstitialAd.buildLoadAdConfig()
                    .withAdListener(listener)
                    .build());
        }

    }

    public static void showStartappInterstitialAds(Activity context) {
        if (!PreferenceUtils.isActivePlan(context)) {
            String startAppAppId = new DatabaseHelper(context).getConfigurationData().getAdsConfig().getStartappAppId();
            //String developerId = "165678100";
            String developerId = new DatabaseHelper(context).getConfigurationData().getAdsConfig().getStartappDeveloperId();
            /*StartAppAd.init(context,developerId, startAppAppId);
            //startapp
            StartAppAd.showAd(context); // show the ad*/

        }
    }

}