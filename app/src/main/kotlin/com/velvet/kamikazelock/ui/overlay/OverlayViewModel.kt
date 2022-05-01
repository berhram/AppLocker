package com.velvet.kamikazelock.ui.overlay

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velvet.kamikazelock.data.AppRepository
import com.velvet.kamikazelock.data.cache.overlay.ClientOverlayCache
import com.velvet.kamikazelock.data.infra.ValidationStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

class OverlayViewModel(
    private val appPackageName: String,
    private val clientCache: ClientOverlayCache
    ) : ViewModel(), ContainerHost<OverlayState, OverlayEffect> {

    override val container: Container<OverlayState, OverlayEffect> = container(OverlayState())

    init {
        Log.d("OVER", "overlay viewmodel init")
        viewModelScope.launch(Dispatchers.IO) {
            clientCache.successFlow.collect {
                when (it) {
                    ValidationStatus.SUCCESS -> { }
                    ValidationStatus.FAILURE -> { }
                }
            }
        }
    }

    fun enterDigit(digit: Int) = intent {
        if (state.password.length <= 12) {
            reduce { state.copy(password = state.password + digit) }
        }
    }

    fun deleteDigit() = intent {
        reduce { state.copy(password = state.password.dropLast(1)) }
    }

    fun confirm() = intent {
        viewModelScope.launch(Dispatchers.IO) {
            //clientCache.passwordFlow.tryEmit((state.password))
        }
    }
}
