package com.velvet.kamikazelock.data

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.velvet.kamikazelock.data.cache.app.RepositoryAppCache
import com.velvet.kamikazelock.data.infra.AppInfo
import com.velvet.kamikazelock.data.infra.Face
import com.velvet.kamikazelock.data.infra.LockedApp
import com.velvet.kamikazelock.data.infra.AppStatus
import com.velvet.kamikazelock.data.room.LockedAppsDao


class AppRepository(
    private val packageManager: PackageManager,
    private val lockedAppsDao: LockedAppsDao,
    private val appCache: RepositoryAppCache
    ) {

    fun changeFace(newFace: Face) {
        packageManager.setComponentEnabledSetting(
            ComponentName("com.velvet.kamikazelock", "com.velvet.kamikazelock.AppActivity"),
            if (newFace == Face.DEFAULT) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        packageManager.setComponentEnabledSetting(
            ComponentName("com.velvet.kamikazelock", "com.velvet.kamikazelock.AppActivityScheduleAlias"),
            if (newFace == Face.SCHEDULE) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        packageManager.setComponentEnabledSetting(
            ComponentName("com.velvet.kamikazelock", "com.velvet.kamikazelock.AppActivityFitnessAlias"),
            if (newFace == Face.FITNESS) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun fetchApps() {
        appCache.status.tryEmit(AppStatus.FETCHING_APPS)
        Log.d("LOCK", "status emitted")
        val output = ArrayList<AppInfo>()
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        packageManager.queryIntentActivities(intent, 0).forEach { resolveInfo ->
            with(resolveInfo) {
                output.add(
                    AppInfo(
                    name = loadLabel(packageManager) as String,
                    packageName = activityInfo.packageName,
                    icon = loadIcon(packageManager),
                    isLocked = lockedAppsDao.downloadLockedApps().contains(LockedApp(activityInfo.packageName)),
                    isChanged = false
                )
                )
            }
        }
        appCache.status.tryEmit(AppStatus.FETCH_COMPLETE)
        appCache.apps.tryEmit(output)
        Log.d("LOCK", "status and apps emitted")
    }

    fun lockApps(apps: List<AppInfo>) {
        apps.forEach {
            lockedAppsDao.lockApp(it.toLockedApp())
        }
    }
}