package com.example.robocoinx.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.robocoinx.R;
import com.example.robocoinx.logic.BackgroundService;
import com.example.robocoinx.logic.FileManager;
import com.example.robocoinx.model.db.ClaimHistory;
import com.example.robocoinx.logic.ClaimHistoryHandler;
import com.example.robocoinx.model.view.ProfileView;
import com.example.robocoinx.model.StaticValues;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    private TextView balance;
    private TextView nextRoll;
    private TextView rp;
    private TextView rpBonus;
    private TextView btcBonus;
    private TextView userID;
    private TextView status;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setView();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void setView() {
        tableLayout = findViewById(R.id.tableLayout);
        userID = findViewById(R.id.textViewUserId);
        balance = findViewById(R.id.textViewBalance);
        nextRoll = findViewById(R.id.textViewnextRoll);
        rp = findViewById(R.id.textViewRP);
        rpBonus = findViewById(R.id.textViewRPBonus);
        btcBonus = findViewById(R.id.textViewBTCBonus);
        status = findViewById(R.id.textViewService);
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

        boolean running = isMyServiceRunning(BackgroundService.class);
        if(running) status.setText("RUNNING");
    }

    private void setValueUI(final ProfileView pp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                userID.setText(pp.userID);
                balance.setText(pp.balance);
                rp.setText(pp.rewardPoint);

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

                // table
//                String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
//                ArrayList<ClaimHistory> claimHistories = ClaimHistoryHandler.getInstance(getApplicationContext()).getClaimHistories();
//                for (ClaimHistory cl: claimHistories) {
//                    TableRow row= new TableRow(getApplicationContext());
//                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
//                    row.setLayoutParams(lp);
//                    TextView t1 = new TextView(getApplicationContext());
//                    TextView t2 = new TextView(getApplicationContext());
//                    TextView t3 = new TextView(getApplicationContext());
//                    t1.setText("  "+cl.date);
//                    t2.setText("  "+cl.claim);
//                    t3.setText("  "+cl.balance);
//                    row.addView(t1);
//                    row.addView(t2);
//                    row.addView(t3);
//                    tableLayout.addView(row);
//                }
            }
        });
    }

    public  class Content extends AsyncTask<Void, Void, Void>{
        private ProfileView profileView;
        public Content(ProfileView _profileView) {
            profileView = _profileView;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
//                executeTaskBackground(profileView);
            }catch (Exception e){
                FileManager.getInstance().appendLog(e);
            }
            return null;
        }
    }


}
