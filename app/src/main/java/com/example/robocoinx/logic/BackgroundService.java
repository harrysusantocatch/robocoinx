package com.example.robocoinx.logic;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.example.robocoinx.R;
import com.example.robocoinx.model.RollErrorResponse;
import com.example.robocoinx.model.RollSuccessResponse;
import com.example.robocoinx.model.StaticValues;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.core.app.NotificationCompat;

public class BackgroundService extends Service {

    private int mInterval = 3630000; // 3630000 1 hr by default, can be changed later
    private Handler mHandler;
    private String claim;
    private String balance;

    public BackgroundService() {
    }

    Thread mStatusChecker = new Thread() {
        @Override
        public void run() {
            try {
                mInterval = 3630000;
                Object obj = RoboHandler.parsingRollResponse(getApplicationContext());
                if(obj instanceof RollSuccessResponse){
                    RollSuccessResponse rollSuccessResponse = (RollSuccessResponse)obj;
                    balance = rollSuccessResponse.getBalance();
                    claim = rollSuccessResponse.getClaim();
                    startNotificationListener();
                }else if(obj instanceof RollErrorResponse){
                    RollErrorResponse err = (RollErrorResponse) obj;
                    mInterval = (err.countDown + 5) * 1000;
                    restartNotificationListener();
                }else {
                    mInterval = 30000;
                }
            } catch (Exception e){
//                FileManager.getInstance().appendLog(e);
                mInterval = 30000;
            }
            finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.start();
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
                sendNotifyClaim();
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

    public void restartNotificationListener() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendNotifyRestart();
            }
        }).start();
    }
    private void sendNotifyStop() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "stop")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Service Stop")
                        .setContentText("Click the start button below to run the service");

        builder.setAutoCancel(true);
        Intent yesReceive = new Intent();
        yesReceive.setAction(StaticValues.START_SERVICE);
        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 9, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_launcher_foreground, "START", pendingIntentYes);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.notify(0, builder.build());
    }

    private void sendNotifyClaim() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "start")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("New Claim")
                        .setContentText("You got "+claim+" & current balance is "+balance);

        Intent notificationIntent = new Intent();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.notify(0, builder.build());
    }

    private void sendNotifyRestart() {
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(System.currentTimeMillis() +mInterval));
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "start")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Restart Service")
                        .setContentText("service will run again at "+currentTime);

        Intent notificationIntent = new Intent();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.notify(0, builder.build());
    }
}
