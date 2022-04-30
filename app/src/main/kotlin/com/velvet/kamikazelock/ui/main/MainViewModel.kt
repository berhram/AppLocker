package com.velvet.kamikazelock.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velvet.kamikazelock.data.infra.AppInfo
import com.velvet.kamikazelock.data.AppRepository
import com.velvet.kamikazelock.data.infra.Face
import com.velvet.kamikazelock.data.infra.MainStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

class MainViewModel(private val repository: AppRepository) : ViewModel(), ContainerHost<MainState, MainEffect> {
    override val container: Container<MainState, MainEffect> = container(MainState())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                repository.status.collect {
                    when (it) {
                        MainStatus.FETCHING_APPS -> intent { reduce { state.copy(isAppLoading = true) } }
                        MainStatus.FETCH_COMPLETE -> intent { reduce { state.copy(isAppLoading = false) } }
                        null -> { }
                    }
                }
            }
            launch {
                repository.apps.collect {
                    intent { reduce { state.copy(appList = it) } }
                }
            }
            launch {
                while (true) {
                    repository.fetchApps()
                    delay(5 * 1000 * 60)
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
        reduce { state.copy(appList = ArrayList(state.appList).apply {
            this.add(this.indexOf(appInfo), appInfo.copy(isChanged = !appInfo.isChanged))
            })
        }
    }

    fun appLockButtonClick() = intent {
        reduce { state.copy(isAppLockDialogEnabled = true) }
    }

    fun appLockDialogDismiss() = intent {
        reduce { state.copy(isAppLockDialogEnabled = false) }
    }

    fun applyLock() = intent {
        repository.lockApps(state.appList.filter { it.isChanged })
    }
}