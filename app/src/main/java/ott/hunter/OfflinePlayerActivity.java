//package ott.hunter;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.app.ActionBar;
//import android.content.pm.ActivityInfo;
//import android.os.Build;
//import android.os.Bundle;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.ImageView;
//
//import com.potyvideo.library.AndExoPlayerView;
//import com.potyvideo.library.globalInterfaces.AndExoPlayerListener;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.InvalidKeyException;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import javax.crypto.Cipher;
//import javax.crypto.CipherInputStream;
//import javax.crypto.NoSuchPaddingException;
//import javax.crypto.spec.SecretKeySpec;
//
//import ott.hunter.utils.Constants;
//
//
//public class OfflinePlayerActivity extends AppCompatActivity implements AndExoPlayerListener {
//
//    AndExoPlayerView andExoPlayerView;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
////        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_offline_player);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//
//        andExoPlayerView = findViewById(R.id.andExoPlayerView);
//        String stream_url = getIntent().getStringExtra("video_url");
////        File file = new File(stream_url);
//
////        try {
////            decrypt(stream_url,cryptPassword);
////        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
////            e.printStackTrace();
////        }
//        HashMap<String, String> extraHeaders = new HashMap<>();
//        extraHeaders.put("foo", "poo");
//        andExoPlayerView.setSource("STREAM_URL", extraHeaders);
//        andExoPlayerView.setSource(stream_url, extraHeaders);
//
//        ImageView des_back_iv = findViewById(R.id.des_back_iv);
//
//        des_back_iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });
//
////        andExoPlayerView.setSource(stream_url);
//    }
//
//    @Override
//    public void onBackPressed() {
//      /*  if (activeMovie || isFullScr) {
//            setPlayerNormalScreen();
//            if (player != null) {
//                player.setPlayWhenReady(false);
//                player.stop();
//            }
//            showDescriptionLayout();
//            activeMovie = false;
//        } else {
//            releasePlayer();
//        }*/
//        super.onBackPressed();
//    }
//
//    @Override
//    public void onExoBuffering() {
//
//    }
//
//    @Override
//    public void onExoEnded() {
//
//    }
//
//    @Override
//    public void onExoIdle() {
//
//    }
//
//    @Override
//    public void onExoPlayerError(@Nullable String s) {
//
//    }
//
//    @Override
//    public void onExoPlayerFinished() {
//
//    }
//
//    @Override
//    public void onExoPlayerLoading() {
//
//    }
//
//    @Override
//    public void onExoPlayerStart() {
//
//    }
//
//    @Override
//    public void onExoReady() {
//
//    }
//
//    private static final String salt = "t784";
//    private static final String cryptPassword = "873147cbn9x5'2 79'79314";
//
//    public void decrypt(String path,String Pass) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
//        FileInputStream fis = new FileInputStream(path);
//        FileOutputStream fos = new FileOutputStream(path.replace(".crypt",""));
//        byte[] key = (salt + Pass).getBytes("UTF-8");
//        MessageDigest sha = MessageDigest.getInstance("SHA-1");
//        key = sha.digest(key);
//        key = Arrays.copyOf(key,16);
//        SecretKeySpec sks = new SecretKeySpec(key, "AES");
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.DECRYPT_MODE, sks);
//        CipherInputStream cis = new CipherInputStream(fis, cipher);
//        int b;
//        byte[] d = new byte[8];
//        while((b = cis.read(d)) >= 0) {
//            fos.write(d, 0, b);
//        }
//        fos.flush();
//        fos.close();
//        cis.close();
//
//    }
//
//}