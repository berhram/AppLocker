package com.velvet.applocker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenStateReceiver(
    private val onOnScreen: () -> Unit,
    private val onOffScreen: () -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SCREEN_ON -> onOnScreen()
            Intent.ACTION_SCREEN_OFF -> onOffScreen()
        }
    }
}