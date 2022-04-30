package com.velvet.kamikazelock.data.cache.overlay

import com.velvet.kamikazelock.data.infra.ValidationStatus
import kotlinx.coroutines.flow.MutableSharedFlow

class OverlayCache : ClientOverlayCache, ServiceOverlayCache {

    override val passwordFlow: MutableSharedFlow<String> = MutableSharedFlow()
    override val successFlow: MutableSharedFlow<ValidationStatus> = MutableSharedFlow()
}