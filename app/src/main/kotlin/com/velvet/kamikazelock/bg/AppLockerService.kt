package com.velvet.kamikazelock.bg

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.compose.runtime.Recomposer
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import com.velvet.kamikazelock.bg.NotificationManager.Companion.NOTIFICATION_ID_APPLOCKER_PERMISSION_NEED
import com.velvet.kamikazelock.bg.NotificationManager.Companion.NOTIFICATION_ID_APPLOCKER_SERVICE
import com.velvet.kamikazelock.bg.receiver.ScreenStateReceiver
import com.velvet.kamikazelock.data.cache.overlay.ServiceOverlayCache
import com.velvet.kamikazelock.data.infra.ValidationStatus
import com.velvet.kamikazelock.data.room.LockedAppsDao
import com.velvet.kamikazelock.data.room.PasswordDao
import com.velvet.kamikazelock.ui.overlay.OverlayScreen
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.getViewModel

class AppLockerService : Service(), SavedStateRegistryOwner {

    private val serviceCache by inject<ServiceOverlayCache>()
    private val appForegroundFLow by inject<AppForegroundFlow>()
    private val permissionChecker by inject<PermissionChecker>()
    private val notificationManager by inject<NotificationManager>()
    private val lockedAppsDao by inject<LockedAppsDao>()
    private val passwordDao by inject<PasswordDao>()

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val lockedAppPackageSet: HashSet<String> = HashSet()
    private var isOverlayShowing = false
    private var observeForegroundAppsJob: Job? = null


    private val screenStateReceiver = ScreenStateReceiver(
        onOnScreen = { observeForegroundApp() },
        onOffScreen = { stopObserveForegroundApp() }
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
        registerScreenReceiver()
        showServiceNotification()
        observePermissionChecker()
        observePassword()
    }

    override fun onDestroy() {
        ContextCompat.startForegroundService(applicationContext, Intent(applicationContext, AppLockerService::class.java))
        unregisterScreenReceiver()
        job.cancel()
    }

    //Password

    private fun observePassword() {
        scope.launch {
            serviceCache.passwordFlow.collect {
                if (passwordDao.getPassword().realPassword == it) {
                    serviceCache.successFlow.emit(ValidationStatus.SUCCESS)
                    hideOverlay()
                } else {
                    serviceCache.successFlow.emit(ValidationStatus.FAILURE)
                }
            }
        }
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
        Log.d("OVER", "$foregroundAppPackage in foreground")
        if (lockedAppPackageSet.contains(foregroundAppPackage)) {
            Log.d("OVER", "app locked")
            if (permissionChecker.isOverlayPermissionGranted()) {
                Log.d("OVER", "showOverlay after perm")
                showOverlay(foregroundAppPackage)
                //TODO create one more notification for overlay perm
                hidePermissionNeedNotification()
            } else {
                showPermissionNeedNotification()
            }
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