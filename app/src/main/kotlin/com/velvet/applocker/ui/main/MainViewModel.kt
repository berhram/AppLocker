package com.velvet.applocker.ui.main

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velvet.applocker.*
import com.velvet.applocker.data.PermissionChecker
import com.velvet.applocker.data.AppRepository
import com.velvet.applocker.data.PasswordRepository
import com.velvet.applocker.data.cache.app.AppCacheContract
import com.velvet.applocker.infra.*
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
            launch { appRepository.fetchApps() }
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

    fun applyLock(changedList: List<AppInfo>) = intent {
        appRepository.lockApps(state.appList.filter { changedList.contains(it) }.filter { it.isLocked.not() })
        appRepository.unlockApps(state.appList.filter { changedList.contains(it) }.filter { it.isLocked })
    }

    //Password change

    fun passwordDialogSwitch() = intent {
        if (state.isChangePasswordDialogEnabled) {
            reduce { state.resetAndClosePasswordDialog() }
        } else {
            reduce { state.copy(isChangePasswordDialogEnabled = true) }
        }
    }

    fun setNewPassword(newPassword: String) = intent {
        if (newPassword.length > Password.MAX_PASSWORD_LENGTH) {
            reduce { state.copy(newPasswordErrorTextId = R.string.too_long) }
        } else if (newPassword.length < Password.MIN_PASSWORD_LENGTH) {
            reduce { state.copy(newPasswordErrorTextId = R.string.password_too_short) }
        } else if (!newPassword.isDigitsOnly()) {
            reduce { state.copy(newPasswordErrorTextId = R.string.password_not_digits) }
        } else {
            passwordRepository.setNewPassword(newPassword = newPassword)
            passwordDialogSwitch()
        }
    }

    //Infra

    private fun recomposeInfoTexts() = intent {
        val newList: ArrayList<InfoText> = ArrayList()
        newList.add(InfoText.createWelcome())
        if (!state.isUsageStatsPermissionGranted) {
            newList.add(InfoText.createUsageStatsWarning())
        }
        if (!state.isOverlayPermissionGranted) {
            newList.add(InfoText.createOverlayWarning())
        }
        newList.addAll(listOf(InfoText.createInstruction(), InfoText.createDevContacts()))
        reduce {
            state.copy(infoTextList = newList)
        }
    }
}