package com.bureng.robocoinx.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.bureng.robocoinx.R;
import com.bureng.robocoinx.adapter.TransactionAdapter;
import com.bureng.robocoinx.model.db.ClaimHistory;
import com.bureng.robocoinx.model.view.ProfileView;
import com.bureng.robocoinx.repository.ClaimHistoryHandler;
import com.bureng.robocoinx.service.BackgroundService;
import com.bureng.robocoinx.utils.FileManager;
import com.bureng.robocoinx.utils.RoboHandler;
import com.bureng.robocoinx.utils.StaticValues;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends Activity implements View.OnClickListener {

    private TextView balance;
    private TextView nextRoll;
    private TextView rp;
    private TextView rpBonus;
    private TextView btcBonus;
    private TextView userID;
    private TextView captcha;
    private Button buttonStart;
    private Button buttonStop;
    private ImageButton slideTransaction;
    private boolean isUp;
    private LinearLayout transactionLayout;
    private ConstraintLayout parent;
    private String depositAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setView();
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (BackgroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void setView() {
        userID = findViewById(R.id.textViewUserId);
        balance = findViewById(R.id.textViewBalance);
        nextRoll = findViewById(R.id.textViewnextRoll);
        rp = findViewById(R.id.textViewRP);
        rpBonus = findViewById(R.id.textViewRPBonus);
        btcBonus = findViewById(R.id.textViewBTCBonus);
        captcha = findViewById(R.id.textViewHaveCapcha);
        buttonStart = findViewById(R.id.buttonStart);
        buttonStop = findViewById(R.id.buttonStop);
        slideTransaction = findViewById(R.id.buttonSlideTransaction);
        transactionLayout = findViewById(R.id.headerTransaction);
        parent = findViewById(R.id.parent);
        ListView transactionView = findViewById(R.id.listTransaction);
        ImageView buttonPage = findViewById(R.id.buttonPage);

        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonPage.setOnClickListener(this);
        slideTransaction.setOnClickListener(this);

        ProfileView pp = (ProfileView) getIntent().getSerializableExtra(StaticValues.PROFILE_VIEW);
        setValueUI(pp);

        ArrayList<ClaimHistory> content = ClaimHistoryHandler.getInstance(getApplicationContext()).getClaimHistories();
        TransactionAdapter adapter = new TransactionAdapter(getApplicationContext(),content);
        transactionView.setAdapter(adapter);

        boolean running = isMyServiceRunning();
        if(running) {
            buttonStart.setVisibility(View.GONE);
            buttonStop.setVisibility(View.VISIBLE);
        }else {
            buttonStart.setVisibility(View.VISIBLE);
            buttonStop.setVisibility(View.GONE);
        }

        if(content.size() > 8){
            slideTransaction.setVisibility(View.VISIBLE);
        }else {
            slideTransaction.setVisibility(View.GONE);
        }
    }

    private void setValueUI(final ProfileView pp) {
        runOnUiThread(() -> {
            depositAddress = pp.depositAdress;
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
                    nextRoll.setText(R.string.stop_count_hour);
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
                    rpBonus.setText(R.string.stop_count_day);
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
                    btcBonus.setText(R.string.stop_count_day);
                }
            }.start();
            String text = "AUTO";
            if(pp.haveCaptcha) text = "MANUAL ("+pp.noCaptchaSpec.lottery+":"+pp.noCaptchaSpec.wager+")";
            captcha.setText(text);
        });
    }

    @SuppressLint("BatteryLife")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonStart:
                Intent intent = new Intent(getBaseContext(), BackgroundService.class);
                String packageName = getApplicationContext().getPackageName();
                PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    assert pm != null;
                    if (pm.isIgnoringBatteryOptimizations(packageName))
                        intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    else {
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + packageName));
                    }
                }
                startService(intent);
                buttonStart.setVisibility(View.GONE);
                buttonStop.setVisibility(View.VISIBLE);
                break;
            case R.id.buttonStop:
                stopService(new Intent(getBaseContext(), BackgroundService.class));
                buttonStart.setVisibility(View.VISIBLE);
                buttonStop.setVisibility(View.GONE);
                break;
            case R.id.buttonPage:
                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.menu_option);
                Window window = dialog.getWindow();
                Objects.requireNonNull(window).setLayout(this.getResources().getDisplayMetrics().widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);
                ImageView closeDialog = dialog.findViewById(R.id.imageClose);
                ConstraintLayout layoutWithdraw = dialog.findViewById(R.id.layoutWithdraw);
                ConstraintLayout layoutDeposit = dialog.findViewById(R.id.layoutDeposit);
                ConstraintLayout layoutNotif = dialog.findViewById(R.id.layoutNotification);
                Button btnLogout = dialog.findViewById(R.id.buttonLogout);
                closeDialog.setOnClickListener(v -> dialog.dismiss());
                layoutWithdraw.setOnClickListener(v -> {
                    dialog.dismiss();
                    startActivity(new Intent(this, WithdrawActivity.class));
                });
                layoutDeposit.setOnClickListener(v -> {
                    dialog.dismiss();
                    Intent newIntent = new Intent(this, DepositActivity.class);
                    newIntent.putExtra(StaticValues.DEPOSIT_ADDRESS, depositAddress);
                    startActivity(newIntent);
                });
                layoutNotif.setOnClickListener(v -> dialog.dismiss());
                btnLogout.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
                break;
            case R.id.buttonSlideTransaction:
                if (isUp) {
                    slideDown(transactionLayout);
                    slideTransaction.setImageResource(R.drawable.ic_up);
                } else {
                    slideUp(transactionLayout);
                    slideTransaction.setImageResource(R.drawable.ic_down);
                }
                isUp = !isUp;
        }
    }

    public void slideUp(View view){
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)transactionLayout.getLayoutParams();
        layoutParams.topToTop = parent.getId();
    }

    public void slideDown(View view){
        ConstraintSet set = new ConstraintSet();
        ConstraintLayout layout = findViewById(R.id.parent);
        set.clone(layout);
        set.clear(R.id.headerTransaction, ConstraintSet.TOP);
        set.applyTo(layout);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)transactionLayout.getLayoutParams();
        layoutParams.topToBottom = findViewById(R.id.layoutTimeBonus).getId();
    }

    public void goLogin() {
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(intent);
    }

    @SuppressLint("StaticFieldLeak")
    public  class Content extends AsyncTask<Void, Void, Void>{
//        private ProfileView profileView;
        public Content(){
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                RoboHandler.parsingRollResponse(getApplicationContext());
            }catch (Exception e){
                FileManager.getInstance().appendLog(e);
            }
            return null;
        }
    }


}
