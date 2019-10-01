package com.example.robocoinx.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.example.robocoinx.R;
import com.example.robocoinx.logic.Cache;
import com.example.robocoinx.logic.RoboHandler;
import com.example.robocoinx.model.StaticValues;
import com.example.robocoinx.model.UserCache;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);
        new Content().execute((Void)null);
    }

    private void checkCsrfToken() {
        UserCache userCache = (UserCache) Cache.getInstance().getLru().get(StaticValues.USER_CACHE);
        if(userCache == null){
            RoboHandler.getCsrfToken();
            goToLogin();
        }else {
            goToHome();
        }
    }

    private void goToHome() {
        Intent intent = new Intent(getBaseContext(), HomeActivity.class);
        startActivity(intent);
    }

    private void goToLogin() {
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(intent);
    }

    public class Content extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                checkCsrfToken();
            }catch (Exception e){
                System.out.println(e.getStackTrace());
            }
            return null;
        }
    }
}
