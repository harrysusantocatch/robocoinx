package com.example.robocoinx.service;

import android.content.Context;
import android.content.Intent;

import com.example.robocoinx.utils.FileManager;
import com.example.robocoinx.logic.NotificationRoll;
import com.example.robocoinx.utils.StaticValues;

import java.util.Calendar;
import java.util.Objects;

public class ServiceBroadcastReceiver extends android.content.BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = "service on receive ";
        String action = intent.getAction();
        if (Objects.equals(action, StaticValues.BOOT_COMPLETED) ||
                Objects.equals(action, StaticValues.START_SERVICE)) {
            Intent serviceIntent = new Intent(context, BackgroundService.class);
            context.startService(serviceIntent);
            msg = msg + "from boot";
        }else if (Objects.equals(action, StaticValues.CREATE_SERVICE)){
            NotificationRoll.executeMainTask(context, Calendar.getInstance());
            msg = msg + "execute main task";
        }
        FileManager.getInstance().appendLog(msg);
    }
}
