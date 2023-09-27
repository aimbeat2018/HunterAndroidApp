package ott.hunter;

import static android.content.ContentValues.TAG;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mukeshsolanki.OtpView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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

public class OtpActivity extends AppCompatActivity {

    TextView txt_otp, txt_resend;
    AppCompatEditText otp_edit_box1, otp_edit_box2, otp_edit_box3, otp_edit_box4;
    private String deviceId = "", intentOtp = "", mobile = "", from = "", countryCode = "";
    private ProgressDialog dialog;
    AppCompatButton submit;
    String firebaseToken = "";
    ImageView img_back;

    private FirebaseAuth mAuth;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken verificationToken;
    OtpView otp_view, otp_viewIndia;
    String userEnterOtp = "";

    CleverTapAPI clevertapDefaultInstance;
    CleverTapAPI clevertapscreenviewd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        clevertapscreenviewd = CleverTapAPI.getDefaultInstance(getApplicationContext());

        init();
        otpView();
        mAuth = FirebaseAuth.getInstance();
        intentOtp = getIntent().getStringExtra("otp");
        mobile = getIntent().getStringExtra("mobile_no");
        countryCode = getIntent().getStringExtra("countryCode");
        from = getIntent().getStringExtra("from");
        deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        txt_otp.setText("Enter verification code. We just sent you on mobile number " + mobile);

        if (!countryCode.contains("91")) {
            otp_view.setVisibility(View.VISIBLE);
            otp_viewIndia.setVisibility(View.GONE);
            sendVerificationCode("+" + countryCode + mobile);
        } else {
            otp_view.setVisibility(View.GONE);
            otp_viewIndia.setVisibility(View.VISIBLE);
        }

//        otp_view.setVisibility(View.VISIBLE);
//        otp_viewIndia.setVisibility(View.GONE);
//        sendVerificationCode("+" + countryCode + mobile);

        startMobileTimer();

        txt_resend.setOnClickListener(view -> {

            if (countryCode.contains("91"))
                sendOtp();
            else
                resendVerificationCode("+" + countryCode + mobile, verificationToken);
        });

        otp_view.setOtpCompletionListener(otp -> userEnterOtp = otp);

        otp_viewIndia.setOtpCompletionListener(otp -> userEnterOtp = otp);

        submit.setOnClickListener(view -> {

            if (!countryCode.contains("91")) {
                if (userEnterOtp.equals("")) {
                    Toast.makeText(OtpActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                    verifyCode(userEnterOtp);
                }

            } else {
                if (userEnterOtp.equals("")) {
                    Toast.makeText(OtpActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                } else if (!userEnterOtp.equals(intentOtp)) {

                    Log.d(TAG, "otp123: "+intentOtp);
                    Toast.makeText(OtpActivity.this, "Enter Valid OTP", Toast.LENGTH_SHORT).show();
                } else {
                    if (from.equals("device_change")) {
                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                            if (task.isComplete()) {
                                firebaseToken = task.getResult();
                                Log.e("AppConstants", "onComplete: new Token got: " + firebaseToken);
                                changeDevice();
//                                    sendDataToServer();
                            }
                        });
                    } else if (from.equals("login")) {
                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                            if (task.isComplete()) {
                                firebaseToken = task.getResult();
                                Log.e("AppConstants", "onComplete: new Token got: " + firebaseToken);
                                changeDevice();
//                                    sendDataToServer();
                            }
                        });



                    } else if (from.equals("signup")) {
                        startActivity(new Intent(OtpActivity.this, SignUpActivity.class).putExtra("mobile", mobile).putExtra("countryCode", countryCode));
                    }
                }
            }

//            if (userEnterOtp.equals("")) {
//                Toast.makeText(OtpActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
//            } else {
//                verifyCode(userEnterOtp);
//            }
        });


    }


    private void sendVerificationCode(String number) {
        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)            // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)            // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)
                        .setForceResendingToken(token)// OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            verificationToken = forceResendingToken;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//            final String code = phoneAuthCredential.getSmsCode();
//
//            if (code != null) {
////                edtOTP.setText(code);
//                verifyCode(code);
//            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // displaying error message with firebase exception.
            Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String code) {
        // below line is used for getting
        // credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // if the code is correct and the task is successful
                            // we are sending our user to new activity.
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

                            } else if (from.equals("signup")) {
                                startActivity(new Intent(OtpActivity.this, SignUpActivity.class).putExtra("mobile", mobile).putExtra("countryCode", countryCode));
                            }
//
//                            Intent i = new Intent(OtpActivity.this, MainActivity.class);
//                            startActivity(i);
//                            finish();
                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Toast.makeText(OtpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
//        img_back = findViewById(R.id.img_back);
        otp_edit_box2 = findViewById(R.id.otp_edit_box2);
        otp_edit_box3 = findViewById(R.id.otp_edit_box3);
        otp_edit_box4 = findViewById(R.id.otp_edit_box4);
        otp_view = findViewById(R.id.otp_view);
        otp_viewIndia = findViewById(R.id.otp_viewIndia);


//        img_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
//                startActivity(intent);
//
//            }
//        });
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
                        DatabaseHelper db = new DatabaseHelper(OtpActivity.this);
                        db.deleteUserData();
                        db.insertUserData(user);
                        ApiResources.USER_PHONE = user.getPhone();

                        SharedPreferences.Editor preferences = getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
                        preferences.putBoolean(Constants.USER_LOGIN_STATUS, true);
                        preferences.apply();
                        preferences.commit();


                        addUserToCleverTap(user.getName(), user.getUserId(), user.getEmail(), user.getPhone());

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


    public void addUserToCleverTap(String name, String id, String email, String mobile) {
        HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
        profileUpdate.put("Name", name);    // String
        profileUpdate.put("Identity", id);      // String or number
        profileUpdate.put("Email", email); // Email address of the user
        profileUpdate.put("Phone", mobile);   // Phone (with the country code, starting with +)
//        profileUpdate.put("Gender", "M");             // Can be either M or F
//        profileUpdate.put("DOB", new Date());         // Date of Birth. Set the Date object to the appropriate value first
//// optional fields. controls whether the user will be sent email, push etc.
       profileUpdate.put("MSG-email", true);        // Disable email notifications
        profileUpdate.put("MSG-push", true);          // Enable push notifications
       profileUpdate.put("MSG-sms", true);          // Disable SMS notifications
        profileUpdate.put("MSG-whatsapp", true);      // Enable WhatsApp notifications
//        ArrayList<String> stuff = new ArrayList<String>();
//        stuff.add("bag");
//        stuff.add("shoes");
//        profileUpdate.put("MyStuff", stuff);                        //ArrayList of Strings
//        String[] otherStuff = {"Jeans", "Perfume"};
//        profileUpdate.put("MyStuff", otherStuff);                   //String Array

        clevertapDefaultInstance.onUserLogin(profileUpdate);
        // clevertapDefaultInstance.pushProfile(profileUpdate);
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

                        DatabaseHelper db = new DatabaseHelper(OtpActivity.this);
                        db.deleteAllActiveStatusData();
                        db.insertActiveStatusData(activeStatus);

                        Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                        HashMap<String, Object> screenViewedAction = new HashMap<String, Object>();
                        screenViewedAction.put("Screen Name", "HomepageActivity");
                        clevertapscreenviewd.pushEvent("Screen Viewed", screenViewedAction);

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
                            Toast.makeText(OtpActivity.this, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                            String otp = jsonObject.getString("otp");
                            intentOtp = otp;
                            startMobileTimer();
                        } else {
                            Toast.makeText(OtpActivity.this, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
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