package com.velvet.applocker.data

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import com.velvet.applocker.data.cache.app.AppCacheContract
import com.velvet.applocker.infra.AppInfo
import com.velvet.applocker.infra.Face
import com.velvet.applocker.data.room.LockedAppsDao


class AppRepository(
    private val packageManager: PackageManager,
    private val lockedAppsDao: LockedAppsDao,
    private val appCache: AppCacheContract.RepositoryCache
    ) {

    private val lockedAppPackageSet = HashSet<String>()

    fun changeFace(newFace: Face) {
        packageManager.setComponentEnabledSetting(
            ComponentName("com.velvet.applocker", "com.velvet.applocker.ui.main.MainActivity"),
            if (newFace == Face.DEFAULT) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        packageManager.setComponentEnabledSetting(
            ComponentName("com.velvet.applocker", "com.velvet.applocker.MainActivityScheduleAlias"),
            if (newFace == Face.SCHEDULE) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        packageManager.setComponentEnabledSetting(
            ComponentName("com.velvet.applocker", "com.velvet.applocker.MainActivityFitnessAlias"),
            if (newFace == Face.FITNESS) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun fetchApps() {
        lockedAppPackageSet.clear()
        lockedAppsDao.syncLockedApps().forEach { app ->
            lockedAppPackageSet.add(app.packageName)
        }
        val output = ArrayList<AppInfo>()
        packageManager.queryIntentActivities(
            Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }, 0
        ).forEach { resolveInfo ->
            with(resolveInfo) {
                output.add(
                    AppInfo(
                    name = loadLabel(packageManager) as String,
                    packageName = activityInfo.packageName,
                    icon = loadIcon(packageManager),
                    isLocked = lockedAppPackageSet.contains(activityInfo.packageName),
                    isChanged = false
                    )
                )
            }
        }
        appCache.apps.tryEmit(output)
    }

    fun lockApps(apps: List<AppInfo>) {
        apps.forEach {
            lockedAppsDao.lockApp(it.toLockedApp())
        }
    }

    fun unlockApps(apps: List<AppInfo>) {
        apps.forEach {
            lockedAppsDao.unlockApp(it.toLockedApp())
        }
    }
}