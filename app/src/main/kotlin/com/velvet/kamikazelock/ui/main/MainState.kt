package com.velvet.kamikazelock.ui.main

import androidx.annotation.StringRes
import com.velvet.kamikazelock.infra.AppInfo
import com.velvet.kamikazelock.infra.InfoText

data class MainState(
    val isChangePasswordDialogEnabled: Boolean = false,
    val isChangeFaceDialogEnabled: Boolean = false,
    val isAppLockDialogEnabled: Boolean = false,

    val appList: List<AppInfo> = emptyList(),
    val infoTextList: List<InfoText> = listOf(),

    val isOverlayPermissionGranted: Boolean = true,
    val isUsageStatsPermissionGranted: Boolean = true,

    val newTruePassword: String = "",
    val newFalsePassword: String = "",
    @StringRes val newPasswordErrorTextId: Int? = null
)