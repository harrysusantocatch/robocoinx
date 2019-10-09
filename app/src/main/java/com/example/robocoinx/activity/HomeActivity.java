package com.example.robocoinx.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robocoinx.R;
import com.example.robocoinx.logic.BackgroundService;
import com.example.robocoinx.logic.FileManager;
import com.example.robocoinx.logic.RoboHandler;
import com.example.robocoinx.model.ProfileView;
import com.example.robocoinx.model.StaticValues;
import com.example.robocoinx.model.UserCache;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    private WebView webView;
    private TextView balance;
    private TextView nextRoll;
    private TextView rp;
    private TextView rpBonus;
    private TextView btcBonus;
    private TextView userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setView();
    }

    private void injectJS(){
        String content = null;

        try {
            InputStream stream = getAssets().open("roll.js");

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            content = new String(buffer);
        } catch (IOException e) {
            // Handle exceptions here
        }
        if(content != null) webView.loadUrl("javascript:("+content+")()");
    }

    private void setView() {
        webView = findViewById(R.id.webView);
        userID = findViewById(R.id.textViewUserId);
        balance = findViewById(R.id.textViewBalance);
        nextRoll = findViewById(R.id.textViewnextRoll);
        rp = findViewById(R.id.textViewRP);
        rpBonus = findViewById(R.id.textViewRPBonus);
        btcBonus = findViewById(R.id.textViewBTCBonus);
        ProfileView pp = (ProfileView) getIntent().getSerializableExtra(StaticValues.PROFILE_VIEW);
        setValueUI(pp);

        Button btnStart = findViewById(R.id.buttonStart);
        Button btnStop = findViewById(R.id.buttonStop);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), BackgroundService.class);
                startService(intent);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), BackgroundService.class);
                stopService(intent);
            }
        });
    }

    private void setValueUI(final ProfileView pp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                userID.setText(pp.getUserID());
                balance.setText(pp.getBalance());
                rp.setText(pp.getRewardPoint());

                // next roll
                new CountDownTimer(Long.valueOf(pp.nextRollTime)*1000, 1000){

                    @Override
                    public void onTick(long millis) {
                        String hms = String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(millis) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                TimeUnit.MILLISECONDS.toSeconds(millis) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                        nextRoll.setText(hms);
                    }

                    @Override
                    public void onFinish() {
                        nextRoll.setText("finish");
//                        doRoll();
                    }
                }.start();

                // reward point bonus
                new CountDownTimer(Long.valueOf(pp.rpBonusTime)*1000, 1000){

                    @Override
                    public void onTick(long millis) {
                        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                TimeUnit.MILLISECONDS.toMinutes(millis) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                TimeUnit.MILLISECONDS.toSeconds(millis) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                        rpBonus.setText(hms);
                    }

                    @Override
                    public void onFinish() {
                        rpBonus.setText("finish");
                    }
                }.start();

                // btc bonus

                new CountDownTimer(Long.valueOf(pp.btcBonusTime)*1000, 1000){

                    @Override
                    public void onTick(long millis) {
                        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                TimeUnit.MILLISECONDS.toMinutes(millis) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                TimeUnit.MILLISECONDS.toSeconds(millis) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                        btcBonus.setText(hms);
                    }

                    @Override
                    public void onFinish() {
                        btcBonus.setText("finish");
                    }
                }.start();
            }
        });
    }

    private void doRoll() {
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectJS();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        new Content().execute((Void)null);
//                    }
//                }, 5000);
            }
        });
        String strUserCache = FileManager.getInstance().readFile(getApplicationContext(), StaticValues.USER_CACHE);
        UserCache userCache = new Gson().fromJson(strUserCache, UserCache.class);
        String hdr = "login_auth="+userCache.getLoginAuth()+"; btc_address="+userCache.getBtcAddress()+"; password="+userCache.getPassword();
        Map<String, String> headers = new HashMap<>();
        headers.put("cookie", hdr);
        webView.loadUrl(StaticValues.URL_HOME, headers);
    }

    public  class Content extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ProfileView profileView = RoboHandler.parsingHomeResponse(getApplicationContext());
                setValueUI(profileView);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

}
