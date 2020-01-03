package com.bureng.robocoinx.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import com.bureng.robocoinx.utils.FileManager;
import com.bureng.robocoinx.logic.NotificationRoll;
import com.bureng.robocoinx.utils.StaticValues;

import java.util.Calendar;
import java.util.Objects;

public class ServiceBroadcastReceiver extends android.content.BroadcastReceiver {

    @SuppressLint("BatteryLife")
    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = "service on receive ";
        String action = intent.getAction();
        if (Objects.equals(action, StaticValues.BOOT_COMPLETED) ||
                Objects.equals(action, StaticValues.START_SERVICE)) {
            Intent serviceIntent = new Intent(context, BackgroundService.class);
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                assert pm != null;
                if (pm.isIgnoringBatteryOptimizations(packageName))
                    intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                else {
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                }
            }
            context.startService(serviceIntent);
            msg = msg + "from boot";
        }else if (Objects.equals(action, StaticValues.CREATE_SERVICE)){
            NotificationRoll.executeMainTask(context, Calendar.getInstance());
            msg = msg + "execute main task";
        }
        FileManager.getInstance().appendLog(msg);
    }
}
