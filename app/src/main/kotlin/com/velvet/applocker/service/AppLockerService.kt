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
import com.velvet.applocker.receiver.UnlockResultReceiver
import com.velvet.applocker.receiver.UnlockResultReceiver.Companion.APP_UNLOCKED
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

    private var lastInvokedApp: String? = null

    private val job = SupervisorJob()

    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val lockedAppsPackages: HashMap<String, Long> = HashMap()

    private var observeForegroundAppsJob: Job? = null

    private val screenStateReceiver = ScreenStateReceiver(
        onOnScreen = { observeForegroundApp() },
        onOffScreen = { stopObserveForegroundApp() }
    )

    private val unlockResultReceiver = UnlockResultReceiver(
        onSuccessUnlock = { successUnlock() }
    )

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
        showServiceNotification()
        observePermissionChecker()
        registerScreenReceiver()
        registerUnlockResultReceiver()
    }

    override fun onDestroy() {
        ContextCompat.startForegroundService(
            applicationContext,
            Intent(applicationContext, AppLockerService::class.java)
        )
        unregisterScreenReceiver()
        unregisterUnlockResultReceiver()
        job.cancel()
    }

    //Locked apps

    private fun observeLockedApps() {
        scope.launch {
            lockedAppsDao.getLockedAppsDistinctUntilChanged().collect { apps ->
                apps.forEach { app ->
                    if (!lockedAppsPackages.contains(app.packageName)) {
                        lockedAppsPackages[app.packageName] = 0
                    }
                }
            }
        }
    }

    private fun successUnlock() {
        lastInvokedApp?.let { lockedAppsPackages[it] = System.currentTimeMillis() }
    }

    //Foreground app

    private fun observeForegroundApp() {
        scope.launch {
            appForegroundFLow.get().collect { foregroundAppPackage ->
                if (lockedAppsPackages.contains(foregroundAppPackage)) {
                    onLockedAppForeground(foregroundAppPackage)
                }
            }
        }
    }

    private fun stopObserveForegroundApp() {
        observeForegroundAppsJob?.cancel()
    }

    private fun onLockedAppForeground(lockedAppPackage: String) {
        if (
            (System.currentTimeMillis() - lockedAppsPackages[lockedAppPackage]!! >= UNLOCKED_TIME_MILLIS) &&
            (System.currentTimeMillis() - lastInvocationTime >= DELAY_MILLIS) &&
            (permissionChecker.isOverlayPermissionGranted())
        ) {
            val intent = OverlayActivity.newIntent(applicationContext, lockedAppPackage)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            lastInvocationTime = System.currentTimeMillis()
            lastInvokedApp = lockedAppPackage
        }
    }

    //Screen state receiver

    private fun registerScreenReceiver() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenStateReceiver, filter)
    }

    private fun unregisterScreenReceiver() {
        unregisterReceiver(screenStateReceiver)
    }

    //Unlocked app receiver

    private fun registerUnlockResultReceiver() {
        val filter = IntentFilter()
        filter.addAction(APP_UNLOCKED)
        registerReceiver(unlockResultReceiver, filter)
    }

    private fun unregisterUnlockResultReceiver() {
        unregisterReceiver(unlockResultReceiver)
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
        NotificationManagerCompat.from(applicationContext)
            .cancel(NOTIFICATION_ID_APPLOCKER_PERMISSION_NEED)
    }
}