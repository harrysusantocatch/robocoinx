package com.example.robocoinx.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;

import com.example.robocoinx.R;
import com.example.robocoinx.logic.Cache;
import com.example.robocoinx.logic.RoboHandler;
import com.example.robocoinx.model.StaticValues;
import com.example.robocoinx.model.UserCache;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class SplashActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);
        new Content().execute((Void)null);

//        try {
//            InputStream inputStream = getAssets().open("home.html");
//            File file = new File("test");
//            FileUtils.copy(inputStream, new FileOutputStream(file));
//            Document document = Jsoup.parse(file, "UTF-8");
//            Element elClientSeed = document.getElementById("next_client_seed");
//            String clientSeed = elClientSeed.val();
//            System.out.println(clientSeed);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void checkCsrfToken() {
        UserCache userCache = null;
        try {
            FileInputStream fileInputStream = openFileInput(StaticValues.USER_CACHE);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            userCache = (UserCache)objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
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
