package com.example.robocoinx.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.robocoinx.R;
import com.example.robocoinx.model.db.ClaimHistory;
import com.example.robocoinx.repository.ClaimHistoryHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setupView();
    }

    private void setupView() {
        TableLayout tableLayout = findViewById(R.id.tableLayout);
                ArrayList<ClaimHistory> claimHistories = ClaimHistoryHandler.getInstance(getApplicationContext()).getClaimHistories();
                for (ClaimHistory cl: claimHistories) {
                    TableRow row = new TableRow(getApplicationContext());
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                    row.setLayoutParams(lp);
                    TextView t1 = new TextView(getApplicationContext());
                    TextView t2 = new TextView(getApplicationContext());
                    TextView t3 = new TextView(getApplicationContext());
                    t1.setText("  " + cl.date);
                    t2.setText("  " + cl.claim);
                    t3.setText("  " + cl.balance);
                    row.addView(t1);
                    row.addView(t2);
                    row.addView(t3);
                    tableLayout.addView(row);
                }
    }
}
