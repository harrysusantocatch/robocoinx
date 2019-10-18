package com.example.robocoinx.model;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.robocoinx.R;
import com.example.robocoinx.logic.RoboHandler;
import com.example.robocoinx.logic.ServiceBroadcastReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationRoll {

    private static int interval = 3600000;
    private static int defaultInterval = 3600000;
    private static AlarmManager alarmManager;
    private static PendingIntent pendingIntent;
    private static String claim;
    private static String balance;

    public NotificationRoll(Context context){
        Intent intent = new Intent(context, ServiceBroadcastReceiver.class);
        intent.setAction(StaticValues.CREATE_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }


    public static AlarmManager getAlarmManager() {
        return alarmManager;
    }

    public static PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public static String getClaim() {
        return claim;
    }

    public static void setClaim(String claim) {
        NotificationRoll.claim = claim;
    }

    public static String getBalance() {
        return balance;
    }

    public static void setBalance(String balance) {
        NotificationRoll.balance = balance;
    }

    private static void startNotificationListener(Context context) {
        new Thread(() -> sendNotifyClaim(context)).start();
    }

    public static void stopNotificationListener(Context context) {
        new Thread(() -> sendNotifyStop(context)).start();
    }

    private static void restartNotificationListener(Context context) {
        new Thread(() -> sendNotifyRestart(context)).start();
    }

    private static void sendNotifyStop(Context context) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "stop")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Service Stop")
                        .setContentText("Click the start button below to run the service");

        builder.setAutoCancel(true);
        Intent yesReceive = new Intent();
        yesReceive.setAction(StaticValues.START_SERVICE);
        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(context, 9, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_launcher_foreground, "START", pendingIntentYes);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.notify(0, builder.build());
    }

    private static void sendNotifyClaim(Context context) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "start")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("New Claim")
                        .setContentText("You got "+claim+" & current balance is "+balance);

        Intent notificationIntent = new Intent();
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.notify(0, builder.build());
    }

    private static void sendNotifyRestart(Context context) {
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(System.currentTimeMillis() + interval));
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "start")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Restart Service")
                        .setContentText("service will run again at "+currentTime);

        Intent notificationIntent = new Intent();
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.notify(0, builder.build());
    }

    public static void executeMainTask(Context context) {
        new Thread(() -> {
            try {
                interval = 3600000;
                Object obj = RoboHandler.parsingRollResponse(context);
                if(obj instanceof RollSuccessResponse){
                    RollSuccessResponse rollSuccessResponse = (RollSuccessResponse)obj;
                    balance = rollSuccessResponse.getBalance();
                    claim = rollSuccessResponse.getClaim();
                    String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                    new ClaimHistoryHandler(context).insert(currentTime, claim, balance);
                    startNotificationListener(context);
                }else if(obj instanceof RollErrorResponse){
                    RollErrorResponse err = (RollErrorResponse) obj;
                    interval = (err.countDown + 5) * 1000;
                    restartNotificationListener(context);
                }else {
                    interval = 30000;
                }
            } catch (Exception e){
//                FileManager.getInstance().appendLog(e);
                interval = 30000;
            }
            finally {
                if(alarmManager != null) alarmManager.cancel(pendingIntent);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis()+ interval);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), defaultInterval, pendingIntent);
            }
        }).start();
    }
}