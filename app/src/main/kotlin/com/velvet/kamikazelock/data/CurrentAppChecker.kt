package com.velvet.kamikazelock.data

import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageEvents.Event.*
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import com.velvet.kamikazelock.data.PermissionChecker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

private const val CHECK_APP_DELAY_MILLIS = 250L
private const val CHECK_IN_APPS_OPENED_LAST_MILLIS = 1000 * 3600

class CurrentAppChecker(private val context: Context, private val permissionChecker: PermissionChecker) {

    fun get(): Flow<String> = flow {
        while (true) {
            if (permissionChecker.isUsageAccessPermissionGranted()) {
                emit(Unit)
            }
            delay(CHECK_APP_DELAY_MILLIS)
        }
    }.map {
        val pkgName = getCurrentAppPackageName()
        pkgName
    }.filter { it != null }.map { it!! }.distinctUntilChanged()

    private fun getCurrentAppPackageName(): String? {
        var pkgName: String? = null
        val mUsageStatsManager = context.getSystemService(Service.USAGE_STATS_SERVICE)
                as UsageStatsManager
        val time = System.currentTimeMillis()
        val usageEvents = mUsageStatsManager.queryEvents(time - CHECK_IN_APPS_OPENED_LAST_MILLIS, time)
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
