package ott.hunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clevertap.android.sdk.CleverTapAPI;

import java.util.HashMap;
import java.util.Random;

import dev.android.oneupi.OneUPIPayment;
import dev.android.oneupi.listener.PaymentStatusListener;
import dev.android.oneupi.model.PaymentApp;
import dev.android.oneupi.model.TransactionDetails;
import okhttp3.ResponseBody;
import ott.hunter.database.DatabaseHelper;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.PaymentApi;
import ott.hunter.network.apis.SubscriptionApi;
import ott.hunter.network.model.ActiveStatus;
import ott.hunter.network.model.Package;
import ott.hunter.network.model.SubscriptionHistory;
import ott.hunter.network.model.User;
import ott.hunter.utils.PreferenceUtils;
import ott.hunter.utils.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;


public class OneUPIPaymentActivity extends AppCompatActivity implements PaymentStatusListener {
    String orderID = "ORDER_ID";
    String token = "TOKEN";
    String order_token = "order_token";
    String cf_order_id = "cf_order_id";
    String order_status = "order_status";

    ProgressDialog dialog;

    String subscription_end_date = "";

    private static final String TAG = "OneUPIPaymentActivity";
    String uid = "", uname = "", mobile = "", email = "", order_id = "", orderIdstr = "";
    static int min, max, create_otp;
    String plantamount = "";
    private Package aPackage;

  //  private DatabaseHelper databaseHelper;

    private ott.hunter.database.DatabaseHelper databaseHelper;
    TextView txt_txn_id,
            txt_falied_reason;
    LinearLayout lnr_success,
            lnr_failed;
    private ProgressBar progressBar;
    CleverTapAPI clevertapChergedInstance;

    String transactionId;
    private OneUPIPayment easyUpiPayment;
    Float float_plan_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_upipayment);

        clevertapChergedInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        clevertapChergedInstance.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);

        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);

        if (getIntent() != null) {
            aPackage = (Package) getIntent().getSerializableExtra("package");
            databaseHelper = new ott.hunter.database.DatabaseHelper(this);
        }


        float_plan_amount = Float.valueOf(aPackage.getPrice());

        lnr_success = findViewById(R.id.lnr_success);
        lnr_failed = findViewById(R.id.lnr_failed);
        txt_txn_id = findViewById(R.id.txt_txn_id);
        txt_falied_reason = findViewById(R.id.txt_falied_reason);
        progressBar = findViewById(R.id.progressBar);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);

        User user = databaseHelper.getUserData();
        uid = user.getUserId();
        uname = user.getName();
