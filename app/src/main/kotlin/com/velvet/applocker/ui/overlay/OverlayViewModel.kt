package com.velvet.applocker.ui.overlay

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velvet.applocker.data.PasswordRepository
import com.velvet.applocker.data.cache.overlay.OverlayCacheContract
import com.velvet.applocker.infra.Password
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import kotlin.math.log

class OverlayViewModel(
    private val clientCache: OverlayCacheContract.UiCache,
    private val repository: PasswordRepository
    ) : ViewModel(), ContainerHost<OverlayState, OverlayEffect> {

    override val container: Container<OverlayState, OverlayEffect> = container(OverlayState())

    fun enterDigit(digit: Int) = intent {
        if (state.password.length <= Password.MAX_PASSWORD_LENGTH) {
            reduce { state.copy(password = state.password + digit) }
        }
    }

    fun deleteDigit() = intent {
        reduce { state.copy(password = state.password.dropLast(1)) }
    }

    fun confirm() = intent {
        viewModelScope.launch(Dispatchers.IO) {
            if (state.password.length >= Password.MIN_PASSWORD_LENGTH) {
                Log.d("CAN", "emitted ${state.password}")
                clientCache.password.send(state.password)
                repository.checkPassword()
            } else {
                postSideEffect(OverlayEffect.PasswordTooShort)
            }
        }
    }
}
