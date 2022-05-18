package com.velvet.applocker.data.cache.overlay

import com.velvet.applocker.infra.ValidationStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OverlayCacheContract {
    interface UiCache {
        val passwordFlow: MutableStateFlow<String?>
    }

    interface ActivityCache {
        val statusFlow: StateFlow<ValidationStatus?>
    }

    interface RepositoryCache {
        val passwordFlow: StateFlow<String?>
        val statusFlow: MutableStateFlow<ValidationStatus?>
    }
}