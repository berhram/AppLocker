package com.velvet.kamikazelock.bg

import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageEvents.Event.*
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.velvet.kamikazelock.BuildConfig
import com.velvet.kamikazelock.OverlayActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class AppForegroundFlow(private val context: Context, private val permissionChecker: PermissionChecker) {

    fun get(): Flow<String> = flow {
        while (true) {
            if (permissionChecker.isUsageAccessPermissionGranted()) {
                emit(Unit)
            }
            delay(100)
        }
    }.map {
        val pkgName = getCurrentAppPackageName()
        pkgName
    }.filter { it != null }.map { it!! }.distinctUntilChanged()

    fun getCurrentAppPackageName(): String? {
        var pkgName: String? = null
        val mUsageStatsManager = context.getSystemService(Service.USAGE_STATS_SERVICE)
                as UsageStatsManager
        val time = System.currentTimeMillis()
        val usageEvents = mUsageStatsManager.queryEvents(time - 1000 * 3600, time)
        while (usageEvents.hasNextEvent()) {
            val event = UsageEvents.Event()
            usageEvents.getNextEvent(event)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                when (event.eventType) {
                    ACTIVITY_RESUMED -> pkgName = event.packageName
                    ACTIVITY_PAUSED -> { if (event.packageName == pkgName) pkgName = null }
                }
            } else {
                when (event.eventType) {
                    MOVE_TO_FOREGROUND -> pkgName = event.packageName
                    MOVE_TO_BACKGROUND -> { if (event.packageName == pkgName) pkgName = null }
                }
            }
        }
        return pkgName
    }
}
