package ott.hunter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clevertap.android.sdk.CleverTapAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.ResponseBody;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.PaymentGatewayApi;
import ott.hunter.network.model.Package;
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
            card_cashfree,card_razorpay,card_gpay;
    ImageView close_iv;
    private Package aPackage;

    CleverTapAPI clevertapPaymentStartedInstance,clevertapscreenviewd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_payment);
        aPackage = (Package) getIntent().getSerializableExtra("package");
        init();
        onClick();

        clevertapPaymentStartedInstance= CleverTapAPI.getDefaultInstance(getApplicationContext());
        clevertapscreenviewd= CleverTapAPI.getDefaultInstance(getApplicationContext());

    }


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

        package_name.setText(aPackage.getName());
        package_validity.setText(aPackage.getDay()+" Days");

        price.setText( "\u20B9 "+ aPackage.getPrice());
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
}