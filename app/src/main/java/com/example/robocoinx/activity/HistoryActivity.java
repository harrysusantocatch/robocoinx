package com.example.robocoinx.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.robocoinx.R;
import com.example.robocoinx.model.db.ClaimHistory;
import com.example.robocoinx.repository.ClaimHistoryHandler;

import java.util.ArrayList;

public class HistoryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setupView();
    }

    private void setupView() {
        TableLayout tableLayout = findViewById(R.id.tableLayoutContent);
        ArrayList<ClaimHistory> claimHistories = ClaimHistoryHandler.getInstance(getApplicationContext()).getClaimHistories();
        for (ClaimHistory cl: claimHistories) {
            TableRow row = new TableRow(getApplicationContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView t1 = new TextView(getApplicationContext());
            TextView t2 = new TextView(getApplicationContext());
            TextView t3 = new TextView(getApplicationContext());
            t1.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.42f));
            t2.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.29f));
            t3.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.29f));
            t1.setText(cl.date);
            t2.setText(cl.claim);
            t3.setText(cl.balance);

            t1.setPadding(10, 6, 0, 6);
            t2.setPadding(0, 6, 0, 6);
            t3.setPadding(0, 6, 0, 6);

            t1.setTextColor(getResources().getColor(R.color.midBlue));
            t2.setTextColor(getResources().getColor(R.color.midBlue));
            t3.setTextColor(getResources().getColor(R.color.midBlue));

            row.addView(t1);
            row.addView(t2);
            row.addView(t3);
            tableLayout.addView(row);
        }
    }
}
