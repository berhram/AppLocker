package com.velvet.kamikazelock.data.cache.app

import com.velvet.kamikazelock.data.infra.AppInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppCacheContract {
    interface UiCache {
        val apps: StateFlow<List<AppInfo>>
    }

    interface RepositoryCache {
        val apps: MutableStateFlow<List<AppInfo>>
    }
}