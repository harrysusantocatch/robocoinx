package com.example.robocoinx.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.robocoinx.R;
import com.example.robocoinx.logic.BackgroundService;
import com.example.robocoinx.logic.FileManager;
import com.example.robocoinx.logic.RoboHandler;
import com.example.robocoinx.model.ProfileView;
import com.example.robocoinx.model.RollErrorResponse;
import com.example.robocoinx.model.RollSuccessResponse;
import com.example.robocoinx.model.StaticValues;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    private TextView balance;
    private TextView nextRoll;
    private TextView rp;
    private TextView rpBonus;
    private TextView btcBonus;
    private TextView userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setView();
    }

    private void setView() {
        userID = findViewById(R.id.textViewUserId);
        balance = findViewById(R.id.textViewBalance);
        nextRoll = findViewById(R.id.textViewnextRoll);
        rp = findViewById(R.id.textViewRP);
        rpBonus = findViewById(R.id.textViewRPBonus);
        btcBonus = findViewById(R.id.textViewBTCBonus);
        Button btnStart = findViewById(R.id.buttonStart);
        Button btnStop = findViewById(R.id.buttonStop);

        ProfileView pp = (ProfileView) getIntent().getSerializableExtra(StaticValues.PROFILE_VIEW);
        if(pp != null) {
            setValueUI(pp);
        }else {
            // TODO back to login
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), BackgroundService.class);
                startService(intent);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), BackgroundService.class);
                stopService(intent);
            }
        });
    }

    private void setValueUI(final ProfileView pp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                userID.setText(pp.getUserID());
                balance.setText(pp.getBalance());
                rp.setText(pp.getRewardPoint());

                // next roll
                new CountDownTimer((pp.nextRollTime)*1000, 1000){

                    @Override
                    public void onTick(long millis) {
                        String hms = String.format(Locale.US,"%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(millis) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                TimeUnit.MILLISECONDS.toSeconds(millis) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                        nextRoll.setText(hms);
                    }

                    @Override
                    public void onFinish() {
                        nextRoll.setText("finish");
//                        doRoll();
                    }
                }.start();

                // reward point bonus
                new CountDownTimer((pp.rpBonusTime)*1000, 1000){

                    @Override
                    public void onTick(long millis) {
                        String hms = String.format(Locale.US,"%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                TimeUnit.MILLISECONDS.toMinutes(millis) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                TimeUnit.MILLISECONDS.toSeconds(millis) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                        rpBonus.setText(hms);
                    }

                    @Override
                    public void onFinish() {
                        rpBonus.setText("finish");
                    }
                }.start();

                // btc bonus

                new CountDownTimer((pp.btcBonusTime)*1000, 1000){

                    @Override
                    public void onTick(long millis) {
                        String hms = String.format(Locale.US,"%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                TimeUnit.MILLISECONDS.toMinutes(millis) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                TimeUnit.MILLISECONDS.toSeconds(millis) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                        btcBonus.setText(hms);
                    }

                    @Override
                    public void onFinish() {
                        btcBonus.setText("finish");
                    }
                }.start();
            }
        });
    }

    private void doRoll() {
        new Content().execute((Void)null);
    }

    public  class Content extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Object obj = RoboHandler.parsingRollResponse(getApplicationContext());
                if(obj instanceof RollSuccessResponse){
                    Object result = RoboHandler.parsingHomeResponse(getApplicationContext());
                    if(result instanceof ProfileView) {
                        ProfileView profileView = (ProfileView) obj;
                        setValueUI(profileView);
                    }else {
                        String message = (String) obj;
                        // TODO show message error
                    }
                }else if(obj instanceof RollErrorResponse){
                    String message = ((RollErrorResponse) obj).message;
                    // TODO show message error
                }
                else {
                    String message = (String) obj;
                    // TODO show message error
                }

            }catch (Exception e){
//                FileManager.getInstance().appendLog(e);
            }
            return null;
        }
    }

}
