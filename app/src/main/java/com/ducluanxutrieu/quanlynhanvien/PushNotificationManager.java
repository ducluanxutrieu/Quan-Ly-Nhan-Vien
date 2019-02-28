package com.ducluanxutrieu.quanlynhanvien;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class PushNotificationManager {

    private static PushNotificationManager pushNotifyManager;
    private Context context;
    private NotificationManager notifyManager;
    private static int PUSH_NOTIFICATION_ID = 0;

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

    public int generateNotification(String notifyMessage, Class<?> cls) {
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

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setTicker(noticeTitle);
        mBuilder.setContentTitle(noticeTitle);
        mBuilder.setContentText(notifyMessage);
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setOngoing(false);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(pendingIntent);

        // Play default notification sound
        mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        notifyManager.notify(PUSH_NOTIFICATION_ID, mBuilder.build());
        return PUSH_NOTIFICATION_ID;
    }

    public void cancelNotification(int notifyId) {
        notifyManager.cancel(notifyId);
    }
}