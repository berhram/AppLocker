package com.velvet.kamikazelock.data.cache.app

import com.velvet.kamikazelock.data.infra.AppInfo
import com.velvet.kamikazelock.data.infra.AppStatus
import kotlinx.coroutines.flow.MutableStateFlow

class AppCache : ClientAppCache, RepositoryAppCache {
    override val status: MutableStateFlow<AppStatus?> = MutableStateFlow(null)
    override val apps: MutableStateFlow<List<AppInfo>> = MutableStateFlow(emptyList())
}