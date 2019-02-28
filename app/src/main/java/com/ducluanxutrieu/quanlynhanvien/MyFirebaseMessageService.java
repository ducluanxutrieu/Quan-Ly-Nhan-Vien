package com.ducluanxutrieu.quanlynhanvien;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
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
        Log.i("tokentoken1", s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null) return;
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Notification body: " + remoteMessage.getNotification().getBody());
            hanldeNotification(remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Data payload: " + remoteMessage.getData().toString());
        }

    }

    private void hanldeNotification(String messege) {
        PushNotificationManager.getInstance().generateNotification(messege, MainActivity.class);
    }
}
