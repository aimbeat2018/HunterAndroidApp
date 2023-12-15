package ott.hunter.more;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;

import ott.hunter.AppConfig;
import ott.hunter.MoreActivity;
import ott.hunter.R;
import ott.hunter.network.RetrofitClient;
import ott.hunter.network.apis.PassResetApi;
import ott.hunter.utils.PreferenceUtils;
import ott.hunter.utils.RtlUtils;
import ott.hunter.utils.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HelpActivity extends AppCompatActivity {

    LinearLayout call_us, mail_us;
//    TextView mobile, email;
    ImageView img_call, img_mail, imgback;
    String strissue = "";
    String[] issuelist = {"Select Issue", "Partnership", "Upcoming Movie/series", "Application Issue", "Login Issue", "Subscription Issue", "Other"};
    Spinner spinnerissue;
    Button submit;
    TextInputEditText name, emailid, number, issue, message;
    String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RtlUtils.setScreenDirection(this);
        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        boolean isDark = sharedPreferences.getBoolean("dark", false);

        userId = PreferenceUtils.getUserId(HelpActivity.this);

        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.subscription_toolbar);

        img_call = findViewById(R.id.img_call);

        name = findViewById(R.id.edtname);
        number = findViewById(R.id.edtmobile);
        emailid = findViewById(R.id.edtemail);
        message = findViewById(R.id.edtmessage);


        submit = findViewById(R.id.submit);
        img_mail = findViewById(R.id.img_mail);
        imgback = findViewById(R.id.imgback);
        call_us = findViewById(R.id.call_us);
        spinnerissue = findViewById(R.id.spinnerissue);
        mail_us = findViewById(R.id.mail_us);

        ArrayAdapter favour = new ArrayAdapter(this, android.R.layout.simple_spinner_item, issuelist);
        favour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerissue.setAdapter(favour);

        spinnerissue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                strissue = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this, MoreActivity.class);
                startActivity(intent);

            }
        });


        if (!isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.black_window_light));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Help");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //---analytics-----------
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "help_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

//        mobile = findViewById(R.id.t4);
//        email = findViewById(R.id.t6);
        call_us = findViewById(R.id.call_us);
        mail_us = findViewById(R.id.mail_us);

        call_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                // intent.setData(Uri.parse("tel:" + mobile.getText()));
                intent.setData(Uri.parse("tel:" + "8377003507"));
                //System.out.println("OnItemClickListener ==> listData ==> " + mobile.getText());
                System.out.println("OnItemClickListener ==> listData ==> " + "8377003507");
                startActivity(intent);
            }
        });


        mail_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sendEmail(email.getText().toString());
                sendEmail();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (number.getText().toString().isEmpty() && message.getText().toString().isEmpty()) {
                    new ToastMsg(HelpActivity.this).toastIconSuccess("Enter Mobile and message");

                } else {
                    savefeedback();

               }

            }
        });
    }


    private void savefeedback() {
        //dialog.show();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PassResetApi passResetApi = retrofit.create(PassResetApi.class);
        Call<FeedbackformdResponse> call = passResetApi.add_feedback(AppConfig.API_KEY, userId, name.getText().toString(), number.getText().toString(), emailid.getText().toString(), strissue, message.getText().toString());
        call.enqueue(new Callback<FeedbackformdResponse>() {
            @Override
            public void onResponse(Call<FeedbackformdResponse> call, Response<FeedbackformdResponse> response) {
                if (response.code() == 200) {
                    FeedbackformdResponse pr = response.body();
                    if (pr.getStatus().equals("success")) {
                        new ToastMsg(HelpActivity.this).toastIconSuccess(pr.getMessage());

                        //  Intent intent = new Intent(HelpActivity.this, LoginActivity.class);
                        //  startActivity(intent);
                        //dialog.cancel();
                    } else {
                        new ToastMsg(HelpActivity.this).toastIconError(pr.getMessage());
                        // dialog.cancel();
                    }
                }
            }

            @Override
            public void onFailure(Call<FeedbackformdResponse> call, Throwable t) {
                new ToastMsg(HelpActivity.this).toastIconError("Something went wrong." + t.getMessage());
                // dialog.cancel();
                t.printStackTrace();
            }
        });
    }


    private void sendEmail() {
        Log.i("Send email", "");
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        // emailIntent.setData(Uri.parse("mailto:admin@cineprime.app"));
        // emailIntent.setData(Uri.parse("mailto:info@primeplay.co.in"));
        emailIntent.setType("text/plain");

        //  emailIntent .setType("vnd.android.cursor.dir/email");
        //  String to[] = {"info@hunter.co.in"};

        String to[] = {"customercare@hunters.co.in"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);

        // emailIntent.putExtra(Intent.EXTRA_EMAIL, "admin@cineprime.app");
        //  emailIntent.putExtra(Intent.EXTRA_EMAIL, "info@hunter.co.in");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hunter Customer Query ");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            Log.i("Finished sending email.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(HelpActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}