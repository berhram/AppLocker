package com.velvet.applocker

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import com.velvet.applocker.infra.AppInfo
import com.velvet.applocker.ui.components.AppTheme
import com.velvet.applocker.ui.components.SystemUISetup
import com.velvet.applocker.ui.main.MainState

fun MainState.resetAndClosePasswordDialog() : MainState {
    return this.copy(newPasswordErrorTextId = null, isChangePasswordDialogEnabled = false)
}

fun ComponentActivity.setThemedContent(content: @Composable () -> Unit) {
    this.actionBar?.hide()
    setContent {
        AppTheme() {
            SystemUISetup()
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

fun LazyListState.isScrolledToTheEnd() : Boolean {
    Log.d("Compose", "scroll to end")
    return layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
}