package com.velvet.applocker.ui.main

import androidx.annotation.StringRes
import com.velvet.applocker.infra.AppInfo
import com.velvet.applocker.infra.InfoText

data class MainState(
    val appList: List<AppInfo> = emptyList(),
    val infoTextList: List<InfoText> = listOf(),
    @StringRes val setPasswordErrorTextId: Int? = null
)