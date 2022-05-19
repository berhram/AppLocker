package com.velvet.applocker

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.velvet.applocker.infra.AppInfo
import com.velvet.applocker.ui.SystemUISetup
import com.velvet.applocker.ui.main.MainState

fun List<AppInfo>.resetEnabledStates() : List<AppInfo> {
    val output = ArrayList<AppInfo>()
    this.forEach {
        output.add(it.copy(isChanged = false))
    }
    return output
}

fun List<AppInfo>.onAppLockChoice(appInfo: AppInfo) : List<AppInfo> {
    val output = ArrayList(this)
    output[output.indexOf(appInfo)] = appInfo.copy(isChanged = !appInfo.isChanged)
    return output
}

fun MainState.resetAndClosePasswordDialog() : MainState {
    return this.copy(newPasswordErrorTextId = null, isChangePasswordDialogEnabled = false)
}

fun ComponentActivity.setThemedContent(content: @Composable () -> Unit) {
    this.actionBar?.hide()
    setContent {
        MaterialTheme(colors = if (isSystemInDarkTheme()) darkColors() else lightColors()) {
            SystemUISetup()
            content()
        }
    }
}