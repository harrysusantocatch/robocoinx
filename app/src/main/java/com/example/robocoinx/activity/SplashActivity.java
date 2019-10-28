package com.example.robocoinx.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.robocoinx.R;
import com.example.robocoinx.logic.FileManager;
import com.example.robocoinx.logic.FingerprintHandler;
import com.example.robocoinx.logic.RoboHandler;
import com.example.robocoinx.model.db.Fingerprint;
import com.example.robocoinx.model.request.SignupRequest;
import com.example.robocoinx.model.view.ProfileView;
import com.example.robocoinx.model.StaticValues;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    boolean loadingFinished = true;
    boolean redirect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBar();
        setContentView(R.layout.splash_main);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
        new Content().execute((Void)null);
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

    public class WebAppInterface {
        SignupRequest signupRequest;
        Context context;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c, SignupRequest s) {
            context = c;
            signupRequest = s;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void getFingerprint(String data) {
            if(!data.contains("error")){
                String[] split = data.split(":");
                Fingerprint fingerprint = new Fingerprint(split[0], split[1]);
                FingerprintHandler.getInstance(context).insert(fingerprint);
                goToLogin(context, signupRequest);
            }
        }

        private void goToLogin(Context context, SignupRequest signupRequest) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra(StaticValues.SIGNUP_REQ, signupRequest);
            context.startActivity(intent);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class Content extends AsyncTask<Void, Void, Object> {

        @Override
        protected Object doInBackground(Void... voids) {
            try {
                return checkUserCache();
            }catch (Exception e){
                e.printStackTrace();
                FileManager.getInstance().appendLog(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object obj) {
            if(obj instanceof ProfileView){
                ProfileView pp = (ProfileView)obj;
                goToHome(pp);
            }else if(obj instanceof SignupRequest){
                SignupRequest sg = (SignupRequest)obj;
                loadWebview(sg);
            }else {
                // TODO error
            }
        }

        private Object checkUserCache() {
            if(FileManager.getInstance().fileExists(getApplicationContext(), StaticValues.USER_CACHE)){
                return RoboHandler.parsingHomeResponse(getApplicationContext());
            }else {
                return RoboHandler.getSignUpRequest(getApplicationContext());
            }
        }

        private void goToHome(ProfileView pp) {
            Intent intent = new Intent(getBaseContext(), HomeActivity.class);
            intent.putExtra(StaticValues.PROFILE_VIEW, pp);
            startActivity(intent);
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

    private void loadWebview(SignupRequest signupRequest) {
        WebView webView = findViewById(R.id.webviewx);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.getSettings().setSafeBrowsingEnabled(false);
        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(this, signupRequest), "Android");
//        webView.setWebViewClient(new WebViewClient() {
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                loadingFinished = false;
//            }
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                if (!loadingFinished) {
//                    redirect = true;
//                }
//
//                loadingFinished = false;
//                webView.loadUrl(request.getUrl().toString());
//                return true;
//            }
//
//            public void onPageFinished(WebView webview, String url) {
//                if (!redirect) {
//                    loadingFinished = true;
//                    String javascript = "(function() { return { fingerprint }; })();";
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                        webview.evaluateJavascript(javascript, new ValueCallback<String>() {
//                            @Override
//                            public void onReceiveValue(String s) {
//                                System.out.println(s);
//                            }
//                        });
//                    } else {
//                        webview.loadUrl("javascript:(function(){" + javascript + "})()");
//                    }
//                } else {
//                    redirect = false;
//                }
//            }
//        });
        webView.loadUrl("file:///android_asset/finger.html");
    }
}