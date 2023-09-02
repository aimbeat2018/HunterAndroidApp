package ott.hunter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.mukeshsolanki.OtpView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import ott.hunter.database.DatabaseHelper;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.DeactivateAccountApi;
import ott.hunter.network.apis.LoginApi;
import ott.hunter.network.apis.ProfileApi;
import ott.hunter.network.apis.SetPasswordApi;
import ott.hunter.network.apis.UserDataApi;
import ott.hunter.network.model.ResponseStatus;
import ott.hunter.network.model.User;
import ott.hunter.utils.ApiResources;
import ott.hunter.utils.Constants;
import ott.hunter.utils.PreferenceUtils;
import ott.hunter.utils.RtlUtils;
import ott.hunter.utils.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private TextInputEditText etName, etEmail, etPhone, etPass, etCurrentPassword;
    private AutoCompleteTextView genderSpinner;
    private Button btnUpdate, deactivateBt, setPasswordBtn;
    private ProgressDialog dialog;
    private String URL = "", strGender;
    private CircleImageView userIv;
    private ImageView editProfilePicture;
    private static final int GALLERY_REQUEST_CODE = 1;
    private Uri imageUri;
    private ProgressBar progressBar;
    private String id;
    private boolean isDark;
    private String selectedGender = "Male";
    String from = "", intentOtp = "", countryCode = "";
    String userEnteredOtp = "";
    private ImageView backbtn;
    CountryCodePicker ccp;
    Button saveButton1;
    private FirebaseAuth mAuth;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken verificationToken;
    CleverTapAPI clevertapDefaultInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RtlUtils.setScreenDirection(this);
        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("dark", false);
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());

        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (!isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            //genderSpinner.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.black_window_light));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //---analytics-----------
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "profile_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();

        from = getIntent().getStringExtra("from");
        etName = findViewById(R.id.name);
        etEmail = findViewById(R.id.email);
        etPhone = findViewById(R.id.phone);
        etPass = findViewById(R.id.password);
        etCurrentPassword = findViewById(R.id.currentPassword);
        btnUpdate = findViewById(R.id.saveButton);
        userIv = findViewById(R.id.user_iv);
        editProfilePicture = findViewById(R.id.pro_pic_edit_image_view);
        progressBar = findViewById(R.id.progress_bar);
        deactivateBt = findViewById(R.id.deactive_bt);
        backbtn = findViewById(R.id.imgback);
        genderSpinner = findViewById(R.id.genderSpinnerField);
        setPasswordBtn = findViewById(R.id.setPasswordBtn);
        ccp = findViewById(R.id.ccp);

        id = PreferenceUtils.getUserId(ProfileActivity.this);


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ProfileActivity.this, MoreActivity.class);
                startActivity(intent);

            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etEmail.getText().toString().equals("")) {
                    Toast.makeText(ProfileActivity.this, "Please enter valid email.", Toast.LENGTH_LONG).show();
                    return;
                } else if (etName.getText().toString().equals("")) {
                    Toast.makeText(ProfileActivity.this, "Please enter name.", Toast.LENGTH_LONG).show();
                    return;
                } else if (etPhone.getText().toString().equals("")) {
                    Toast.makeText(ProfileActivity.this, "Please enter phone number.", Toast.LENGTH_LONG).show();
                    return;
                } else { /*else if (etCurrentPassword.getText().toString().equals("")) {
                    new ToastMsg(ProfileActivity.this).toastIconError("Current password must not be empty.");
                    return;
                }*/
//                progressBar.setVisibility(View.VISIBLE);
                    showLoader();

                    String email = etEmail.getText().toString();
                    String phone = etPhone.getText().toString();
                    String pass = etPass.getText().toString();
                    String currentPass = etCurrentPassword.getText().toString();
                    String name = etName.getText().toString();

                    if (ccp.getSelectedCountryCode().equals("")) {
                        Toast.makeText(ProfileActivity.this, "Select countryCode", Toast.LENGTH_SHORT).show();
                    } else if (ccp.getSelectedCountryCode().contains("91")) {
                        sendOtp(id, email, phone, name, pass, currentPass);

//                    if (etPhone.getText().toString().contains("+")) {
//                        sendVerificationCode(etPhone.getText().toString());
//                    } else {
//                        sendVerificationCode("+" + ccp.getSelectedCountryCode() + etPhone.getText().toString());
//                    }
//                    sendVerificationCode("+" + ccp.getSelectedCountryCode() + etPhone.getText().toString());
                    } else {
                        if (etPhone.getText().toString().contains("+")) {
                            sendVerificationCode(etPhone.getText().toString());
                        } else {
                            sendVerificationCode("+" + ccp.getSelectedCountryCode() + etPhone.getText().toString());
                        }
                    }


                }
            }
        });

        setPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSetPasswordDialog();
            }
        });

        //gender spinner setup
        final String[] genderArray = new String[2];
        genderArray[0] = "Male";
        genderArray[1] = "Female";
        genderSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Select Gender");
                builder.setSingleChoiceItems(genderArray, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((TextView) v).setText(genderArray[i]);
                        selectedGender = genderArray[i];
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

        getProfile(false);
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

    // callback method is called on Phone auth provider.
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            verificationToken = forceResendingToken;

            String email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();
            String pass = etPass.getText().toString();
            String currentPass = etCurrentPassword.getText().toString();
            String name = etName.getText().toString();

            otpDialog(id, email, phone, name, pass, currentPass);
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
            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

                            String email = etEmail.getText().toString();
                            String phone = etPhone.getText().toString();
                            String pass = etPass.getText().toString();
                            String currentPass = etCurrentPassword.getText().toString();
                            String name = etName.getText().toString();


                            updateProfile(id, email, phone, name, pass, currentPass);


                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();

        editProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        deactivateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeactivationDialog();
            }
        });
    }

    private void showDeactivationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_deactivate, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final EditText passEt = view.findViewById(R.id.pass_et);
        final EditText reasonEt = view.findViewById(R.id.reason_et);
        final Button okBt = view.findViewById(R.id.ok_bt);
        Button cancelBt = view.findViewById(R.id.cancel_bt);
        ImageView closeIv = view.findViewById(R.id.close_iv);
        final ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        LinearLayout topLayout = view.findViewById(R.id.top_layout);
        if (isDark) {
            topLayout.setBackgroundColor(getResources().getColor(R.color.overlay_dark_30));
        }

        okBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = passEt.getText().toString();
                String reason = reasonEt.getText().toString();

                if (TextUtils.isEmpty(pass)) {
                    new ToastMsg(ProfileActivity.this).toastIconError("Please enter your password");
                    return;
                } else if (TextUtils.isEmpty(reason)) {
                    new ToastMsg(ProfileActivity.this).toastIconError("Please enter your reason");
                    return;
                }
                deactivateAccount(pass, reason, alertDialog, progressBar);
            }
        });

        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


    }

    private void deactivateAccount(String pass, String reason, final AlertDialog alertDialog, final ProgressBar progressBar) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        DeactivateAccountApi api = retrofit.create(DeactivateAccountApi.class);
        Call<ResponseStatus> call = api.deactivateAccount(id, pass, reason, AppConfig.API_KEY);
        call.enqueue(new Callback<ResponseStatus>() {
            @Override
            public void onResponse(Call<ResponseStatus> call, retrofit2.Response<ResponseStatus> response) {
                if (response.code() == 200) {
                    ResponseStatus resStatus = response.body();
                    if (resStatus.getStatus().equalsIgnoreCase("success")) {
                        logoutUser();

                        new ToastMsg(ProfileActivity.this).toastIconSuccess(resStatus.getData());

                        if (PreferenceUtils.isMandatoryLogin(ProfileActivity.this)) {
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        alertDialog.dismiss();
                        finish();
                    } else {
                        new ToastMsg(ProfileActivity.this).toastIconError(resStatus.getData());
                        alertDialog.dismiss();
                    }

                } else {
                    new ToastMsg(ProfileActivity.this).toastIconError("Something went wrong");
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseStatus> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(ProfileActivity.this).toastIconError("Something went wrong");
                alertDialog.dismiss();
            }
        });
    }

    public void logoutUser() {
        DatabaseHelper databaseHelper = new DatabaseHelper(ProfileActivity.this);
        databaseHelper.deleteAllDownloadData();
        databaseHelper.deleteUserData();
        databaseHelper.deleteAllActiveStatusData();

        SharedPreferences.Editor sp = getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
        sp.putBoolean(Constants.USER_LOGIN_STATUS, false);
        sp.apply();
        sp.commit();
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    userIv.setImageURI(selectedImage);
                    imageUri = selectedImage;
                }
                break;
        }
    }

    private void getProfile(boolean update) {
//        progressBar.setVisibility(View.VISIBLE);
        showLoader();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        UserDataApi api = retrofit.create(UserDataApi.class);
        Call<User> call = api.getUserData(AppConfig.API_KEY, id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {

                        User user = response.body();
                        DatabaseHelper db = new DatabaseHelper(ProfileActivity.this);
                        db.deleteUserData();
                        db.insertUserData(user);
                        ApiResources.USER_PHONE = user.getPhone();

                        etName.setText(user.getName());
                        etEmail.setText(user.getEmail());
                        etPhone.setText(user.getPhone());
//                        if (user.getGender() == null) {
//                            genderSpinner.setText(R.string.male);
//                        } else {
//                            genderSpinner.setText(user.getGender());
//                           /* if(user.getGender().equals("Female")){
//                                genderSpinner.setSelection(1);
//                            }else{
//                                genderSpinner.setSelection(0);
//                            }*/
//                            selectedGender = user.getGender();
//                        }


                        String ccode = user.getCountry_code();
                        if (ccode != null) {
                            if (user.getPhone().contains(ccode))

                                addUserToCleverTap(user.getName(), user.getUserId(), user.getEmail(), user.getPhone());

                            else
                                addUserToCleverTap(user.getName(), user.getUserId(), user.getEmail(), ccode + user.getPhone());

                        } else {
                            addUserToCleverTap(user.getName(), user.getUserId(), user.getEmail(), "+91" + user.getPhone());
                        }
                        
                        if (!user.isPasswordAvailable()) {
                            btnUpdate.setVisibility(View.VISIBLE);
                            etCurrentPassword.setVisibility(View.GONE);
                            etPass.setVisibility(View.GONE);
                            setPasswordBtn.setVisibility(View.VISIBLE);
                        }

                        if (update) {
                            if (from.equals("main")) {
                                finish();
                            }
                        }
                        hideLoader();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
                hideLoader();
            }
        });


    }

    public void addUserToCleverTap(String name, String id, String email, String mobile) {
        HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
        profileUpdate.put("Name", name);    // String
        profileUpdate.put("Identity", id);      // String or number
        profileUpdate.put("Email", email); // Email address of the user
        profileUpdate.put("Phone", mobile);   // Phone (with the country code, starting with +)
        //  profileUpdate.put("Gender", "M");             // Can be either M or F
        //  profileUpdate.put("DOB", new Date());         // Date of Birth. Set the Date object to the appropriate value first
        //optional fields. controls whether the user will be sent email, push etc.
           profileUpdate.put("MSG-email", true);        // Disable email notifications
        profileUpdate.put("MSG-push", true);          // Enable push notifications
           profileUpdate.put("MSG-sms", true);          // Disable SMS notifications
          profileUpdate.put("MSG-whatsapp", true);      // Enable WhatsApp notifications
        // ArrayList<String> stuff = new ArrayList<String>();
        // stuff.add("bag");
        // stuff.add("shoes");
        // profileUpdate.put("MyStuff", stuff);                        //ArrayList of Strings
        // String[] otherStuff = {"Jeans", "Perfume"};
        //  profileUpdate.put("MyStuff", otherStuff);                   //String Array
        clevertapDefaultInstance.pushProfile(profileUpdate);

    }


    private void updateProfile(String idString, String emailString, String phoneString, String nameString, String passString, String currentPassString) {
        showLoader();
        File file = null;
        RequestBody requestFile = null;
//        MultipartBody.Part multipartBody = null;
//        try {
//            if (imageUri != null) {
//                file = FileUtil.from(ProfileActivity.this, imageUri);
//                requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),
//                        file);
//
//                multipartBody = MultipartBody.Part.createFormData("photo",
//                        file.getName(), requestFile);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }



        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), emailString);
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), idString);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), nameString);
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), passString);
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), phoneString);
        RequestBody currentPass = RequestBody.create(MediaType.parse("text/plain"), currentPassString);
        RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), selectedGender);
        RequestBody key = RequestBody.create(MediaType.parse("text/plain"), AppConfig.API_KEY);

        /* password, currentPass,*/
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        ProfileApi api = retrofit.create(ProfileApi.class);
//        Call<ResponseStatus> call = api.updateProfile(AppConfig.API_KEY, id, name, email, phone, multipartBody, gender);
        Call<ResponseStatus> call = api.updateProfile(AppConfig.API_KEY, id, name, email, phone);
        call.enqueue(new Callback<ResponseStatus>() {
            @Override
            public void onResponse(Call<ResponseStatus> call, retrofit2.Response<ResponseStatus> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        new ToastMsg(ProfileActivity.this).toastIconSuccess(response.body().getData());
                        getProfile(true);
                    } else {
                        new ToastMsg(ProfileActivity.this).toastIconError(response.body().getData());
                    }
                } else {
                    new ToastMsg(ProfileActivity.this).toastIconError(getString(R.string.something_went_wrong));
                }
//                progressBar.setVisibility(View.GONE);
                hideLoader();
            }

            @Override
            public void onFailure(Call<ResponseStatus> call, Throwable t) {
                new ToastMsg(ProfileActivity.this).toastIconError(getString(R.string.something_went_wrong));
//                progressBar.setVisibility(View.GONE);
                hideLoader();
                Log.e(TAG, t.getLocalizedMessage());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.password_entry_layout, null);
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        EditText passEt = view.findViewById(R.id.passwordEt);
        EditText confirmPassEt = view.findViewById(R.id.confirmPasswordEt);
        Button setButton = view.findViewById(R.id.setButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passEt.getText().toString();
                String confirmPass = confirmPassEt.getText().toString();
                if (!password.isEmpty() && !confirmPass.isEmpty()) {
                    if (password.equals(confirmPass)) {
                        // send password to server
                        alertDialog.dismiss();
                        setPassword(password);
                    } else {
                        confirmPassEt.setError("Password mismatch.");
                        new ToastMsg(view.getContext()).toastIconError("Password mismatch.");
                    }
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private void setPassword(String password) {
        ProgressDialog dialog = new ProgressDialog(ProfileActivity.this);
        dialog.setMessage("Please wait..");
        dialog.setCancelable(false);
        dialog.show();
        //get UID from firebase auth
//        String uid = "";
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null)
//            uid = user.getUid();
//        else {
//            dialog.dismiss();
//            new ToastMsg(ProfileActivity.this).toastIconError(getString(R.string.something_went_text));
//            return;
//        }

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SetPasswordApi api = retrofit.create(SetPasswordApi.class);
        Call<ResponseStatus> call = api.setPassword(AppConfig.API_KEY, id, password);
        call.enqueue(new Callback<ResponseStatus>() {
            @Override
            public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        ResponseStatus status = response.body();
                        if (status.getStatus().equalsIgnoreCase("success")) {
                            new ToastMsg(ProfileActivity.this).toastIconSuccess("Password set successfully.");
                            //password set successfully.
                            //visible hidden buttons
                            setPasswordBtn.setVisibility(View.GONE);
                            btnUpdate.setVisibility(View.VISIBLE);
                            etCurrentPassword.setVisibility(View.VISIBLE);
                            etPass.setVisibility(View.VISIBLE);
                            getProfile(false);

                        } else {
                            new ToastMsg(ProfileActivity.this).toastIconError(getString(R.string.something_went_text));
                        }
                    } else {
                        new ToastMsg(ProfileActivity.this).toastIconError(getString(R.string.something_went_text));
                    }
                } else {
                    new ToastMsg(ProfileActivity.this).toastIconError(getString(R.string.something_went_text));
                }

                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseStatus> call, Throwable t) {
                new ToastMsg(ProfileActivity.this).toastIconError(getString(R.string.something_went_text));
                Log.e("ProfileActivity", t.getLocalizedMessage());
                dialog.dismiss();
            }
        });
    }


    private void sendOtp(String idString, String emailString, String phoneString, String nameString, String passString, String currentPassString) {
        dialog.show();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        LoginApi api = retrofit.create(LoginApi.class);
        Call<ResponseBody> call = api.verify_otp(AppConfig.API_KEY, etPhone.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String status = jsonObject.getString("status");
                        if (status.equals("success")) {
                            Toast.makeText(ProfileActivity.this, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                            intentOtp = jsonObject.getString("otp");
                            otpDialog(idString
                                    , emailString
                                    , phoneString
                                    , nameString
                                    , passString
                                    , currentPassString);
//                            progressBar.setVisibility(View.GONE);
//                            startMobileTimer();
                            hideLoader();
                        } else {
                            Toast.makeText(ProfileActivity.this, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.cancel();
                  /*  if (response.body().getStatus().equals("success")) {
//                        Toast.makeText(ProfileActivity.this, deviceId, Toast.LENGTH_LONG).show();
                        User user = response.body();
                        DatabaseHelper db = new DatabaseHelper(ProfileActivity.this);
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
                hideLoader();
            }
        });
    }


    private void showLoader() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideLoader() {
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    private void otpDialog(String idString, String emailString, String phoneString, String nameString, String passString, String currentPassString) {
        final Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //  dialog.getWindow().setLayout(((getWidth(this) / 100) * 90), LinearLayout.LayoutParams.MATCH_PARENT);
        //  dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setDimAmount((float) 0.6);
        dialog.setContentView(R.layout.otp_dialog_layout);


        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
        dialog.getWindow().setLayout(width, height);

        dialog.getWindow().setGravity(Gravity.CENTER);


        TextView txt_otp, txt_resend, txt_change_no;
        AppCompatEditText otp_edit_box1, otp_edit_box2, otp_edit_box3, otp_edit_box4;
        AppCompatButton submit = dialog.findViewById(R.id.submit);

        txt_otp = dialog.findViewById(R.id.txt_otp);
        txt_resend = dialog.findViewById(R.id.txt_resend);
        txt_change_no = dialog.findViewById(R.id.txt_change_no);
        otp_edit_box1 = dialog.findViewById(R.id.otp_edit_box1);
        otp_edit_box2 = dialog.findViewById(R.id.otp_edit_box2);
        otp_edit_box3 = dialog.findViewById(R.id.otp_edit_box3);
        otp_edit_box4 = dialog.findViewById(R.id.otp_edit_box4);
        OtpView otp_view = dialog.findViewById(R.id.otp_view);
        OtpView otp_viewIndia = dialog.findViewById(R.id.otp_viewIndia);

        txt_otp.setText("Enter OTP sent on " + etPhone.getText().toString());

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

        txt_resend.setOnClickListener(view -> {

            if (ccp.getSelectedCountryCode().contains("91")) {
                sendOtp(idString,
                        emailString,
                        phoneString,
                        nameString,
                        passString,
                        currentPassString);
            } else {
                if (etPhone.getText().toString().contains("+")) {
                    resendVerificationCode(phoneString, verificationToken);
                } else {
                    resendVerificationCode("+" + ccp.getSelectedCountryCode() + phoneString, verificationToken);
                }

            }
        });

        otp_view.setOtpCompletionListener(otp -> userEnteredOtp = otp);

        otp_viewIndia.setOtpCompletionListener(otp -> userEnteredOtp = otp);

        if (ccp.getSelectedCountryCode().contains("91")) {
            otp_viewIndia.setVisibility(View.VISIBLE);
            otp_view.setVisibility(View.GONE);
        } else {
            otp_viewIndia.setVisibility(View.GONE);
            otp_view.setVisibility(View.VISIBLE);
        }

        submit.setOnClickListener(view -> {
//            String otp = otp_edit_box1.getText().toString() +
//                    otp_edit_box2.getText().toString()
//                    + otp_edit_box3.getText().toString()
//                    + otp_edit_box4.getText().toString();

            if (ccp.getSelectedCountryCode().contains("91")) {
                if (userEnteredOtp.equals("")) {
                    Toast.makeText(ProfileActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                } else if (!userEnteredOtp.equals(intentOtp)) {
                    Toast.makeText(ProfileActivity.this, "Enter Valid OTP", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    updateProfile(idString, emailString, phoneString, nameString, passString, currentPassString);
                }
            } else {
                if (userEnteredOtp.equals("")) {
                    Toast.makeText(ProfileActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                    verifyCode(userEnteredOtp);
                }
            }
        });


        txt_change_no.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }


    private void startMobileTimer(TextView text) {
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                text.setText("0 : " + String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                text.setText("Resend OTP");
            }
        }.start();
    }

}
