package com.ducluanxutrieu.quanlynhanvien.util

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ducluanxutrieu.quanlynhanvien.R

class PushNotificationManager {
    private var context: Context? = null
    private var notifyManager: NotificationManager? = null

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_name"
            val description = "channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context!!.getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    fun init(context: Context) {
        this.context = context
        notifyManager = this.context!!
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    internal fun generateNotification(title: String, notifyMessage: String, cls: Class<*>) {
        PUSH_NOTIFICATION_ID++

        val noticeTitle = context!!.getString(R.string.app_name)
        val notificationIntent = Intent(context, cls)

        notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_SINGLE_TOP
                or Intent.FLAG_ACTIVITY_CLEAR_TASK
                or Intent.FLAG_ACTIVITY_NEW_TASK)

        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(cls)
        stackBuilder.addNextIntent(notificationIntent)
        val pendingIntent = stackBuilder.getPendingIntent(PUSH_NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT)

        createNotificationChannel()

        val mBuilder = NotificationCompat.Builder(this.context!!, CHANNEL_ID)
        mBuilder.setSmallIcon(R.drawable.ic_app_icon)
        mBuilder.setTicker(noticeTitle)
        mBuilder.setContentTitle(title)
        mBuilder.setContentText(notifyMessage)
        mBuilder.priority = NotificationCompat.PRIORITY_HIGH
        mBuilder.setOngoing(false)
        mBuilder.setAutoCancel(true)
        mBuilder.setContentIntent(pendingIntent)

        // Play default notification sound
        mBuilder.setDefaults(Notification.DEFAULT_LIGHTS)
        notifyManager!!.notify(PUSH_NOTIFICATION_ID, mBuilder.build())
    }

    fun cancelNotification(notifyId: Int) {
        notifyManager!!.cancel(notifyId)
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var pushNotifyManager: PushNotificationManager? = null
        private var PUSH_NOTIFICATION_ID = 0
        private const val CHANNEL_ID = "123"

        val instance: PushNotificationManager
            get() {
                if (pushNotifyManager == null) {
                    pushNotifyManager = PushNotificationManager()
                }
                return pushNotifyManager as PushNotificationManager
            }
    }
}