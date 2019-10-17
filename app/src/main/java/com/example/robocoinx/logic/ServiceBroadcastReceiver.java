package com.example.robocoinx.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.robocoinx.model.StaticValues;

public class ServiceBroadcastReceiver extends android.content.BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(StaticValues.BOOT_COMPLETED) ||
                intent.getAction().equals(StaticValues.START_SERVICE)) {
            Intent serviceIntent = new Intent(context, BackgroundService.class);
            context.startService(serviceIntent);
        }
    }
}
