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
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplashActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);
        new Content().execute((Void)null);

        try {
            InputStream inputStream = getAssets().open("home.html");
            Document document = Jsoup.parse(inputStream, "UTF-8", "");
            String balance = getBalance(document);
            String userID = "";
            String rewardPoints = getRP(document);
            int nextR = getNextR(document);
            int btcBonusCountDown = getBTCBonusCountDown(document);
            int rpBonusCountDown = getRPBonusCountDown(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getRPBonusCountDown(Document doc) {
        String[] split = doc.html().split("BonusEndCountdown\\(\"fp_bonus\",");
        if(split.length > 1){
            Pattern pattern = Pattern.compile("[0-9]*.\\)");
            Matcher matcher = pattern.matcher(split[1]);
            if(matcher.find()){
                return Integer.parseInt(matcher.group().substring(0, matcher.group().length()-1));
            }
        }
        return 0;
    }

    private int getBTCBonusCountDown(Document doc) {
        String[] split = doc.html().split("BonusEndCountdown\\(\"free_points\",");
        if(split.length > 1){
            Pattern pattern = Pattern.compile("[0-9]*.\\)");
            Matcher matcher = pattern.matcher(split[1]);
            if(matcher.find()){
                return Integer.parseInt(matcher.group().substring(0, matcher.group().length()-1));
            }
        }
        return 0;
    }

    private String getBalance(Document doc) {
        String balance = null;
        Element elBalance = doc.getElementById("balance");
        balance = elBalance.text();
        System.out.println("balance ="+balance);
        return balance;
    }

    private String getRP(Document doc) {
        String rp = null;
        Elements elRP = doc.getElementsByClass("reward_table_box br_0_0_5_5 user_reward_points font_bold");
        rp = elRP.text();
        System.out.println("reward point ="+rp);
        return rp;
    }

    private int getNextR(Document doc) {
        String data = doc.html();
        if(data.contains("free_play_time_remaining")){
            String matcher = extractCountDown(data, "free_play_time_remaining");
            if (matcher != null) return (Integer.parseInt(matcher));
        }else if(data.contains("#time_remaining")){
            String matcher = extractCountDown(data, "#time_remaining");
            if (matcher != null) return (Integer.parseInt(matcher));
        }
        return 0;
    }

    private String extractCountDown(String data, String regex) {
        String[] array = data.split(regex);
        String input = array[1];
        Pattern pattern = Pattern.compile("\\+.[0-9]*.\\,");
        Matcher matcher = pattern.matcher(input);
        if(matcher.find()){
            return matcher.group().substring(1, matcher.group().length()-1);
        }
        return null;
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
