package com.velvet.kamikazelock.ui.main

import com.velvet.kamikazelock.R
import com.velvet.kamikazelock.data.infra.AppInfo
import com.velvet.kamikazelock.data.infra.InfoText
import com.velvet.kamikazelock.data.infra.TextType

data class MainState(
    val isChangeKeyDialogEnabled: Boolean = false,
    val isChangeFaceDialogEnabled: Boolean = false,
    val isAppLockDialogEnabled: Boolean = false,
    val appList: List<AppInfo> = emptyList(),
    val isAppLoading: Boolean = false,
    val isOverlayPermissionNeed: Boolean = false,
    val isUsageStatsPermissionNeed: Boolean = false,
    val infoTextList: List<InfoText> = listOf()
)