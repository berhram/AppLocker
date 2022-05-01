package com.velvet.kamikazelock.ui.main

import android.util.Log
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velvet.kamikazelock.R
import com.velvet.kamikazelock.bg.PermissionChecker
import com.velvet.kamikazelock.data.AppRepository
import com.velvet.kamikazelock.data.cache.app.ClientAppCache
import com.velvet.kamikazelock.data.infra.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

class MainViewModel(
    private val repository: AppRepository,
    private val appCache: ClientAppCache,
    private val permissionChecker: PermissionChecker
    ) : ViewModel(), ContainerHost<MainState, MainEffect> {

    override val container: Container<MainState, MainEffect> = container(MainState())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                appCache.status.collect {
                    Log.d("LOCK", "status collected $it")
                    when (it) {
                        AppStatus.FETCHING_APPS -> intent { reduce { state.copy(isAppLoading = true) } }
                        AppStatus.FETCH_COMPLETE -> intent { reduce { state.copy(isAppLoading = false) } }
                        null -> { }
                    }
                }
            }
            launch {
                appCache.apps.collect {
                    Log.d("LOCK", "apps collected")
                    intent { reduce { state.copy(appList = it) } }
                }
            }
            launch { repository.observeLockedApps() }
            launch {
                permissionChecker.usagePermissionFlow.collect { granted ->
                    intent { reduce { state.copy(isUsageStatsPermissionNeed = !granted) } }
                    recomposeInfoTexts()
                }
            }
        }
    }

    //Face

    fun faceChangeChoice(newFace: Face) = intent {
        repository.changeFace(newFace)
        reduce { state.copy(isChangeFaceDialogEnabled = false) }
        postSideEffect(MainEffect.IconChanged)
    }

    fun faceChangeButtonClick() = intent {
        reduce { state.copy(isChangeFaceDialogEnabled = true) }
    }

    fun faceChangeDialogDismiss() = intent {
        reduce { state.copy(isChangeFaceDialogEnabled = false) }
    }

    //Lock

    fun appLockChoice(appInfo: AppInfo) = intent {
        val newList = ArrayList(state.appList)
        newList[newList.indexOf(appInfo)] = appInfo.copy(isChanged = !appInfo.isChanged)
        reduce {
            state.copy(appList = newList)
        }
    }

    fun appLockButtonClick() = intent {
        viewModelScope.launch(Dispatchers.IO) { repository.fetchApps() }
        reduce { state.copy(isAppLockDialogEnabled = true) }
    }

    fun appLockDialogDismiss() = intent {
        reduce { state.copy(isAppLockDialogEnabled = false) }
    }

    fun applyLock() = intent {
        repository.lockApps(state.appList.filter { it.isChanged && !it.isLocked })
        repository.unlockApps(state.appList.filter { it.isChanged && it.isLocked })
        reduce { state.copy(isAppLockDialogEnabled = false) }
        val newList = ArrayList<AppInfo>()
        //TODO make this a extension
        state.appList.forEach {
            newList.add(it.copy(isChanged = false))
        }
        reduce { state.copy(appList = newList) }
    }

    private fun recomposeInfoTexts() = intent {
        val newList: ArrayList<InfoText> = ArrayList()
        if (state.isUsageStatsPermissionNeed) {
            newList.add(InfoText.getUsageStatsWarning())
        }
        newList.addAll(listOf(InfoText.getInstruction(), InfoText.getDevContacts()))
        reduce {
            state.copy(infoTextList = newList)
        }
    }
}