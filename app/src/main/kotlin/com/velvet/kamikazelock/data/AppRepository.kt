package com.velvet.kamikazelock.data

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import com.velvet.kamikazelock.data.cache.app.AppCacheContract
import com.velvet.kamikazelock.data.infra.AppInfo
import com.velvet.kamikazelock.data.infra.Face
import com.velvet.kamikazelock.data.room.LockedAppsDao


class AppRepository(
    private val packageManager: PackageManager,
    private val lockedAppsDao: LockedAppsDao,
    private val appCache: AppCacheContract.RepositoryCache
    ) {

    private val lockedAppPackageSet = HashSet<String>()

    fun changeFace(newFace: Face) {
        packageManager.setComponentEnabledSetting(
            ComponentName("com.velvet.kamikazelock", "com.velvet.kamikazelock.MainActivity"),
            if (newFace == Face.DEFAULT) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        packageManager.setComponentEnabledSetting(
            ComponentName("com.velvet.kamikazelock", "com.velvet.kamikazelock.MainActivityScheduleAlias"),
            if (newFace == Face.SCHEDULE) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        packageManager.setComponentEnabledSetting(
            ComponentName("com.velvet.kamikazelock", "com.velvet.kamikazelock.MainActivityFitnessAlias"),
            if (newFace == Face.FITNESS) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun fetchApps() {
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

    suspend fun observeLockedApps()  {
        lockedAppsDao.getLockedAppsDistinctUntilChanged().collect { apps ->
            lockedAppPackageSet.clear()
            apps.forEach { app ->
                lockedAppPackageSet.add(app.packageName)
            }
        }
    }
}