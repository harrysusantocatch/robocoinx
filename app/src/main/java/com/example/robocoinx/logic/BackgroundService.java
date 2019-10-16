package com.example.robocoinx.logic;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.example.robocoinx.R;

import androidx.core.app.NotificationCompat;

public class BackgroundService extends Service {

    private int mInterval = 60000; // 3600000 1 hr by default, can be changed later
    private Handler mHandler;

    public BackgroundService() {
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                startNotificationListener();
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
        stopNotificationListener();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        mHandler = new Handler();
        startRepeatingTask();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        stopRepeatingTask();
        super.onDestroy();
    }

    public void startNotificationListener() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendNotifyStart();
            }
        }).start();
    }

    public void stopNotificationListener() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendNotifyStop();
            }
        }).start();
    }

    private void sendNotifyStop() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "test")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Notifications")
                        .setContentText("Service Stop");

        Intent notificationIntent = new Intent();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.notify(0, builder.build());
    }

    private void sendNotifyStart() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "test")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Notifications")
                        .setContentText("Service Running");

        Intent notificationIntent = new Intent();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.notify(0, builder.build());
    }
}
