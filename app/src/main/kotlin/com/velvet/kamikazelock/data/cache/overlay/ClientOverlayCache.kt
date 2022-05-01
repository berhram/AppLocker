package com.velvet.kamikazelock.data.cache.overlay

import com.velvet.kamikazelock.data.infra.ValidationStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface ClientOverlayCache {
    val successFlow: MutableStateFlow<ValidationStatus?>
}