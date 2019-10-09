package com.example.robocoinx.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.robocoinx.R;
import com.example.robocoinx.logic.FileManager;
import com.example.robocoinx.logic.RoboHandler;
import com.example.robocoinx.model.ProfileView;
import com.example.robocoinx.model.StaticValues;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBar();
        setContentView(R.layout.splash_main);
        new Content().execute((Void)null);
    }

    private void hideTitleBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
    }

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
            long d1 = System.currentTimeMillis();
            boolean userExist = FileManager.getInstance().fileExists(getApplicationContext(), StaticValues.USER_CACHE);
            long d2 = System.currentTimeMillis();
            System.out.println("F.----------------= "+(d2-d1));
            if(userExist){
                goToHome();
            }else {
                goToLogin();
            }
        }

        private void goToHome() {
            ProfileView profileView = RoboHandler.parsingHomeResponse(getApplicationContext());
            Intent intent = new Intent(getBaseContext(), HomeActivity.class);
            intent.putExtra(StaticValues.PROFILE_VIEW, profileView);
            startActivity(intent);
        }

        private void goToLogin() {
            String csrfToken = RoboHandler.getCsrfToken(getApplicationContext());
            if(csrfToken != null) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // ganti ke halaman error
                        Toast.makeText(getApplicationContext(), "Sorry application under maintenance!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
