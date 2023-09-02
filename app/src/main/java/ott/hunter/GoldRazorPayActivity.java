package ott.hunter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentResultListener;
import com.razorpay.RazorpayClient;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import ott.hunter.database.DatabaseHelper;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.PaymentApi;
import ott.hunter.network.apis.SubscriptionApi;
import ott.hunter.network.model.ActiveStatus;
import ott.hunter.network.model.User;
import ott.hunter.network.model.config.PaymentConfig;
import ott.hunter.utils.ApiResources;
import ott.hunter.utils.PreferenceUtils;
import ott.hunter.utils.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GoldRazorPayActivity extends AppCompatActivity implements PaymentResultListener {
    private static final String TAG = "RazorPayActivity";
//    private Package aPackage;
    private DatabaseHelper databaseHelper;
    private ProgressBar progressBar;
    private String amountPaidInRupee = "", orderIdstr;
    String videoId = "", videoAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_razor_pay);
        progressBar = findViewById(R.id.progress_bar);
//        aPackage = (Package) getIntent().getSerializableExtra("package");
        videoId = getIntent().getStringExtra("videoId");
        videoAmount = getIntent().getStringExtra("videoAmount");
        databaseHelper = new DatabaseHelper(this);
        //Checkout.preload(getApplicationContext());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        startPayment();
    }

    public void startPayment() {
        PaymentConfig config = databaseHelper.getConfigurationData().getPaymentConfig();
        User user = databaseHelper.getUserData();

        double big = Double.valueOf(videoAmount);
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
            Log.e(TAG, currencyConvert(config.getCurrency(),videoAmount, ApiResources.RAZORPAY_EXCHANGE_RATE));
        } catch (Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }


    @Override
    public void onPaymentSuccess(String s) {
        saveChargeData(s);
    }

    @Override
    public void onPaymentError(int i, String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Error: " + message);
        finish();
    }

    /*videos_id
user_id
paid_amount
payment_info
payment_method*/

    public void saveChargeData(String token) {
        progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentApi paymentApi = retrofit.create(PaymentApi.class);
        Call<ResponseBody> call = paymentApi.saveGoldPayment(AppConfig.API_KEY, videoId,
                databaseHelper.getUserData().getUserId(),
                amountPaidInRupee,
                token, "RazorPay");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    updateActiveStatus();

                } else {
                    new ToastMsg(GoldRazorPayActivity.this).toastIconError(getString(R.string.something_went_wrong));
                    finish();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(GoldRazorPayActivity.this).toastIconError(getString(R.string.something_went_wrong));
                finish();
                progressBar.setVisibility(View.GONE);
            }
        });


    }

    private void updateActiveStatus() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);

        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(AppConfig.API_KEY, PreferenceUtils.getUserId(GoldRazorPayActivity.this));
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(Call<ActiveStatus> call, Response<ActiveStatus> response) {
                if (response.code() == 200) {
                    ActiveStatus activeStatus = response.body();
                    DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                    db.deleteAllActiveStatusData();
                    db.insertActiveStatusData(activeStatus);
                    new ToastMsg(GoldRazorPayActivity.this).toastIconSuccess(getResources().getString(R.string.payment_success));
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(GoldRazorPayActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ActiveStatus> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(GoldRazorPayActivity.this).toastIconError(getString(R.string.something_went_wrong));
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
