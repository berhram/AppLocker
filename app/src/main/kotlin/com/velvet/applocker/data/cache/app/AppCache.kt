package com.velvet.applocker.data.cache.app

import com.velvet.applocker.infra.AppInfo
import kotlinx.coroutines.flow.MutableStateFlow

class AppCache : AppCacheContract.RepositoryCache, AppCacheContract.UiCache {
    override val apps: MutableStateFlow<List<AppInfo>> = MutableStateFlow(emptyList())
}