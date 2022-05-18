package com.velvet.applocker.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.velvet.applocker.data.CurrentAppChecker
import com.velvet.applocker.ui.overlay.OverlayActivity
import com.velvet.applocker.data.NotificationManager
import com.velvet.applocker.data.NotificationManager.Companion.NOTIFICATION_ID_APPLOCKER_PERMISSION_NEED
import com.velvet.applocker.data.NotificationManager.Companion.NOTIFICATION_ID_APPLOCKER_SERVICE
import com.velvet.applocker.data.PermissionChecker
import com.velvet.applocker.receiver.ScreenStateReceiver
import com.velvet.applocker.data.room.LockedAppsDao
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

private const val DELAY_MILLIS = 500
private const val UNLOCKED_TIME_MILLIS = 1000 * 60 * 30

class AppLockerService : Service() {

    private val appForegroundFLow by inject<CurrentAppChecker>()
    private val permissionChecker by inject<PermissionChecker>()
    private val notificationManager by inject<NotificationManager>()
    private val lockedAppsDao by inject<LockedAppsDao>()
    private var lastInvocationTime = System.currentTimeMillis()
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val lockedAppPackages: HashMap<String, Long> = HashMap()
    private var observeForegroundAppsJob: Job? = null
    private val screenStateReceiver = ScreenStateReceiver(
        onOnScreen = { observeForegroundApp() },
        onOffScreen = { stopObserveForegroundApp() })

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
                lockedAppPackages.clear()
                apps.forEach { app ->
                    lockedAppPackages[app.packageName] = 0
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
        if (
            lockedAppPackages.contains(foregroundAppPackage) &&
            (System.currentTimeMillis() - lockedAppPackages[foregroundAppPackage]!! >= UNLOCKED_TIME_MILLIS) &&
            (System.currentTimeMillis() - lastInvocationTime >= DELAY_MILLIS) &&
            (permissionChecker.isOverlayPermissionGranted())
        ) {
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
            permissionChecker.allPermissionFlow.collect { granted ->
                if (granted) {
                    hidePermissionNeedNotification()
                } else {
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
        val notification = notificationManager.createPermissionNeedNotification()
        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID_APPLOCKER_PERMISSION_NEED, notification)
    }

    private fun hidePermissionNeedNotification() {
        NotificationManagerCompat.from(applicationContext).cancel(NOTIFICATION_ID_APPLOCKER_PERMISSION_NEED)
    }
}