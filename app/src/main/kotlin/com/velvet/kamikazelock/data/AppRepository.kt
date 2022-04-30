package com.velvet.kamikazelock.data

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import com.velvet.kamikazelock.data.infra.AppInfo
import com.velvet.kamikazelock.data.infra.Face
import com.velvet.kamikazelock.data.infra.LockedApp
import com.velvet.kamikazelock.data.infra.MainStatus
import com.velvet.kamikazelock.data.room.LockedAppsDao
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow


class AppRepository(private val packageManager: PackageManager, private val lockedAppsDao: LockedAppsDao) {

    private val _status: MutableSharedFlow<MainStatus?> = MutableSharedFlow()
    val status: SharedFlow<MainStatus?> = _status
    private val _apps: MutableSharedFlow<List<AppInfo>> = MutableSharedFlow()
    val apps: SharedFlow<List<AppInfo>> = _apps

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
        _status.tryEmit(MainStatus.FETCHING_APPS)
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
        _status.tryEmit(MainStatus.FETCH_COMPLETE)
        _apps.tryEmit(output)
    }

    fun lockApps(apps: List<AppInfo>) {
        apps.forEach {
            lockedAppsDao.lockApp(it.toLockedApp())
        }
    }
}