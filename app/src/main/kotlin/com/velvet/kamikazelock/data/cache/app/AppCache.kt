package com.velvet.kamikazelock.data.cache.app

import com.velvet.kamikazelock.data.infra.AppInfo
import kotlinx.coroutines.flow.MutableStateFlow

class AppCache : AppCacheContract.RepositoryCache, AppCacheContract.UiCache {
    override val status: MutableStateFlow<AppStatus?> = MutableStateFlow(null)
    override val apps: MutableStateFlow<List<AppInfo>> = MutableStateFlow(emptyList())
}