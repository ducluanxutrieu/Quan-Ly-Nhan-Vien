package com.ducluanxutrieu.quanlynhanvien;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ducluanxutrieu.quanlynhanvien.Activity.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessageService extends FirebaseMessagingService {
    public static final String TAG = "tagtag";
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null) return;
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData() != null) {
            String s1 = remoteMessage.getData().get("title");
            String s2 = remoteMessage.getData().get("message");
            Log.i("NOTIF", s1 + "|" + s2);
            hanldeNotification( s1, s2);
        }
    }

    private void hanldeNotification(String title, String message) {
        PushNotificationManager.getInstance().generateNotification(title, message, MainActivity.class);
    }
}
