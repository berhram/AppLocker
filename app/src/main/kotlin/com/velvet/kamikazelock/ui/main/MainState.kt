package com.velvet.kamikazelock.ui.main

import com.velvet.kamikazelock.data.infra.AppInfo

data class MainState(
    val isChangeKeyDialogEnabled: Boolean = false,
    val isChangeFaceDialogEnabled: Boolean = false,
    val isAppLockDialogEnabled: Boolean = false,
    val appList: List<AppInfo> = emptyList(),
    //val checkedApps: List<AppInfo> = emptyList(),
    val isAppLoading: Boolean = false
)