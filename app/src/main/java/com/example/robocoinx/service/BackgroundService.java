package com.example.robocoinx.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.robocoinx.utils.FileManager;
import com.example.robocoinx.logic.NotificationRoll;

import java.util.Calendar;

public class BackgroundService extends Service {

    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        new NotificationRoll(getApplicationContext());
        NotificationRoll.executeMainTask(getApplicationContext(), Calendar.getInstance());
        FileManager.getInstance().appendLog("start service & execute main task");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if(NotificationRoll.getAlarmManager() != null) NotificationRoll.getAlarmManager().cancel(NotificationRoll.getPendingIntent());
        NotificationRoll.stopNotificationListener(getApplicationContext(), "");
        FileManager.getInstance().appendLog("stop service");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

}
