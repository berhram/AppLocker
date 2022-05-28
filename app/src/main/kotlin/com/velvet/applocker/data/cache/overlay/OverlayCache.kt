package com.velvet.applocker.data.cache.overlay

import com.velvet.applocker.infra.ValidationStatus
import kotlinx.coroutines.channels.Channel

class OverlayCache : OverlayCacheContract.ActivityCache, OverlayCacheContract.RepositoryCache,
    OverlayCacheContract.UiCache {
    override val status = Channel<ValidationStatus>(1)
    override val password = Channel<String>(1)
}