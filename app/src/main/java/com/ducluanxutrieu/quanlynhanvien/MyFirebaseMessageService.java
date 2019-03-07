package com.ducluanxutrieu.quanlynhanvien;

import com.ducluanxutrieu.quanlynhanvien.Activity.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessageService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null) return;
        if (remoteMessage.getData() != null) {
            String s1 = remoteMessage.getData().get("title");
            String s2 = remoteMessage.getData().get("message");
            if (s1.startsWith("@@")){
                s1 = s1.substring(2);
                s1 = "Ask for a off day from: " + s1;
            }
            hanldeNotification( s1, s2);
        }
    }

    private void hanldeNotification(String title, String message) {
        PushNotificationManager.getInstance().generateNotification(title, message, MainActivity.class);
    }
}
