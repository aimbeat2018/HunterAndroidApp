package ott.hunter.more;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ott.hunter.AppConfig;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.ApplyPromoCode;
import ott.hunter.utils.PreferenceUtils;
import ott.hunter.utils.RtlUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import ott.hunter.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ApplyPromoCodeActivity extends AppCompatActivity {

    private EditText edPromoCode;
    Button button_subscribe;
    private int promoCodeTrial = 0;
    ProgressDialog pDialog;
    boolean IS_PROMOCODE_DONE;
    long PROMOCODE_TIME;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RtlUtils.setScreenDirection(this);
        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        boolean isDark = sharedPreferences.getBoolean("dark", false);

        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_promo_code);
        Toolbar toolbar = (Toolbar) findViewById(R.id.subscription_toolbar);

        if (!isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.black_window_light));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Apply Promo Code");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //---analytics-----------
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "apply_promo_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        id = PreferenceUtils.getUserId(ApplyPromoCodeActivity.this);

        edPromoCode = findViewById(R.id.ed_promo_code);
        button_subscribe = findViewById(R.id.button_subscribe);

        edPromoCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6), new InputFilter.AllCaps()});

        button_subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPromoCodeValidity()) {
                    checkPromoCodeAPI(edPromoCode.getText().toString());
                }
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


    private boolean checkPromoCodeValidity() {
        if (IS_PROMOCODE_DONE) {
            long timer = PROMOCODE_TIME;
            if (!IS_PROMOCODE_DONE && (System.currentTimeMillis() - timer) >= (24 * 60 * 60 * 1000)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void showProgressDialog() {
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    private void checkPromoCodeAPI(String promocode) {
        showProgressDialog();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        ApplyPromoCode api = retrofit.create(ApplyPromoCode.class);
        Call<ResponseBody> call = api.check_promocode(AppConfig.API_KEY, id, promocode);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                dismissProgressDialog();
                try {
                    JSONObject mainJson = new JSONObject(response.body().string());
                    JSONObject jsonArray = mainJson.getJSONObject("VIDEO_STREAMING_APP");
                    if (jsonArray.length() > 0) {
                        int isSuccess = jsonArray.getInt("promocode");
                        if (isSuccess == 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ApplyPromoCodeActivity.this);
                            builder.setMessage("Promocode applied successfully, You have Enrolled into Premium Subscription")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialog, id) -> {
                                        Intent returnIntent = new Intent();
                                        setResult(Activity.RESULT_OK, returnIntent);
                                        finish();
                                        dialog.dismiss();
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else if (isSuccess == 2) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ApplyPromoCodeActivity.this);
                            builder.setMessage("This Promocode is already used.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialog, id) -> {
                                        dialog.dismiss();
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            promoCodeTrial += 1;
                            Toast.makeText(ApplyPromoCodeActivity.this, "Promo code is invalid", Toast.LENGTH_SHORT).show();

                            if (promoCodeTrial == 3) {
                                IS_PROMOCODE_DONE = true;
                                PROMOCODE_TIME = System.currentTimeMillis();
                            }
                        }

                    }
                } catch (JSONException | IOException e) {
                    Log.d("ERROR1", e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("ERROR2", t.getMessage());
                dismissProgressDialog();
            }
        });
    }
}