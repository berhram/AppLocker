package com.velvet.kamikazelock.data

import com.velvet.kamikazelock.data.cache.overlay.OverlayCacheContract
import com.velvet.kamikazelock.data.room.PasswordDao

class PasswordRepository(
    private val passwordDao: PasswordDao,
    private val overlayCache: OverlayCacheContract.RepositoryCache
) {
    //TODO all this stuff

}