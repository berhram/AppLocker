package com.velvet.applocker.ui.main

import androidx.annotation.StringRes
import com.velvet.applocker.infra.AppInfo
import com.velvet.applocker.infra.InfoText

data class MainState(
    val isChangePasswordDialogEnabled: Boolean = false,
    val isChangeFaceDialogEnabled: Boolean = false,

    val appList: List<AppInfo> = emptyList(),
    val infoTextList: List<InfoText> = listOf(),

    val isOverlayPermissionGranted: Boolean = true,
    val isUsageStatsPermissionGranted: Boolean = true,

    @StringRes val newPasswordErrorTextId: Int? = null
)