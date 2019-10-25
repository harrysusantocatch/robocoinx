package com.example.robocoinx.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.robocoinx.R;
import com.example.robocoinx.logic.CryptEx;
import com.example.robocoinx.logic.FileManager;
import com.example.robocoinx.logic.RoboHandler;
import com.example.robocoinx.model.request.SignupRequest;
import com.example.robocoinx.model.view.ProfileView;
import com.example.robocoinx.model.StaticValues;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBar();
        setContentView(R.layout.splash_main);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
//        new Content().execute((Void)null);
        loadWebview();
    }

    private void hideTitleBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do here
                    FileManager.getInstance().appendLog("first....");
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class Content extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                checkUserCache();
//                InputStream inputStream = getAssets().open("signup.html");
//                Document document = Jsoup.parse(inputStream, "UTF-8", "");
//                SignupRequest x = new SignupRequest(document);
//                System.out.println(x);
            }catch (Exception e){
                e.printStackTrace();
                FileManager.getInstance().appendLog(e);
            }
            return null;
        }

        private void checkUserCache() {
            if(FileManager.getInstance().fileExists(getApplicationContext(), StaticValues.USER_CACHE)){
                goToHome();
            }else {
                goToLogin();
            }
        }

        private void goToHome() {
            Object obj = RoboHandler.parsingHomeResponse(getApplicationContext());
            if(obj instanceof ProfileView) {
                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                intent.putExtra(StaticValues.PROFILE_VIEW, (ProfileView)obj);
                startActivity(intent);
            }else {
                // TODO show error
            }
        }

        private void goToLogin() {
            SignupRequest signupRequest = RoboHandler.getSignUpRequest(getApplicationContext());
            if(signupRequest != null) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                intent.putExtra(StaticValues.SIGNUP_REQ, signupRequest);
                startActivity(intent);
            }else {
                runOnUiThread(() -> {
                    // TODO show error
                    Toast.makeText(getApplicationContext(), "Sorry application under maintenance!!", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    private void loadWebview() {

        WebView webView = new WebView(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.getSettings().setSafeBrowsingEnabled(false);
        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView webview, String url) {
                String javascript = "(function() { return { $.fingerprint() }; })();";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    webview.evaluateJavascript(javascript, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            System.out.println(s);
                        }
                    });
                } else {
                    webview.loadUrl("javascript:(function(){" + javascript + "})()");
                }
            }
        });
        webView.loadUrl(CryptEx.toBaseDecode(StaticValues.URL_KEY_B));
    }
}
