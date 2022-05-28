package com.velvet.applocker.data.cache.overlay

import com.velvet.applocker.infra.ValidationStatus
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

class OverlayCacheContract {
    interface UiCache {
        val password: SendChannel<String>
    }

    interface ActivityCache {
        val status: ReceiveChannel<ValidationStatus>
    }

    interface RepositoryCache {
        val password: ReceiveChannel<String>
        val status: SendChannel<ValidationStatus>
    }
}