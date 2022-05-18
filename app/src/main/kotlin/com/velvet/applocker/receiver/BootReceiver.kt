package com.velvet.applocker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.velvet.applocker.service.AppLockerService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            context?.let {
                val serviceIntent = Intent(context, AppLockerService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContextCompat.startForegroundService(context, serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
        }
    }
}