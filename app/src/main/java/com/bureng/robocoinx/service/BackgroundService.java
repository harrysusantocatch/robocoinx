package com.bureng.robocoinx.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.bureng.robocoinx.logic.NotificationRoll;
import com.bureng.robocoinx.utils.FileManager;

import java.util.Calendar;

public class BackgroundService extends Service {

    private static BackgroundService instance = null;

    public static boolean isCreated() {
        return instance != null;
    }

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
        instance = this;
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if(NotificationRoll.getAlarmManager() != null) NotificationRoll.getAlarmManager().cancel(NotificationRoll.getPendingIntent());
        NotificationRoll.stopNotificationListener(getApplicationContext(), "Grant background service access in the battery menu (Setting)");
        FileManager.getInstance().appendLog("stop service");
        instance = null;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

}
