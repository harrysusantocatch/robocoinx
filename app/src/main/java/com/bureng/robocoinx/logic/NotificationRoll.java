package com.bureng.robocoinx.logic;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.bureng.robocoinx.R;
import com.bureng.robocoinx.model.db.ClaimHistory;
import com.bureng.robocoinx.model.response.RollErrorResponse;
import com.bureng.robocoinx.model.response.RollSuccessResponse;
import com.bureng.robocoinx.utils.RoboHandler;
import com.bureng.robocoinx.utils.StaticValues;
import com.bureng.robocoinx.repository.ClaimHistoryHandler;
import com.bureng.robocoinx.service.ServiceBroadcastReceiver;
import com.bureng.robocoinx.utils.FileManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationRoll {

    private static int interval = 1800000;
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

    public static void stopNotificationListener(Context context, String additionalMessage) {
        new Thread(() -> sendNotifyStop(context, additionalMessage)).start();
    }

    private static void restartNotificationListener(Context context) {
        new Thread(() -> sendNotifyRestart(context)).start();
    }

    private static void sendNotifyStop(Context context, String additionalMessage) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "stop")
                        .setSmallIcon(R.drawable.ic_notif_bitcoin)
                        .setContentTitle("Service Stop")
                        .setContentText(additionalMessage+"Click the start button below to run the service");

        builder.setAutoCancel(true);
        Intent yesReceive = new Intent();
        yesReceive.setAction(StaticValues.START_SERVICE);
        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(context, 9, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_notif_bitcoin, "START", pendingIntentYes);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.notify(0, builder.build());
    }

    private static void sendNotifyClaim(Context context) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "start")
                        .setSmallIcon(R.drawable.ic_notif_bitcoin)
                        .setContentTitle("New Bitcoin")
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
                        .setSmallIcon(R.drawable.ic_notif_bitcoin)
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

    public static void executeMainTask(Context context, Calendar calendar) {
        new Thread(() -> {
            boolean isStop = false;
            try {
                interval = 1800000;
                Object obj = RoboHandler.parsingRollResponse(context);
                if(obj instanceof RollSuccessResponse){
                    RollSuccessResponse rollSuccessResponse = (RollSuccessResponse)obj;
                    balance = rollSuccessResponse.getBalance();
                    claim = rollSuccessResponse.getClaim();
                    String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                    ClaimHistoryHandler.getInstance(context).insert(currentTime, "Bonus Network", ClaimHistory.TransactionType.receive.name(),claim, balance);
                    startNotificationListener(context);
                    FileManager.getInstance().appendLog("claim success");
                    FileManager.getInstance().InsertOrUpdate(context, StaticValues.LAST_DATE, String.valueOf(System.currentTimeMillis()));
                }else if(obj instanceof RollErrorResponse){
                    RollErrorResponse err = (RollErrorResponse) obj;
                    if(err.countDown == 0){
                        isStop = true;
                        FileManager.getInstance().appendLog(err.message);
                        stopNotificationListener(context, "Stop with error ");
                    }else {
                        if(!err.message.equalsIgnoreCase("resolve captcha")) {
                            interval = (err.countDown) * 1000;
                            if (interval / 60000 > 59) {
                                interval = 30000;
                                FileManager.getInstance().appendLog("claim wait " + interval / 1000 + " seconds");
                            } else {
                                FileManager.getInstance().appendLog("claim wait " + (interval / 60000) + " minutes");
                            }
                        }else interval = err.countDown;
                        restartNotificationListener(context);
                    }
                }else {
                    interval = 30000;
                    FileManager.getInstance().appendLog("claim wait "+(interval/1000)+" seconds");
                }
            } catch (Exception e){
                interval = 30000;
                FileManager.getInstance().appendLog("claim wait "+(interval/1000)+" seconds with error!");
                FileManager.getInstance().appendLog(e);
            }
            finally {
                if(alarmManager != null) alarmManager.cancel(pendingIntent);
                if(!isStop) {
                    calendar.setTimeInMillis(System.currentTimeMillis() + interval);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }
                }
            }
        }).start();
    }
}
