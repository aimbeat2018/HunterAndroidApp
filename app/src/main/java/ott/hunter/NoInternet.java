package ott.hunter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import ott.hunter.nav_fragments.DownloadNewFragment;

public class NoInternet extends AppCompatActivity {

    Button retry_connection,view_download;
    LinearLayout ll_retry_connection,frame_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        view_download = findViewById(R.id.view_offline);
        retry_connection = findViewById(R.id.retry);
        ll_retry_connection = findViewById(R.id.ll_retry_connection);
        frame_layout = findViewById(R.id.frame_layout);


        view_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ll_retry_connection.setVisibility(View.GONE);
                frame_layout.setVisibility(View.VISIBLE);

                loadFragment(new DownloadNewFragment());

            }
        });

        retry_connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(NoInternet.this, SplashScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean loadFragment(Fragment fragment) {

        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;

    }


}