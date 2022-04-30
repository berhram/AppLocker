package com.velvet.kamikazelock.data.cache.app

import com.velvet.kamikazelock.data.infra.AppInfo
import com.velvet.kamikazelock.data.infra.AppStatus
import kotlinx.coroutines.flow.StateFlow

interface ClientAppCache {
    val status: StateFlow<AppStatus?>
    val apps: StateFlow<List<AppInfo>>
}