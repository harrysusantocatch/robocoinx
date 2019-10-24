package com.example.robocoinx.logic;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.example.robocoinx.model.StaticValues;

import java.util.Calendar;
import java.util.Objects;

public class ServiceBroadcastReceiver extends android.content.BroadcastReceiver {

    private PowerManager.WakeLock screenWakeLock;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (screenWakeLock == null)
        {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            assert pm != null;
            screenWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "robocoinx:mylocktag");
            screenWakeLock.acquire(10*60*1000L /*10 minutes*/);
        }
        if (screenWakeLock != null) {
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
            screenWakeLock.release();
        }

    }
}