//        mobile = "0000000000";
        mobile = user.getPhone();
        email = user.getEmail();

        min = 100000;
        max = 999999;
        Random r = new Random();
        create_otp = r.nextInt(max - min + 1) + min;
        order_id = String.valueOf(create_otp);
        orderID = order_id;

        //getToken(order_id, "1");

        payWithUpi();

    }


    private void payWithUpi() {

        transactionId = "TID" + System.currentTimeMillis();

        PaymentApp paymentApp = PaymentApp.ALL;

        // START PAYMENT INITIALIZATION
        OneUPIPayment.Builder builder = new OneUPIPayment.Builder(this)
                .with(paymentApp)
                .setPayeeVpa("WEBWORLD7.09@cmsidfc")
                .setPayeeName("Webworld Multimedia LLP")
                .setTransactionId(transactionId)
                .setTransactionRefId(transactionId)
                .setPayeeMerchantCode("ae8e5dc9-d337-4a86-8a1b-c5232325d06c")//one upi marchant key
                .setDescription(aPackage.getName())
                //.setAmount("1.00");
                .setAmount(String.valueOf(float_plan_amount));

        // END INITIALIZATION

        try {
            // Build instance

            easyUpiPayment = builder.build();

            // Register Listener for Events
            easyUpiPayment.setPaymentStatusListener(this);

            // Start payment / transaction
            easyUpiPayment.startPayment();
        } catch (Exception exception) {
            exception.printStackTrace();
            toast("Error: " + exception.getMessage());

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onTransactionCancelled() {

        // Payment Cancelled by User
        toast("Cancelled by user");

    }

    @Override
    public void onTransactionCompleted(@NonNull TransactionDetails transactionDetails) {
        // Transaction Completed
        Log.d("TransactionDetails", transactionDetails.toString());

        switch (transactionDetails.getTransactionStatus()) {
            case SUCCESS:
                onTransactionSuccess();
                break;
            case FAILURE:
                onTransactionFailed();
                break;
            case SUBMITTED:
                onTransactionSubmitted();
                break;
        }

    }


    private void onTransactionSuccess() {
        // Payment Success
        lnr_failed.setVisibility(View.GONE);
        lnr_success.setVisibility(View.VISIBLE);

        txt_txn_id.setText("Transaction Id : " + transactionId);

        saveChargeData(transactionId, "oneupi");
        //  saveChargeData(orderID, "oneupi");

        toast("Success");
        Log.d("UPI", "responseStr: " + transactionId);
//  new Transaction(SelectPlanActivity.this).purchasedItem(planId, transactionId, "UPI");
    }


    private void onTransactionSubmitted() {
        // Payment Pending
        toast("Pending | Submitted");
    }

    private void onTransactionFailed() {
        // Payment Failed
        toast("Failed");

        lnr_failed.setVisibility(View.VISIBLE);
        lnr_success.setVisibility(View.GONE);

        /*   txt_txn_id.setText(cfErrorResponse.getMessage());*/


        new Handler().postDelayed(() -> finish(), 1000);

    }



    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void saveChargeData(String token, String from) {
        progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentApi paymentApi = retrofit.create(PaymentApi.class);
        Call<ResponseBody> call = paymentApi.savePayment(AppConfig.API_KEY, aPackage.getPlanId(),
                databaseHelper.getUserData().getUserId(),
                aPackage.getPrice(),
                // "1",
                token, from);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.code() == 200) {

                    plantamount = aPackage.getPrice();
                    updateActiveStatus();

                    getSubscriptionHistory(plantamount);

                } else {
                    new ToastMsg(OneUPIPaymentActivity.this).toastIconError(getString(R.string.something_went_wrong));
                    finish();
                    progressBar.setVisibility(View.GONE);
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(OneUPIPaymentActivity.this).toastIconError(getString(R.string.something_went_wrong));
                finish();
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    private void updateActiveStatus() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);

        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(AppConfig.API_KEY, PreferenceUtils.getUserId(OneUPIPaymentActivity.this));
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(Call<ActiveStatus> call, retrofit2.Response<ActiveStatus> response) {
                if (response.code() == 200) {
                    ActiveStatus activeStatus = response.body();
                   DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                    db.deleteAllActiveStatusData();
                    db.insertActiveStatusData(activeStatus);
                    new ToastMsg(OneUPIPaymentActivity.this).toastIconSuccess(getResources().getString(R.string.payment_success));
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(OneUPIPaymentActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ActiveStatus> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(OneUPIPaymentActivity.this).toastIconError(getString(R.string.something_went_wrong));
                finish();
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    private void getSubscriptionHistory(String plantamount) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);
        Call<SubscriptionHistory> call = subscriptionApi.getSubscriptionHistory(AppConfig.API_KEY, uid);
        call.enqueue(new Callback<SubscriptionHistory>() {
            @Override
            public void onResponse(Call<SubscriptionHistory> call, retrofit2.Response<SubscriptionHistory> response) {
                SubscriptionHistory subscriptionHistory = response.body();
                if (response.code() == 200) {

                    try {

                        if (subscriptionHistory.getActiveSubscription().size() > 0) {

                            HashMap<String, Object> paymentAction = new HashMap<String, Object>();
                            paymentAction.put("payment mode", "oneupi");
                            paymentAction.put("amount", plantamount);
                            paymentAction.put("subscription plan", subscriptionHistory.getActiveSubscription().get(0).getPlanTitle());
                            paymentAction.put("Payment ID", subscriptionHistory.getActiveSubscription().get(0).getPaymentInfo());
                            paymentAction.put("Subscription ID", subscriptionHistory.getActiveSubscription().get(0).getSubscriptionId());
                            paymentAction.put("subscription plan id", subscriptionHistory.getActiveSubscription().get(0).getPlanId());
                            paymentAction.put("subscription Start date", subscriptionHistory.getActiveSubscription().get(0).getStartDate());
                            paymentAction.put("subscription End date", subscriptionHistory.getActiveSubscription().get(0).getExpireDate());
                            clevertapChergedInstance.pushEvent("Charged", paymentAction);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }


            @Override
            public void onFailure(Call<SubscriptionHistory> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });

    }


}