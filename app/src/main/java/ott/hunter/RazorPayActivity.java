package ott.hunter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUPaymentParams;
import com.payu.base.models.PaymentMode;
import com.payu.base.models.PaymentType;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.models.PayUCheckoutProConfig;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentResultListener;
import com.razorpay.RazorpayClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Key;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.ResponseBody;
import ott.hunter.database.DatabaseHelper;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.OrderEntryResponse;
import ott.hunter.network.apis.PassResetApi;
import ott.hunter.network.apis.PaymentApi;
import ott.hunter.network.apis.PaymentGatewayApi;
import ott.hunter.network.apis.SubscriptionApi;
import ott.hunter.network.model.ActiveStatus;
import ott.hunter.network.model.Package;
import ott.hunter.network.model.User;
import ott.hunter.network.model.config.PaymentConfig;
import ott.hunter.utils.ApiResources;
import ott.hunter.utils.Constants;
import ott.hunter.utils.PreferenceUtils;
import ott.hunter.utils.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RazorPayActivity extends AppCompatActivity implements PaymentResultListener {
    private static final String TAG = "RazorPayActivity";
    private Package aPackage;
    private DatabaseHelper databaseHelper;
    private ProgressBar progressBar;
    private String amountPaidInRupee = "", orderIdstr;
    String paytmOrderId = "";

    TextView package_name, package_validity, price, txt_txn_id, txt_falied_reason;
    CardView card_paytm, card_payuMoney, card_cashfree;
    LinearLayout lnr_success, lnr_failed;
    String str_user_age = "";

    private long mLastClickTime;
    User user;
    String hashServer = "";
    //    private final String prodKey = "3TnMpV";
//    private final String prodSalt = "g0nGFe03";
    String from = "";

    private final String prodKey = "eG9W7f";
    private final String prodSalt = "9GWyxoLr38w5H70K3mhqzDuy4xLdOFEr";

    /*Google pay*/
    private static final int TEZ_REQUEST_CODE = 123;

    private static final String GOOGLE_TEZ_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_razor_pay);


        try {
            //  Block of code to try
            SharedPreferences sharedPreferences = RazorPayActivity.this.getSharedPreferences(Constants.USER_AGE, MODE_PRIVATE);
            str_user_age = sharedPreferences.getString("user_age", "20");

        } catch (Exception e) {
            e.printStackTrace();
        }

        progressBar = findViewById(R.id.progress_bar);
        aPackage = (Package) getIntent().getSerializableExtra("package");
        from = getIntent().getStringExtra("from");
        init();
        databaseHelper = new DatabaseHelper(this);
        //Checkout.preload(getApplicationContext());
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//
//        StrictMode.setThreadPolicy(policy);
//
        onClick();
