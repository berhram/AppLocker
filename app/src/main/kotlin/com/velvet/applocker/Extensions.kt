package com.velvet.applocker

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.velvet.applocker.infra.AppInfo
import com.velvet.applocker.ui.composable.AppTheme
import com.velvet.applocker.ui.composable.SystemUiSetup

fun ComponentActivity.setThemedContent(content: @Composable () -> Unit) {
    this.actionBar?.hide()
    setContent {
        AppTheme {
            SystemUiSetup()
            content()
        }
    }
}

fun MutableList<AppInfo>.xor(appInfo: AppInfo) {
    if (this.contains(appInfo)) {
        this.remove(appInfo)
    } else {
        this.add(appInfo)
    }
}