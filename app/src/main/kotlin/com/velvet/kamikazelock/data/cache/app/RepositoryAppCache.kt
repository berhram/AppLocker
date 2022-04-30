package com.velvet.kamikazelock.data.cache.app

import com.velvet.kamikazelock.data.infra.AppInfo
import com.velvet.kamikazelock.data.infra.AppStatus
import kotlinx.coroutines.flow.MutableStateFlow

interface RepositoryAppCache {
    val status: MutableStateFlow<AppStatus?>
    val apps: MutableStateFlow<List<AppInfo>>
}