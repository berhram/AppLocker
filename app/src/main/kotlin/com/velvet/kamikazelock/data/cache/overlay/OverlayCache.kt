package com.velvet.kamikazelock.data.cache.overlay

import com.velvet.kamikazelock.data.infra.ValidationStatus
import kotlinx.coroutines.flow.MutableStateFlow

class OverlayCache : ClientOverlayCache, ActivityOverlayCache {
    override val successFlow: MutableStateFlow<ValidationStatus?> = MutableStateFlow(null)
}