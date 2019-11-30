package com.example.robocoinx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.robocoinx.R;
import com.example.robocoinx.model.db.ClaimHistory;

import java.util.ArrayList;

public class TransactionAdapter extends BaseAdapter {

    private ArrayList<ClaimHistory> content;
    private Context ctx;
    private LayoutInflater inflter;

    public TransactionAdapter(Context _ctx, ArrayList<ClaimHistory> _content){
        ctx = _ctx;
        content = _content;
        inflter = (LayoutInflater.from(_ctx));
    }

    @Override
    public int getCount() {
        return content.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflter.inflate(R.layout.view_transaction, null);
        ImageButton imgTransaction = view.findViewById(R.id.imgTransaction);
        TextView transactionName = view.findViewById(R.id.labelTransactionName);
        TextView date = view.findViewById(R.id.labelDate);
        TextView balance = view.findViewById(R.id.labelBalance);
        TextView amount = view.findViewById(R.id.labelAmount);

        ClaimHistory transaction = content.get(position);
        if(transaction.type == ClaimHistory.TransactionType.withdrawal){
            imgTransaction.setBackground(ctx.getDrawable(R.drawable.small_button_red));
            amount.setTextColor(ctx.getResources().getColor(R.color.transacRed));
            amount.setText("-"+transaction.claim);
        }else {
            imgTransaction.setBackground(ctx.getDrawable(R.drawable.small_button_green));
            amount.setTextColor(ctx.getResources().getColor(R.color.transacGreen));
            amount.setText("+"+transaction.claim);
        }
        transactionName.setText(transaction.name == null ? "Receive" : transaction.name);
        date.setText(transaction.date);
        balance.setText(transaction.balance);

        return view;
    }
}
