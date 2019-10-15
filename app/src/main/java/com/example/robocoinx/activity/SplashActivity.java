package com.example.robocoinx.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.robocoinx.R;
import com.example.robocoinx.logic.CryptEx;
import com.example.robocoinx.logic.FileManager;
import com.example.robocoinx.logic.RoboHandler;
import com.example.robocoinx.model.ProfileView;
import com.example.robocoinx.model.StaticValues;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBar();
        setContentView(R.layout.splash_main);
        String encode1 = CryptEx.toBaseEncode(StaticValues.URL_BASE, StaticValues.KEY);
        String encode2 = CryptEx.toBaseEncode(StaticValues.URL_HOME, StaticValues.KEY);
        System.out.println("encode1="+encode1);
        System.out.println("encode2="+encode2);
        String decode1 = CryptEx.toBaseDecode(encode1, StaticValues.KEY);
        String decode2 = CryptEx.toBaseDecode(encode2, StaticValues.KEY);
        System.out.println("decode1="+decode1);
        System.out.println("decode2="+decode2);
//        new Content().execute((Void)null);
    }

    private void hideTitleBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    @SuppressLint("StaticFieldLeak")
    public class Content extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                checkUserCache();
            }catch (Exception e){
                e.printStackTrace();
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
            String csrfToken = RoboHandler.getCsrfToken(getApplicationContext());
            if(csrfToken != null) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }else {
                runOnUiThread(() -> {
                    // TODO show error
                    Toast.makeText(getApplicationContext(), "Sorry application under maintenance!!", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }
}
