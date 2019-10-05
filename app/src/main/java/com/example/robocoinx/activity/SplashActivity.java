package com.example.robocoinx.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.robocoinx.R;
import com.example.robocoinx.logic.FileManager;
import com.example.robocoinx.logic.RoboHandler;
import com.example.robocoinx.model.StaticValues;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);
        new Content().execute((Void)null);
    }
    public class Content extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                checkUserCache();
            }catch (Exception e){
                e.getStackTrace();
            }
            return null;
        }

        private void checkUserCache() {
            boolean userExist = FileManager.getInstance().fileExists(getApplicationContext(), StaticValues.USER_CACHE);
            if(userExist){
                goToHome();
            }else {
                goToLogin();
            }
        }

        private void goToHome() {
            Intent intent = new Intent(getBaseContext(), HomeActivity.class);
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
