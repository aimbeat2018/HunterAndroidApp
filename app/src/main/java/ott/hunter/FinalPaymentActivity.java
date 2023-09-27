package ott.hunter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.clevertap.android.sdk.CleverTapAPI;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.ResponseBody;
import ott.hunter.database.DatabaseHelper;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.PaymentApi;
import ott.hunter.network.apis.PaymentGatewayApi;
import ott.hunter.network.apis.SubscriptionApi;
import ott.hunter.network.model.ActiveStatus;
import ott.hunter.network.model.Package;
import ott.hunter.network.model.SubscriptionHistory;
import ott.hunter.network.model.User;
import ott.hunter.utils.PreferenceUtils;
import ott.hunter.utils.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FinalPaymentActivity extends AppCompatActivity {

    TextView package_name,
            package_validity,
            price;
    CardView card_paytm,
            card_payuMoney,
            card_cashfree,card_razorpay,card_gpay,card_oneupi, card_stripe;
    ImageView close_iv;
    private Package aPackage;
    CleverTapAPI clevertapPaymentStartedInstance,clevertapscreenviewd;


    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    PaymentSheet.CustomerConfiguration customerConfig;
    String plantamount = "", strip_plan_amount = "";

    CleverTapAPI clevertapChergedInstance;

    String uid = "", uname = "", mobile = "", email = "", order_id = "", orderIdstr = "";
    private ott.hunter.database.DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_payment);
        aPackage = (Package) getIntent().getSerializableExtra("package");
        init();
        onClick();

        clevertapPaymentStartedInstance= CleverTapAPI.getDefaultInstance(getApplicationContext());
        clevertapscreenviewd= CleverTapAPI.getDefaultInstance(getApplicationContext());

       /* User user = databaseHelper.getUserData();
        uid = user.getUserId();
        uname = user.getName();
//        mobile = "0000000000";
        mobile = user.getPhone();
        email = user.getEmail();

        strip_plan_amount = String.valueOf((Long.parseLong(aPackage.getPrice()) * 100));

        fetch_stripe_Payment_data(strip_plan_amount);

        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);*/

    }

/*

    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Log.e("PaymentSheetResult:", "Canceled");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Log.e("PaymentSheetResult:", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {

            //  saveChargeData("","stripe");
            saveChargeData(order_id, "stripe");

            // Display for example, an order confirmation screen
            Log.e("PaymentSheetResult:", "Completed");

            Toast.makeText(FinalPaymentActivity.this, "Payment success", Toast.LENGTH_SHORT).show();

        }
    }


    public void fetch_stripe_Payment_data(String strip_plan_amount) {
        //   RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
       // String url = "https://primeplay.co.in/webworld_backoffice/rest_api/V130/stripe_payment";
        String url = "https://hunters.co.in/webworld_backoffice/rest-api/V130/stripe_payment";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                //  StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "?amount=" + 100 + "&currency=" + "INR" + "&customer=" + 18,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("striperesponse", response);
                        try {

                            final JSONObject result = new JSONObject(response);

                            customerConfig = new PaymentSheet.CustomerConfiguration(
                                    result.getString("customer"),
                                    result.getString("ephemeralKey")
                            );

                            order_id = result.getString("customer");
                            paymentIntentClientSecret = result.getString("paymentIntent");
                            PaymentConfiguration.init(getApplicationContext(), result.getString("publishableKey"));

                            */
