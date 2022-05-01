package com.velvet.kamikazelock.bg

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.velvet.kamikazelock.OverlayActivity
import com.velvet.kamikazelock.bg.NotificationManager.Companion.NOTIFICATION_ID_APPLOCKER_PERMISSION_NEED
import com.velvet.kamikazelock.bg.NotificationManager.Companion.NOTIFICATION_ID_APPLOCKER_SERVICE
import com.velvet.kamikazelock.bg.receiver.ScreenStateReceiver
import com.velvet.kamikazelock.data.room.LockedAppsDao
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class AppLockerService : Service() {

    private val appForegroundFLow by inject<AppForegroundFlow>()
    private val permissionChecker by inject<PermissionChecker>()
    private val notificationManager by inject<NotificationManager>()
    private val lockedAppsDao by inject<LockedAppsDao>()
    private var lastInvocationTime = System.currentTimeMillis()
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val lockedAppPackageSet: HashSet<String> = HashSet()
    private var observeForegroundAppsJob: Job? = null
    private val screenStateReceiver = ScreenStateReceiver(
        onOnScreen = { observeForegroundApp() },
        onOffScreen = { stopObserveForegroundApp() })
    companion object {
        private const val DELAY_MILLIS = 500
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        observeForegroundApp()
        observeLockedApps()
        registerScreenReceiver()
        showServiceNotification()
        observePermissionChecker()
    }

    override fun onDestroy() {
        ContextCompat.startForegroundService(applicationContext, Intent(applicationContext, AppLockerService::class.java))
        unregisterScreenReceiver()
        job.cancel()
    }

    //Locked apps

    private fun observeLockedApps() {
        scope.launch {
            lockedAppsDao.getLockedAppsDistinctUntilChanged().collect { apps ->
                lockedAppPackageSet.clear()
                apps.forEach { app ->
                    Log.d("OVER", "${app.packageName} add to service locked apps")
                    lockedAppPackageSet.add(app.packageName)
                }
            }
        }
    }

    //Foreground app

    private fun observeForegroundApp() {
        scope.launch {
            appForegroundFLow.get().collect { foregroundAppPackage ->
                onAppForeground(foregroundAppPackage)
            }
        }
    }

    private fun stopObserveForegroundApp() {
        observeForegroundAppsJob?.cancel()
    }

    private fun onAppForeground(foregroundAppPackage: String) {
        if (lockedAppPackageSet.contains(foregroundAppPackage) && (System.currentTimeMillis() - lastInvocationTime) >= DELAY_MILLIS) {
            Log.d("OVER", "$foregroundAppPackage is locked and intent send")
            val intent = OverlayActivity.newIntent(applicationContext, foregroundAppPackage)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            lastInvocationTime = System.currentTimeMillis()
        }
    }

    //Screen state receiver

    private fun registerScreenReceiver() {
        val screenFilter = IntentFilter()
        screenFilter.addAction(Intent.ACTION_SCREEN_ON)
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenStateReceiver, screenFilter)
    }

    private fun unregisterScreenReceiver() {
        unregisterReceiver(screenStateReceiver)
    }

    //Permission checker

    private fun observePermissionChecker() {
        scope.launch {
            permissionChecker.usagePermissionFlow.collect { granted ->
                if (granted) {
                    Log.d("APPS", "perms no need")
                    hidePermissionNeedNotification()
                } else {
                    Log.d("APPS", "perms need")
                    showPermissionNeedNotification()
                }
            }
        }
    }

    //Notification

    private fun showServiceNotification() {
        val notification = notificationManager.createNotification()
        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID_APPLOCKER_SERVICE, notification)
        startForeground(NOTIFICATION_ID_APPLOCKER_SERVICE, notification)
    }

    private fun showPermissionNeedNotification() {
        Log.d("APPS", "show perms need notif")
        val notification = notificationManager.createPermissionNeedNotification()
        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID_APPLOCKER_PERMISSION_NEED, notification)
    }

    private fun hidePermissionNeedNotification() {
        NotificationManagerCompat.from(applicationContext).cancel(NOTIFICATION_ID_APPLOCKER_PERMISSION_NEED)
    }
}