//        startPayment();

        Random r = new Random();
        int random = r.nextInt(45 - 28) + 28;

        paytmOrderId = "ORDERID_" + random;

        if (from.equals("payu"))
            payUMoneyGateway();
        else if (from.equals("razor"))
            startPayment();
        else if (from.equals("gpay"))
            startGPayPayment();

    }

    private void init() {
        package_name = findViewById(R.id.package_name);
        package_validity = findViewById(R.id.package_validity);
        price = findViewById(R.id.price);
        card_paytm = findViewById(R.id.card_paytm);
        card_payuMoney = findViewById(R.id.card_payuMoney);
        card_cashfree = findViewById(R.id.card_cashfree);
        lnr_success = findViewById(R.id.lnr_success);
        lnr_failed = findViewById(R.id.lnr_failed);
        txt_txn_id = findViewById(R.id.txt_txn_id);
        txt_falied_reason = findViewById(R.id.txt_falied_reason);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPaymentGatewayStatus();
    }

    public void onClick() {
        card_payuMoney.setOnClickListener(view -> payUMoneyGateway());
    }

    public void startGPayPayment(){
        Uri uri = new Uri.Builder()
                        .scheme("upi")
                        .authority("pay")
                //.appendQueryParameter("pa", "webworld7.09@cmsidfc")
                        .appendQueryParameter("pa", "eazypay.2q3bqj0hfyl3m3m@icici")
                        .appendQueryParameter("pn", "WEBWORLD MULTIMEDIA LLP")
                        .appendQueryParameter("tr", paytmOrderId)
                        .appendQueryParameter("tn", aPackage.getName())
                        .appendQueryParameter("am", aPackage.getPrice())
                        .appendQueryParameter("cu", "INR")
                        .appendQueryParameter("url", "https://hunters.co.in")
                        .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage(GOOGLE_TEZ_PACKAGE_NAME);
        gPayActivityResultLauncher.launch(intent);
//        startActivityForResult(intent, TEZ_REQUEST_CODE);
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> gPayActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    /*failure*/
                    String status = data.getStringExtra("Status");

                    if(status.equals("FAILURE")){
                        lnr_failed.setVisibility(View.VISIBLE);
                        lnr_success.setVisibility(View.GONE);
                        txt_txn_id.setText(status);

                        new Handler().postDelayed(this::finish,1000);

                    }else if(status.equals("COMPLETED") || status.equals("SUCCESS")){
                        lnr_success.setVisibility(View.VISIBLE);
                        lnr_failed.setVisibility(View.GONE);

//                        txt_txn_id.setText("Transaction Id : " + paytmOrderId);
                        saveChargeData(paytmOrderId, "googlepay");
                    }
                    Toast.makeText(RazorPayActivity.this, "payment status = "+status, Toast.LENGTH_SHORT).show();
                }
            });


    public void startPayment() {
        PaymentConfig config = databaseHelper.getConfigurationData().getPaymentConfig();
        User user = databaseHelper.getUserData();

        double big = Double.valueOf(aPackage.getPrice());
        String price = aPackage.getPrice();
        int amount = (int) (big) * 100;
        String payamt = String.valueOf(amount);

        RazorpayClient razorpay = null;
        try {
            razorpay = new RazorpayClient(config.getRazorpayKeyId(), config.getRazorpayKeySecret());

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", Integer.parseInt(payamt)); // amount in the smallest currency unit
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_rcptid_11");

            Order order = razorpay.Orders.create(orderRequest);
            orderIdstr = order.get("id");
            Log.d("Order DATA", "" + order);


            String userid = user.getUserId();
            order_entry(userid);

        } catch (Exception e) {
            e.printStackTrace();
        }

        final Activity activity = this;
        Checkout checkout = new Checkout();
        checkout.setKeyID(config.getRazorpayKeyId());
        checkout.setImage(R.drawable.ppapplogo);

        JSONObject options = new JSONObject();
        try {
            options.put("name", user.getName());
            options.put("description", getString(R.string.app_name));
            options.put("order_id", orderIdstr);//from response of step 3.
            options.put("currency", "INR");
            options.put("amount", payamt);//pass amount in currency subunits
            options.put("prefill.email", user.getEmail());
            options.put("prefill.contact", user.getPhone());
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

            Log.e(TAG, config.getCurrency());
            Log.e(TAG, currencyConvert(config.getCurrency(), aPackage.getPrice(), ApiResources.RAZORPAY_EXCHANGE_RATE));
        } catch (Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    private PayUPaymentParams preparePayUBizParams() {
        HashMap<String, Object> additionalParams = new HashMap<>();
        additionalParams.put(PayUCheckoutProConstants.CP_UDF1, "udf1");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF2, "udf2");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF3, "udf3");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF4, "udf4");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF5, "udf5");

        user = databaseHelper.getUserData();

        PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
        double amount = Double.valueOf(aPackage.getPrice());
//        double amount = Double.valueOf("1");
        builder.setAmount(String.valueOf(amount))
