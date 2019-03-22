package com.ducluanxutrieu.quanlynhanvien;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class PushNotificationManager {

    private static PushNotificationManager pushNotifyManager;
    private Context context;
    private NotificationManager notifyManager;
    private static int PUSH_NOTIFICATION_ID = 0;
    private static String CHANNEL_ID = "123";

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel_name";
            String description = "channel_description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static PushNotificationManager getInstance() {
        if (pushNotifyManager == null) {
            pushNotifyManager = new PushNotificationManager();
        }
        return pushNotifyManager;
    }

    public void init(Context context) {
        this.context = context;
        notifyManager = (NotificationManager) this.context
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    void generateNotification(String title, String notifyMessage, Class<?> cls) {
        PUSH_NOTIFICATION_ID++;

        String noticeTitle = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, cls);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(PUSH_NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT);

        createNotificationChannel();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context, CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.ic_app_icon);
        mBuilder.setTicker(noticeTitle);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(notifyMessage);
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setOngoing(false);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(pendingIntent);

        // Play default notification sound
        mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        notifyManager.notify(PUSH_NOTIFICATION_ID, mBuilder.build());
    }

    public void cancelNotification(int notifyId) {
        notifyManager.cancel(notifyId);
    }
}