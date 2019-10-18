package com.example.robocoinx.logic;

import android.content.Context;
import android.content.Intent;

import com.example.robocoinx.model.NotificationRoll;
import com.example.robocoinx.model.StaticValues;

public class ServiceBroadcastReceiver extends android.content.BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equals(StaticValues.BOOT_COMPLETED) ||
                action.equals(StaticValues.START_SERVICE)) {
            Intent serviceIntent = new Intent(context, BackgroundService.class);
            context.startService(serviceIntent);
        }else if (action.equals(StaticValues.CREATE_SERVICE)){
            NotificationRoll.executeMainTask(context);
        }
    }
}