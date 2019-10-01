package com.example.robocoinx.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.robocoinx.R;
import com.example.robocoinx.logic.Cache;
import com.example.robocoinx.logic.RoboHandler;
import com.example.robocoinx.model.StaticValues;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);
        checkCsrfToken();
    }

    private void checkCsrfToken() {
        String csrfToken = (String) Cache.getInstance().getLru().get(StaticValues.CSRF_TOKEN);
        if(csrfToken == null){
            RoboHandler.getCsrfToken();
            goToLogin();
        }
    }

    private void goToLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        }, 3000);
    }
}