/* used secret key of dahsbord in APi-    sk_live_.......fgHl*//*


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("API-KEY", AppConfig.API_KEY);
                // headers.put("API-KEY", "xm6CG4a0SC");
                return headers;
            }



            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                //paramV.put("amount", plantamount);
                // paramV.put("amount", String.valueOf((Double.parseDouble(plantamount) * 1000)));
                //  paramV.put("amount", String.valueOf((Double.parseDouble(aPackage.getPrice()) * 1000)));
                //  paramV.put("amount", "20000");
                paramV.put("amount", strip_plan_amount);
                paramV.put("currency", "inr");
                paramV.put("customer", uid);

                return paramV;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        queue.add(stringRequest);
    }
*/



    private void init() {
        package_name = findViewById(R.id.package_name);
        package_validity = findViewById(R.id.package_validity);
        price = findViewById(R.id.price);
        close_iv = findViewById(R.id.close_iv);
        card_paytm = findViewById(R.id.card_paytm);
        card_payuMoney = findViewById(R.id.card_payuMoney);
        card_cashfree = findViewById(R.id.card_cashfree);
        card_razorpay = findViewById(R.id.card_razorpay);
        card_gpay = findViewById(R.id.card_gpay);

        card_oneupi = findViewById(R.id.card_oneupi);
        card_stripe = findViewById(R.id.card_stripe);

        package_name.setText(aPackage.getName());
        package_validity.setText(aPackage.getDay()+" Days");

        price.setText( "\u20B9 "+ aPackage.getPrice());


        card_stripe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (paymentIntentClientSecret != null)

                    try {

                        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, new PaymentSheet.Configuration("besharams",
                                customerConfig));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        });

    }


    public void onClick(){
        card_payuMoney.setOnClickListener(view -> {
            Intent intent = new Intent(FinalPaymentActivity.this, RazorPayActivity.class);
            intent.putExtra("package", aPackage);
            intent.putExtra("currency", "currency");
            intent.putExtra("from", "payu");
            startActivity(intent);

        });
        card_razorpay.setOnClickListener(view -> {
            Intent intent = new Intent(FinalPaymentActivity.this, RazorPayActivity.class);
            intent.putExtra("package", aPackage);
            intent.putExtra("currency", "currency");
            intent.putExtra("from", "razor");
            startActivity(intent);

        });


        card_cashfree.setOnClickListener(view -> {
            Intent intent = new Intent(FinalPaymentActivity.this, CashFreePaymentActivity.class);
            intent.putExtra("package", aPackage);
            intent.putExtra("currency", "currency");
            intent.putExtra("from", "cashfree");

            HashMap<String, Object> paymentstartedAction= new HashMap<String, Object>();
            paymentstartedAction.put("payment mode","Cash Free");
            paymentstartedAction.put("Selected Plan",aPackage.getName());
            paymentstartedAction.put("Amount",aPackage.getPrice());
            paymentstartedAction.put("Days",aPackage.getDay());
            clevertapPaymentStartedInstance.pushEvent("Payment Started", paymentstartedAction);

            HashMap<String, Object> screenViewedAction = new HashMap<String, Object>();
            screenViewedAction.put("Screen Name", "cashFreePayment");
            clevertapscreenviewd.pushEvent("Screen Viewed", screenViewedAction);


            startActivity(intent);

        });



        card_oneupi.setOnClickListener(view -> {
            Intent intent = new Intent(FinalPaymentActivity.this, OneUPIPaymentActivity.class);
            intent.putExtra("package", aPackage);
            intent.putExtra("currency", "currency");
            intent.putExtra("from", "oneupi");
            startActivity(intent);

            HashMap<String, Object> paymentstartedAction = new HashMap<String, Object>();
            paymentstartedAction.put("payment mode", "oneUPI");
            paymentstartedAction.put("Selected Plan", aPackage.getName());
            paymentstartedAction.put("Amount", aPackage.getPrice());
            paymentstartedAction.put("Days", aPackage.getDay());
            clevertapPaymentStartedInstance.pushEvent("Payment Started", paymentstartedAction);

            HashMap<String, Object> screenViewedAction = new HashMap<String, Object>();
            screenViewedAction.put("Screen Name", "oneUPIPayment");
            clevertapscreenviewd.pushEvent("Screen Viewed", screenViewedAction);


        });



        card_gpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FinalPaymentActivity.this, RazorPayActivity.class);
                intent.putExtra("package", aPackage);
                intent.putExtra("currency", "currency");
                intent.putExtra("from", "gpay");
                startActivity(intent);
            }
        });
        close_iv.setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPaymentGatewayStatus();
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
                        String razorpay = jsonObject.getString("razorpay");
                        String gpay = jsonObject.getString("gap");
                        String oneupi = jsonObject.getString("oneupi");

                        if (payUMoney.equals("1")) {
                            card_payuMoney.setVisibility(View.VISIBLE);
                        } else {
                            card_payuMoney.setVisibility(View.GONE);
                        }

//                        if (Paytm.equals("1")) {
//                            card_paytm.setVisibility(View.VISIBLE);
//                        } else {
//                            card_paytm.setVisibility(View.GONE);
//                        }
                        if (cashfree.equals("1")) {
                            card_cashfree.setVisibility(View.VISIBLE);
                        } else {
                            card_cashfree.setVisibility(View.GONE);
                        }
                        if (razorpay.equals("1")) {
                            card_razorpay.setVisibility(View.VISIBLE);
                        } else {
                            card_razorpay.setVisibility(View.GONE);
                        } if (gpay.equals("1")) {
                            card_gpay.setVisibility(View.VISIBLE);
                        } else {
                            card_gpay.setVisibility(View.GONE);
                        }
                        if (oneupi.equals("1")) {
                            card_oneupi.setVisibility(View.VISIBLE);
                        } else {
                            card_oneupi.setVisibility(View.GONE);
                        }


                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ToastMsg(FinalPaymentActivity.this).toastIconError("Something went wrong." + t.getMessage());
                // dialog.cancel();
                t.printStackTrace();
            }
        });
    }



/*
    public void saveChargeData(String token, String from) {
        //  progressBar.setVisibility(View.VISIBLE);
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
                    new ToastMsg(FinalPaymentActivity.this).toastIconError(getString(R.string.something_went_wrong));
                    finish();
                    // progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(FinalPaymentActivity.this).toastIconError(getString(R.string.something_went_wrong));
                finish();
                //progressBar.setVisibility(View.GONE);
            }
        });

    }




    private void updateActiveStatus() {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);

        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(AppConfig.API_KEY, PreferenceUtils.getUserId(FinalPaymentActivity.this));
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(Call<ActiveStatus> call, retrofit2.Response<ActiveStatus> response) {
                if (response.code() == 200) {
                    ActiveStatus activeStatus = response.body();
                    ott.hunter.database.DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                    db.deleteAllActiveStatusData();
                    db.insertActiveStatusData(activeStatus);
                    new ToastMsg(FinalPaymentActivity.this).toastIconSuccess(getResources().getString(R.string.payment_success));
                    // progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(FinalPaymentActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ActiveStatus> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(FinalPaymentActivity.this).toastIconError(getString(R.string.something_went_wrong));
                finish();
                //   progressBar.setVisibility(View.GONE);

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
                            paymentAction.put("payment mode", "stripe");
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
                //  progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });

    }*/
}