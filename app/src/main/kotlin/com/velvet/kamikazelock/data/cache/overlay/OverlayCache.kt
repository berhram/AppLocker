package com.velvet.kamikazelock.data.cache.overlay

import com.velvet.kamikazelock.data.infra.ValidationStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OverlayCache : OverlayCacheContract.ActivityCache, OverlayCacheContract.RepositoryCache, OverlayCacheContract.UiCache {
    override val statusFlow: MutableStateFlow<ValidationStatus?> = MutableStateFlow(null)
    override val passwordFlow: MutableStateFlow<String?> = MutableStateFlow(null)
}