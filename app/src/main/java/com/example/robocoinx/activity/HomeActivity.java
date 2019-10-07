package com.example.robocoinx.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.robocoinx.R;
import com.example.robocoinx.model.ProfileView;
import com.example.robocoinx.model.StaticValues;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setView();
    }

    private void setView() {
        TextView userID = findViewById(R.id.textViewUserId);
        TextView balance = findViewById(R.id.textViewBalance);
        TextView nextRoll = findViewById(R.id.textViewnextRoll);
        TextView rp = findViewById(R.id.textViewRP);
        TextView rpBonus = findViewById(R.id.textViewRPBonus);
        TextView btcBonus = findViewById(R.id.textViewBTCBonus);
        ProfileView pp = (ProfileView) getIntent().getSerializableExtra(StaticValues.PROFILE_VIEW);
        userID.setText(pp.getUserID());
        balance.setText(pp.getBalance());
        nextRoll.setText(pp.getNextRollTime());
        rp.setText(pp.getRewardPoint());
        rpBonus.setText(pp.getRpBonusTime());
        btcBonus.setText(pp.getBtcBonusTime());
    }
}
