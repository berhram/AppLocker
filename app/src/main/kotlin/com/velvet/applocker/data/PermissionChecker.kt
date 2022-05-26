package com.velvet.applocker.data

import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

private const val GUARANTEED_PERMISSION_CHECK_DELAY_MILLIS = 1000 * 60 * 15L
private const val UNGUARANTEED_PERMISSION_CHECK_DELAY_MILLIS = 1000L

class PermissionChecker(private val context: Context) {

    private var delay = UNGUARANTEED_PERMISSION_CHECK_DELAY_MILLIS

    val usagePermissionFlow = flow {
        while (true) {
            emit(isUsageAccessPermissionGranted())
            delay(delay)
        }
    }.distinctUntilChanged()

    val overlayPermissionFlow = flow {
        while (true) {
            emit(isOverlayPermissionGranted())
            delay(delay)
        }
    }.distinctUntilChanged()

    val allPermissionFlow = flow {
        while (true) {
            val allPermissionGranted = isOverlayPermissionGranted() && isUsageAccessPermissionGranted()
            setDelay(allPermissionGranted)
            emit(allPermissionGranted)
            delay(delay)
        }
    }

    private fun setDelay(isPermissionsGranted: Boolean) {
        if (isPermissionsGranted) {
            if (delay != GUARANTEED_PERMISSION_CHECK_DELAY_MILLIS) {
                delay = GUARANTEED_PERMISSION_CHECK_DELAY_MILLIS
            }
        } else {
            delay = UNGUARANTEED_PERMISSION_CHECK_DELAY_MILLIS
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
            false
        }
    }
}