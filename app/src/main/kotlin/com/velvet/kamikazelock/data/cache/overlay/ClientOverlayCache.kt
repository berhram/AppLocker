package com.velvet.kamikazelock.data.cache.overlay

import com.velvet.kamikazelock.data.infra.ValidationStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

interface ClientOverlayCache {
    val passwordFlow: MutableSharedFlow<String>
    val successFlow: SharedFlow<ValidationStatus>
}