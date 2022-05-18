package com.velvet.applocker.data.cache.overlay

import com.velvet.applocker.infra.ValidationStatus
import kotlinx.coroutines.flow.MutableStateFlow

class OverlayCache : OverlayCacheContract.ActivityCache, OverlayCacheContract.RepositoryCache, OverlayCacheContract.UiCache {
    override val statusFlow: MutableStateFlow<ValidationStatus?> = MutableStateFlow(null)
    override val passwordFlow: MutableStateFlow<String?> = MutableStateFlow(null)
}