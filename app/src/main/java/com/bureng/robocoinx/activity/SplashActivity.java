package com.bureng.robocoinx.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.bureng.robocoinx.R;
import com.bureng.robocoinx.contract.SplashContract;
import com.bureng.robocoinx.presenter.SplashPresenter;
import com.bureng.robocoinx.utils.CacheContext;
import com.bureng.robocoinx.utils.FileManager;
import com.bureng.robocoinx.model.db.Fingerprint;
import com.bureng.robocoinx.model.request.SignUpRequest;
import com.bureng.robocoinx.model.view.ProfileView;
import com.bureng.robocoinx.utils.StaticValues;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;

public class SplashActivity extends Activity implements SplashContract.View {

    private SplashContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);
        presenter = new SplashPresenter(this, this);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
        new Content().execute((Void)null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //do here
                FileManager.getInstance().appendLog("first....");
            }
        }
    }

    @Override
    public void goHome(ProfileView profileView) {
        Intent intent = new Intent(getBaseContext(), HomeActivity.class);
        intent.putExtra(StaticValues.PROFILE_VIEW, profileView);
        startActivity(intent);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void loadFingerprint(SignUpRequest signupRequest) {
//        Fingerprint fingerprint = new Fingerprint("3269417398", "458b9e977a393c3d9897392a768db437");
//        new CacheContext<>(Fingerprint.class, getApplicationContext()).save(fingerprint, StaticValues.FINGERPRINT);
//        Intent intentlogin = new Intent(getApplicationContext(), LoginActivity.class);
//        intentlogin.putExtra(StaticValues.SIGNUP_REQ, signupRequest);
//        startActivity(intentlogin);

        String html;
        try {
            InputStream inputStream = getAssets().open("finger.html");
            Document document = Jsoup.parse(inputStream, "UTF-8", "");
            String doc = document.html();
            html = doc.replace("[code]", signupRequest.script);
            WebView webView = findViewById(R.id.webviewx);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                webView.getSettings().setSafeBrowsingEnabled(false);
            }
            webView.getSettings().setJavaScriptEnabled(true);
            webView.addJavascriptInterface(new WebAppInterface(this, signupRequest), "Android");
            webView.loadDataWithBaseURL("blarg://ignored", html, "text/html", "UTF-8", "");
        }catch (Exception e){
            e.printStackTrace();
            FileManager.getInstance().appendLog(e);
        }
    }

    public class WebAppInterface {
        SignUpRequest signupRequest;
        Context context;

        WebAppInterface(Context c, SignUpRequest s) {
            context = c;
            signupRequest = s;
        }

        @JavascriptInterface
        public void getFingerprint(String data) {
            if(!data.contains("error")){
                String[] split = data.split(":");
                Fingerprint fingerprint = new Fingerprint(split[0], split[1]);
                new CacheContext<>(Fingerprint.class, context).save(fingerprint, StaticValues.FINGERPRINT);
                goToLogin(context, signupRequest);
            }
        }

        private void goToLogin(Context context, SignUpRequest signupRequest) {
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
                return presenter.getResponse();
            }catch (Exception e){
                e.printStackTrace();
                FileManager.getInstance().appendLog(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object obj) {
            if (obj instanceof ProfileView)
                goHome((ProfileView)obj);
            else if(obj instanceof SignUpRequest)
                loadFingerprint((SignUpRequest)obj);
            else
                showMessage((String)obj);
        }
    }
}