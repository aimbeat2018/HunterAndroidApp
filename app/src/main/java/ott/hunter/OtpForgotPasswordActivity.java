package ott.hunter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import ott.hunter.database.DatabaseHelper;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.LoginApi;
import ott.hunter.network.apis.SubscriptionApi;
import ott.hunter.network.model.ActiveStatus;
import ott.hunter.network.model.User;
import ott.hunter.utils.ApiResources;
import ott.hunter.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OtpForgotPasswordActivity extends AppCompatActivity {
    TextView txt_otp, txt_resend;
    AppCompatEditText otp_edit_box1, otp_edit_box2, otp_edit_box3, otp_edit_box4;
    private String deviceId = "", intentOtp = "", mobile = "", from = "", forgot_otp = "";
    private ProgressDialog dialog;
    AppCompatButton submit;
    String firebaseToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_forgot_password);
        init();
        otpView();
        intentOtp = getIntent().getStringExtra("otp");
        forgot_otp = getIntent().getStringExtra("forgot_otp");
        mobile = getIntent().getStringExtra("mobile_no");
        from = getIntent().getStringExtra("from");
        deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        txt_otp.setText("Enter 4 digit verification code. We just sent you on +91 " + mobile);

        if (from.equals("forgotactivity")) {
            txt_resend.setVisibility(View.GONE);
        }

        startMobileTimer();

        txt_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOtp();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = otp_edit_box1.getText().toString() +
                        otp_edit_box2.getText().toString()
                        + otp_edit_box3.getText().toString()
                        + otp_edit_box4.getText().toString();

                if (from.equals("forgotactivity")) {

                    if (!otp.equals(forgot_otp)) {

                        Toast.makeText(OtpForgotPasswordActivity.this, "Enter Valid OTP", Toast.LENGTH_SHORT).show();
                        
                    } else {

                        Intent intent = new Intent(OtpForgotPasswordActivity.this, ChangePasswordActivity.class);
                        intent.putExtra("mobile_no", mobile);

                        startActivity(intent);
                    }


                } else {

                    if (otp.equals("")) {
                        Toast.makeText(OtpForgotPasswordActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                    } else if (!otp.equals(intentOtp)) {
                        Toast.makeText(OtpForgotPasswordActivity.this, "Enter Valid OTP", Toast.LENGTH_SHORT).show();
                    } else {
                        if (from.equals("device_change")) {
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (task.isComplete()) {
                                        firebaseToken = task.getResult();
                                        Log.e("AppConstants", "onComplete: new Token got: " + firebaseToken);
                                        changeDevice();
//                                    sendDataToServer();
                                    }
                                }
                            });
                        } else if (from.equals("login")) {


                        }
                    }


                }


            }
        });

    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);

        txt_otp = findViewById(R.id.txt_otp);
        txt_resend = findViewById(R.id.txt_resend);
        submit = findViewById(R.id.submit);
        otp_edit_box1 = findViewById(R.id.otp_edit_box1);
        otp_edit_box2 = findViewById(R.id.otp_edit_box2);
        otp_edit_box3 = findViewById(R.id.otp_edit_box3);
        otp_edit_box4 = findViewById(R.id.otp_edit_box4);
    }

    private void otpView() {
        otp_edit_box1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (text.length() == 1)
                    otp_edit_box2.requestFocus();
            }
        });
        otp_edit_box2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (text.length() == 1)
                    otp_edit_box3.requestFocus();
                else if (text.length() == 0)
                    otp_edit_box1.requestFocus();
            }
        });

        otp_edit_box3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (text.length() == 1)
                    otp_edit_box4.requestFocus();
                else if (text.length() == 0)
                    otp_edit_box2.requestFocus();
            }
        });
        otp_edit_box4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (text.length() == 0)
                    otp_edit_box3.requestFocus();
            }
        });
    }


    private void changeDevice() {
//        progressBar.setVisibility(View.VISIBLE);
        dialog.show();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        LoginApi api = retrofit.create(LoginApi.class);
        Call<User> call = api.postChangeDeviceStatus(AppConfig.API_KEY, mobile, deviceId, firebaseToken);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("success")) {
//                        Toast.makeText(OtpActivity.this, deviceId, Toast.LENGTH_LONG).show();
                        User user = response.body();
                        ott.hunter.database.DatabaseHelper db = new ott.hunter.database.DatabaseHelper(OtpForgotPasswordActivity.this);
                        db.deleteUserData();
                        db.insertUserData(user);
                        ApiResources.USER_PHONE = user.getPhone();

                        SharedPreferences.Editor preferences = getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
                        preferences.putBoolean(Constants.USER_LOGIN_STATUS, true);
                        preferences.apply();
                        preferences.commit();

                        //save user login time, expire time
                        updateSubscriptionStatus(db.getUserData().getUserId());
                    }

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
            }
        });


    }


    public void updateSubscriptionStatus(String userId) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);

        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(AppConfig.API_KEY, userId);
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(Call<ActiveStatus> call, Response<ActiveStatus> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        ActiveStatus activeStatus = response.body();

                        ott.hunter.database.DatabaseHelper db = new DatabaseHelper(OtpForgotPasswordActivity.this);
                        db.deleteAllActiveStatusData();
                        db.insertActiveStatusData(activeStatus);

                        Intent intent = new Intent(OtpForgotPasswordActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                        startActivity(intent);
                        finish();
                        dialog.cancel();
                    }
                }
            }

            @Override
            public void onFailure(Call<ActiveStatus> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void startMobileTimer() {
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                txt_resend.setText("0 : " + String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                txt_resend.setText("Resend OTP");
            }
        }.start();
    }

    public boolean validCellPhone(String number) {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private void sendOtp() {
//        progressBar.setVisibility(View.VISIBLE);
        dialog.show();
  /*      String phoneNo = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        if (phoneNo.contains("+91")) {
            phoneNo = phoneNo.replace("+91", "");
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();*/
        String mobileNo = "", email = "";
        boolean validEmail = isValidEmailAddress(mobile);
        boolean validMobile = validCellPhone(mobile);
        if (validEmail) {
            email = mobile;
            mobileNo = "";
        } else if (validMobile) {
            mobileNo = mobile;
            email = "";
        }

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        LoginApi api = retrofit.create(LoginApi.class);
        Call<ResponseBody> call = api.login_otp(AppConfig.API_KEY, mobile, email);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String status = jsonObject.getString("status");
                        if (status.equals("success")) {
                            Toast.makeText(OtpForgotPasswordActivity.this, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                            String otp = jsonObject.getString("otp");
                            intentOtp = otp;
                            startMobileTimer();
                        } else {
                            Toast.makeText(OtpForgotPasswordActivity.this, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.cancel();
                  /*  if (response.body().getStatus().equals("success")) {
//                        Toast.makeText(OtpActivity.this, deviceId, Toast.LENGTH_LONG).show();
                        User user = response.body();
                        DatabaseHelper db = new DatabaseHelper(OtpActivity.this);
                        db.deleteUserData();
                        db.insertUserData(user);
                        ApiResources.USER_PHONE = user.getPhone();

                        SharedPreferences.Editor preferences = getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
                        preferences.putBoolean(Constants.USER_LOGIN_STATUS, true);
                        preferences.apply();
                        preferences.commit();

                        //save user login time, expire time
                        updateSubscriptionStatus(db.getUserData().getUserId());
                    }*/

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
/*//                progressBar.setVisibility(View.GONE);
                changeDeviceSignIn();*/
                dialog.cancel();
            }
        });


    }


}