package com.velvet.kamikazelock.ui.main

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velvet.kamikazelock.*
import com.velvet.kamikazelock.data.PermissionChecker
import com.velvet.kamikazelock.data.AppRepository
import com.velvet.kamikazelock.data.PasswordRepository
import com.velvet.kamikazelock.data.cache.app.AppCacheContract
import com.velvet.kamikazelock.infra.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

class MainViewModel(
    private val appRepository: AppRepository,
    private val appCache: AppCacheContract.UiCache,
    private val permissionChecker: PermissionChecker,
    private val passwordRepository: PasswordRepository
    ) : ViewModel(), ContainerHost<MainState, MainEffect> {

    override val container: Container<MainState, MainEffect> = container(MainState())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                appCache.apps.collect {
                    intent { reduce { state.copy(appList = it) } }
                }
            }
            launch { appRepository.observeLockedApps() }
            launch {
                permissionChecker.usagePermissionFlow.collect { granted ->
                    intent { reduce { state.copy(isUsageStatsPermissionGranted = granted) } }
                    recomposeInfoTexts()
                }
            }
            launch {
                permissionChecker.overlayPermissionFlow.collect { granted ->
                    intent { reduce { state.copy(isOverlayPermissionGranted = granted) } }
                    recomposeInfoTexts()
                }
            }
        }
    }

    //Face

    fun faceChangeChoice(newFace: Face) = intent {
        appRepository.changeFace(newFace)
        reduce { state.copy(isChangeFaceDialogEnabled = false) }
        postSideEffect(MainEffect.IconChanged)
    }

    fun faceChangeSwitch() = intent {
        reduce { state.copy(isChangeFaceDialogEnabled = !state.isChangeFaceDialogEnabled) }
    }

    //Lock

    fun appLockChoice(appInfo: AppInfo) = intent {
        reduce { state.copy(appList = state.appList.onAppLockChoice(appInfo)) }
    }

    fun appLockDialogSwitch() = intent {
        if (state.isAppLockDialogEnabled) {
            reduce { state.copy(isAppLockDialogEnabled = false) }
        } else {
            reduce { state.copy(isAppLockDialogEnabled = true, appList = emptyList()) }
            viewModelScope.launch(Dispatchers.IO) { appRepository.fetchApps() }
        }
    }

    fun applyLock() = intent {
        appRepository.lockApps(state.appList.filter { it.isChanged && !it.isLocked })
        appRepository.unlockApps(state.appList.filter { it.isChanged && it.isLocked })
        reduce { state.copy(
            isAppLockDialogEnabled = false,
            appList = state.appList.resetEnabledStates())
        }
    }

    //Password change

    fun passwordDialogSwitch() = intent {
        if (state.isChangePasswordDialogEnabled) {
            reduce { state.resetAndClosePasswordDialog() }
        } else {
            reduce { state.copy(isChangePasswordDialogEnabled = true) }
        }
    }

    fun onNewTruePasswordChange(newTruePassword: String) = intent {
        reduce { state.copy(newTruePassword = newTruePassword) }
    }

    fun onNewFalsePasswordChange(newFalsePassword: String) = intent {
        reduce { state.copy(newFalsePassword = newFalsePassword) }
    }

    fun setNewPassword(newTruePassword: String, newFalsePassword: String) = intent {
        if (newFalsePassword.length >= Password.MAX_PASSWORD_LENGTH || newTruePassword.length >= Password.MAX_PASSWORD_LENGTH) {
            reduce { state.copy(newPasswordErrorTextId = R.string.too_long) }
        } else if (newFalsePassword.length <= Password.MAX_PASSWORD_LENGTH || newTruePassword.length <= Password.MIN_PASSWORD_LENGTH) {
            reduce { state.copy(newPasswordErrorTextId = R.string.password_too_short) }
        } else if (!newFalsePassword.isDigitsOnly() || !newTruePassword.isDigitsOnly()) {
            reduce { state.copy(newPasswordErrorTextId = R.string.password_not_digits) }
        } else if (newFalsePassword == newTruePassword) {
            reduce { state.copy(newPasswordErrorTextId = R.string.passwords_same) }
        } else {
            passwordRepository.setNewPassword(newTrue = newTruePassword, newFalse = newFalsePassword)
            passwordDialogSwitch()
        }
    }

    //Infra

    private fun recomposeInfoTexts() = intent {
        val newList: ArrayList<InfoText> = ArrayList()
        if (!state.isUsageStatsPermissionGranted) {
            newList.add(InfoText.getUsageStatsWarning())
        }
        if (!state.isOverlayPermissionGranted) {
            newList.add(InfoText.getOverlayWarning())
        }
        newList.addAll(listOf(InfoText.getInstruction(), InfoText.getDevContacts()))
        reduce {
            state.copy(infoTextList = newList)
        }
    }
}