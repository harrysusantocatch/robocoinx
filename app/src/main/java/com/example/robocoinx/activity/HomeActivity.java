package com.example.robocoinx.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.example.robocoinx.R;
import com.example.robocoinx.model.ProfileView;
import com.example.robocoinx.model.StaticValues;

import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    int counter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setView();
    }

    private void setView() {
        TextView userID = findViewById(R.id.textViewUserId);
        TextView balance = findViewById(R.id.textViewBalance);
        final TextView nextRoll = findViewById(R.id.textViewnextRoll);
        TextView rp = findViewById(R.id.textViewRP);
        final TextView rpBonus = findViewById(R.id.textViewRPBonus);
        final TextView btcBonus = findViewById(R.id.textViewBTCBonus);
        final ProfileView pp = (ProfileView) getIntent().getSerializableExtra(StaticValues.PROFILE_VIEW);
        userID.setText(pp.getUserID());
        balance.setText(pp.getBalance());
        rp.setText(pp.getRewardPoint());
        System.out.println("-------------------"+pp.getNextRollTime());

        // next roll
        new CountDownTimer(Long.valueOf(pp.nextRollTime)*1000, 1000){

            @Override
            public void onTick(long millis) {
                String hms = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millis) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                        TimeUnit.MILLISECONDS.toSeconds(millis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                nextRoll.setText(hms);
            }

            @Override
            public void onFinish() {
                nextRoll.setText("Roll");
            }
        }.start();

        // reward point bonus
        new CountDownTimer(Long.valueOf(pp.rpBonusTime)*1000, 1000){

            @Override
            public void onTick(long millis) {
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                        TimeUnit.MILLISECONDS.toSeconds(millis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                rpBonus.setText(hms);
            }

            @Override
            public void onFinish() {
                rpBonus.setText("Roll");
            }
        }.start();

        // btc bonus

        new CountDownTimer(Long.valueOf(pp.btcBonusTime)*1000, 1000){

            @Override
            public void onTick(long millis) {
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                        TimeUnit.MILLISECONDS.toSeconds(millis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                btcBonus.setText(hms);
            }

            @Override
            public void onFinish() {
                btcBonus.setText("Roll");
            }
        }.start();
    }
}
