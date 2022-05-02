package com.velvet.kamikazelock

import com.velvet.kamikazelock.data.infra.AppInfo
import com.velvet.kamikazelock.ui.main.MainState

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
    return this.copy(newFalsePassword = "",
        newTruePassword = "",
        newPasswordErrorTextId = null,
        isChangePasswordDialogEnabled = false)
}