package com.velvet.kamikazelock.bg

import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration

class PermissionChecker(private val context: Context) {

    companion object {
        private const val PERMISSION_CHECK_DELAY_MILLIS = 1000 * 60 * 5L
    }

    val usagePermissionFlow = flow {
        while (true) {
            emit(isUsageAccessPermissionGranted())
            delay(PERMISSION_CHECK_DELAY_MILLIS)
        }
    }

    val overlayPermissionFlow = flow {
        while (true) {
            emit(isOverlayPermissionGranted())
            delay(PERMISSION_CHECK_DELAY_MILLIS)
        }
    }

    val allPermissionFlow = flow {
        while (true) {
            emit(isOverlayPermissionGranted() && isUsageAccessPermissionGranted())
            delay(PERMISSION_CHECK_DELAY_MILLIS)
        }
    }

    fun isOverlayPermissionGranted() : Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun isUsageAccessPermissionGranted(): Boolean {
        return try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(context.packageName, 0)
            val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOpsManager.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName
                )
            }
            else {
                appOpsManager.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d("APPS", "permis false")
            false
        }
    }
}