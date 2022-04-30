package com.velvet.kamikazelock.bg

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.velvet.kamikazelock.MainActivity
import com.velvet.kamikazelock.R

class NotificationManager(private val context: Context) {

    private val resultPendingIntent = PendingIntent.getActivity(
        context, 0,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    private val notification = NotificationCompat.Builder(context, CHANNEL_ID_APPLOCKER_SERVICE)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(context.getString(R.string.notification_title))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(resultPendingIntent)

    fun createNotification(): Notification {
        createAppLockerServiceChannel()
        val serviceNotification = notification
            .setContentText(context.getString(R.string.notification_protecting_description))
            .build()
        NotificationManagerCompat.from(context).notify(
            NOTIFICATION_ID_APPLOCKER_SERVICE,
            serviceNotification
        )
        return serviceNotification
    }

    fun createPermissionNeedNotification(): Notification {
        createAppLockerServiceChannel()
        val permissionNotification = notification
            .setContentText(context.getString(R.string.notification_protecting_description))
            .build()
        NotificationManagerCompat.from(context).notify(
            NOTIFICATION_ID_APPLOCKER_SERVICE,
            permissionNotification
        )
        return permissionNotification
    }

    fun hidePermissionNotification(){
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID_APPLOCKER_PERMISSION_NEED)
    }

    private fun createAppLockerServiceChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID_APPLOCKER_SERVICE,
            "KamiLock channel",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "KamiLock notification channel"
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID_APPLOCKER_SERVICE = "CHANNEL_ID_APPLOCKER_SERVICE"
        const val NOTIFICATION_ID_APPLOCKER_SERVICE = 1
        const val NOTIFICATION_ID_APPLOCKER_PERMISSION_NEED = 2
    }
}