package com.velvet.kamikazelock.bg

import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class AppForegroundFlow(private val context: Context, private val permissionChecker: PermissionChecker) {

    fun get(): Flow<String> {
        return flow<String> {
            while (true) { delay(100) }
        }. filter {
            permissionChecker.isUsageAccessPermissionGranted()
        }.map {
            Log.d("APPS", "after filter")
            var usageEvent: UsageEvents.Event? = null

            val mUsageStatsManager = context.getSystemService(Service.USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()

            val usageEvents = mUsageStatsManager.queryEvents(time - 1000 * 3600, time)
            val event = UsageEvents.Event()
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    usageEvent = event
                }
            }
            usageEvent
        }.filter {
            it != null
        }.map {
            it!!
        }.filter {
            it.className != null
        }.map {
            it.packageName
        }.distinctUntilChanged()
    }
}