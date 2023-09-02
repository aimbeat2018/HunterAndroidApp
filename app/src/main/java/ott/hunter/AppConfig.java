package ott.hunter;

public class AppConfig {

    static {
        System.loadLibrary("api_config");
    }

    public static native String getApiServerUrl();

    public static native String getApiKey();

    public static native String getPurchaseCode();

    public static final String API_SERVER_URL = getApiServerUrl();
    public static final String API_KEY = getApiKey();
    //copy your terms url from php admin dashboard & paste below
    //public static final String TERMS_URL                = "https://streamcineprime.co.in/cineprime_adminpanel/terms/";
    public static final String TERMS_URL = "https://hunters.co.in/webworld_backoffice/terms/";

    //paypal payment status
    public static final boolean PAYPAL_ACCOUNT_LIVE = true;

    // download option for non subscribed user
    public static final boolean ENABLE_DOWNLOAD_TO_ALL = true;

    //enable RTL
    public static boolean ENABLE_RTL = false;

    //enable external player
    public static final boolean ENABLE_EXTERNAL_PLAYER = false;

    //default theme
    public static boolean DEFAULT_DARK_THEME_ENABLE = true;

    // First, you have to configure firebase to enable facebook, phone and google login
    // facebook authentication
    public static final boolean ENABLE_FACEBOOK_LOGIN = true;

    //Phone authentication
    public static final boolean ENABLE_PHONE_LOGIN = true;

    //Google authentication
    public static final boolean ENABLE_GOOGLE_LOGIN = true;
}
