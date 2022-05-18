package com.velvet.applocker.infra

import android.graphics.drawable.Drawable

data class AppInfo(
    val name: String,
    val packageName: String,
    val isLocked: Boolean,
    val icon: Drawable,
    val isChanged: Boolean
) {
    fun toLockedApp() : LockedApp {
        return LockedApp(packageName)
    }
}