//                .setIsProduction(false)
                .setIsProduction(true)
                .setProductInfo(aPackage.getName())
                .setKey(prodKey)
                .setPhone(user.getPhone())
                .setTransactionId(String.valueOf(System.currentTimeMillis()))
                .setFirstName(user.getName().replaceAll("\\s+", ""))
                .setEmail(user.getEmail())
                .setSurl("https://www.payumoney.com/mobileapp/payumoney/success.php")
                .setFurl("https://www.payumoney.com/mobileapp/payumoney/failure.php")
                .setUserCredential(prodKey + ":" + user.getEmail())
                .setAdditionalParams(additionalParams);
        PayUPaymentParams payUPaymentParams = builder.build();
        return payUPaymentParams;
    }

    public void payUMoneyGateway() {
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000)
            return;
        mLastClickTime = SystemClock.elapsedRealtime();
        initUiSdk(preparePayUBizParams());

    }

    private void initUiSdk(PayUPaymentParams payUPaymentParams) {
        PayUCheckoutPro.open(
                this,
                payUPaymentParams,
                getCheckoutProConfig(),
                new PayUCheckoutProListener() {

                    @Override
                    public void onPaymentSuccess(Object response) {
//                        showAlertDialog(response);
//                        Toast.makeText(RazorPayActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        lnr_success.setVisibility(View.VISIBLE);
                        lnr_failed.setVisibility(View.GONE);
                        try {
                            String jsonInString = new Gson().toJson(response);
                            JSONObject jsonObject = new JSONObject(jsonInString);
                            String jsonString = jsonObject.getString("payuResponse");
                            JSONObject jsonObject1 = new JSONObject(jsonString);
                            String txnId;
                            if (jsonObject1.has("result")) {
                                String jsonString1 = jsonObject1.getString("result");
                                JSONObject jsonObject2 = new JSONObject(jsonString1);
                                txnId = jsonObject2.getString("txnid");

                            } else {
                                txnId = jsonObject1.getString("txnid");

                            }
//                            txt_txn_id.setText("Transaction Id : " + txnId);
                            saveChargeData(txnId, "payumoney");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//
                    }

                    @Override
                    public void onPaymentFailure(Object response) {
//                        showAlertDialog(response);
                        lnr_success.setVisibility(View.GONE);
                        lnr_failed.setVisibility(View.VISIBLE);
//                        Toast.makeText(RazorPayActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPaymentCancel(boolean isTxnInitiated) {
//                        showSnackBar(getResources().getString(R.string.transaction_cancelled_by_user));
//                        Toast.makeText(RazorPayActivity.this, "user cancel", Toast.LENGTH_SHORT).show();
                        lnr_success.setVisibility(View.GONE);
                        lnr_failed.setVisibility(View.VISIBLE);
                        txt_falied_reason.setText("Transaction cancelled by user");
                    }

                    @Override
                    public void onError(ErrorResponse errorResponse) {
                        String errorMessage = errorResponse.getErrorMessage();
                        if (TextUtils.isEmpty(errorMessage))
                            errorMessage = /*getResources().getString(R.string.some_error_occurred);*/"Some error occured";
                        lnr_success.setVisibility(View.GONE);
                        lnr_failed.setVisibility(View.VISIBLE);
                        txt_falied_reason.setText(errorMessage);
//                        showSnackBar(errorMessage);
                    }

                    @Override
                    public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {
                        //For setting webview properties, if any. Check Customized Integration section for more details on this
                    }

                    @Override
                    public void generateHash(HashMap<String, String> valueMap, PayUHashGenerationListener hashGenerationListener) {
                        String hashName = valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                        String hashData = valueMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                        if (!TextUtils.isEmpty(hashName) && !TextUtils.isEmpty(hashData)) {
                            //Generate Hash from your backend here
                            String salt = prodSalt;
                            if (valueMap.containsKey(PayUCheckoutProConstants.CP_POST_SALT))
                                salt = salt + "" + (valueMap.get(PayUCheckoutProConstants.CP_POST_SALT));


                            String hash = null;
                            if (hashName.equalsIgnoreCase(PayUCheckoutProConstants.CP_LOOKUP_API_HASH)) {
                                //Calculate HmacSHA1 HASH for calculating Lookup API Hash
                                ///Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.

                                hash = calculateHmacSHA1Hash(hashData, "");
                            } else {

                                //Calculate SHA-512 Hash here
//                                hash = calculateHash(hashData + salt);

                                hash = getPayUMoneyHas(hashName, hashGenerationListener, hashData);
                            }

//                            HashMap<String, String> dataMap = new HashMap<>();
//                            dataMap.put(hashName, hash);
//                            hashGenerationListener.onHashGenerated(dataMap);
                        }
                    }
                }
        );
    }

    private String calculateHmacSHA1Hash(String data, String key) {
        String HMAC_SHA1_ALGORITHM = "HmacSHA1";
        String result = null;

        try {
            Key signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = getHexString(rawHmac);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String calculateHash(String hashString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(hashString.getBytes());
            byte[] mdbytes = messageDigest.digest();
            return getHexString(mdbytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getHexString(byte[] array) {
        StringBuilder hash = new StringBuilder();
        for (byte hashByte : array) {
            hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
        }
        return hash.toString();
    }


    private void showSnackBar(String message) {
//        Snackbar.make(binding.clMain, message, Snackbar.LENGTH_LONG).show();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private PayUCheckoutProConfig getCheckoutProConfig() {
        PayUCheckoutProConfig checkoutProConfig = new PayUCheckoutProConfig();
        checkoutProConfig.setPaymentModesOrder(getCheckoutOrderList());
        checkoutProConfig.setOfferDetails(null);
        // uncomment below code for performing enforcement
//        checkoutProConfig.setEnforcePaymentList(getEnforcePaymentList());
        checkoutProConfig.setShowCbToolbar(true);
        checkoutProConfig.setAutoSelectOtp(true);
        checkoutProConfig.setAutoApprove(true);
        checkoutProConfig.setSurePayCount(1);
        checkoutProConfig.setShowExitConfirmationOnPaymentScreen(true);
        checkoutProConfig.setShowExitConfirmationOnCheckoutScreen(true);
        checkoutProConfig.setMerchantName("Hunter");
        checkoutProConfig.setMerchantLogo(R.drawable.ppapplogo);
        checkoutProConfig.setWaitingTime(30000);
        checkoutProConfig.setMerchantResponseTimeout(30000);
        checkoutProConfig.setCustomNoteDetails(null);
        return checkoutProConfig;
    }

    private ArrayList<PaymentMode> getCheckoutOrderList() {
        ArrayList<PaymentMode> checkoutOrderList = new ArrayList();
//        if (binding.switchShowGooglePay.isChecked())
        checkoutOrderList.add(new PaymentMode(PaymentType.UPI, PayUCheckoutProConstants.CP_GOOGLE_PAY));
//        if (binding.switchShowPhonePe.isChecked())
        checkoutOrderList.add(new PaymentMode(PaymentType.WALLET, PayUCheckoutProConstants.CP_PHONEPE));
//        if (binding.switchShowPaytm.isChecked())
        checkoutOrderList.add(new PaymentMode(PaymentType.WALLET, PayUCheckoutProConstants.CP_PAYTM));

        return checkoutOrderList;
    }



    private String getPayUMoneyHas(String hashName, PayUHashGenerationListener hashGenerationListener, String hashData) {
        // dialog.show();

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentGatewayApi apiInterface = retrofit.create(PaymentGatewayApi.class);
        Call<ResponseBody> call = apiInterface.payumoney(AppConfig.API_KEY, hashData);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String result = jsonObject.getString("hash_key");
                        hashServer = result;
                        HashMap<String, String> dataMap = new HashMap<>();
                        dataMap.put(hashName, result);
                        hashGenerationListener.onHashGenerated(dataMap);

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ToastMsg(RazorPayActivity.this).toastIconError("Something went wrong." + t.getMessage());
                // dialog.cancel();
                t.printStackTrace();
            }
        });

        return hashServer;
    }

    private void order_entry(String user_id) {
        // dialog.show();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PassResetApi passResetApi = retrofit.create(PassResetApi.class);
        Call<OrderEntryResponse> call = passResetApi.order_entry(AppConfig.API_KEY, user_id, aPackage.getPlanId(), aPackage.getPrice(), orderIdstr);
        call.enqueue(new Callback<OrderEntryResponse>() {
            @Override
            public void onResponse(Call<OrderEntryResponse> call, Response<OrderEntryResponse> response) {
                if (response.code() == 200) {
                    OrderEntryResponse pr = response.body();
                    if (pr.getStatus().equals("success")) {

                        // new ToastMsg(RazorPayActivity.this).toastIconSuccess(pr.getData());

                    } else {
                        //  new ToastMsg(RazorPayActivity.this).toastIconError(pr.getData());
                        // dialog.cancel();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderEntryResponse> call, Throwable t) {
                new ToastMsg(RazorPayActivity.this).toastIconError("Something went wrong." + t.getMessage());
                // dialog.cancel();
                t.printStackTrace();
            }
        });
    }

    private void getPaymentGatewayStatus() {
        // dialog.show();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentGatewayApi apiInterface = retrofit.create(PaymentGatewayApi.class);
        Call<ResponseBody> call = apiInterface.paymentgatway_status(AppConfig.API_KEY, "");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                        String payUMoney = jsonObject.getString("Payu money");
                        String Paytm = jsonObject.getString("Paytm");
                        String cashfree = jsonObject.getString("cashfree");

                        if (payUMoney.equals("1")) {
                            card_payuMoney.setVisibility(View.VISIBLE);
                        } else {
                            card_payuMoney.setVisibility(View.GONE);
                        }

                        if (Paytm.equals("1")) {
                            card_paytm.setVisibility(View.VISIBLE);
                        } else {
                            card_paytm.setVisibility(View.GONE);
                        }
                        if (cashfree.equals("1")) {
                            card_cashfree.setVisibility(View.VISIBLE);
                        } else {
                            card_cashfree.setVisibility(View.GONE);
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ToastMsg(RazorPayActivity.this).toastIconError("Something went wrong." + t.getMessage());
                // dialog.cancel();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onPaymentSuccess(String s) {
        saveChargeData(s, "razorpay");
    }

    @Override
    public void onPaymentError(int i, String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Error: " + message);
        finish();
    }

    public void saveChargeData(String token, String from) {
        progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentApi paymentApi = retrofit.create(PaymentApi.class);
        Call<ResponseBody> call = paymentApi.savePayment(AppConfig.API_KEY, aPackage.getPlanId(),
                databaseHelper.getUserData().getUserId(),
                aPackage.getPrice(),
                token,str_user_age, from);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    updateActiveStatus();

                } else {
                    new ToastMsg(RazorPayActivity.this).toastIconError(getString(R.string.something_went_wrong));
                    finish();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(RazorPayActivity.this).toastIconError(getString(R.string.something_went_wrong));
                finish();
                progressBar.setVisibility(View.GONE);
            }
        });


    }

    private void updateActiveStatus() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);

        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(AppConfig.API_KEY, PreferenceUtils.getUserId(RazorPayActivity.this));
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(Call<ActiveStatus> call, Response<ActiveStatus> response) {
                if (response.code() == 200) {
                    ActiveStatus activeStatus = response.body();
                    DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                    db.deleteAllActiveStatusData();
                    db.insertActiveStatusData(activeStatus);
                    new ToastMsg(RazorPayActivity.this).toastIconSuccess(getResources().getString(R.string.payment_success));
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(RazorPayActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }


            @Override
            public void onFailure(Call<ActiveStatus> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(RazorPayActivity.this).toastIconError(getString(R.string.something_went_wrong));
                finish();
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private String currencyConvert(String currency, String value, String exchangeRate) {
        //convert currency to rupee
        String convertedValue = "";
        if (!currency.equalsIgnoreCase("INR")) {
            double temp = Double.parseDouble(value) * Double.parseDouble(exchangeRate);
            convertedValue = String.valueOf(temp * 100); //convert to rupee
        } else {
            double temp = Double.parseDouble(value);
            convertedValue = String.valueOf(temp * 100);
        }
        amountPaidInRupee = convertedValue;
        return convertedValue;
    }
}
