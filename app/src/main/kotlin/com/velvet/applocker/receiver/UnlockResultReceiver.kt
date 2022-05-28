package com.velvet.applocker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class UnlockResultReceiver(
    private val onSuccessUnlock: () -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            "APP_UNLOCKED" -> onSuccessUnlock()
        }
    }
}