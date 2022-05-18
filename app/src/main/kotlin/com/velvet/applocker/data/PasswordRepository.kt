package com.velvet.applocker.data

import com.velvet.applocker.data.cache.overlay.OverlayCacheContract
import com.velvet.applocker.infra.Password
import com.velvet.applocker.infra.ValidationStatus
import com.velvet.applocker.data.room.PasswordDao

class PasswordRepository(
    private val passwordDao: PasswordDao,
    private val overlayCache: OverlayCacheContract.RepositoryCache
) {
    fun setNewPassword(newPassword: String) {
        passwordDao.createPassword(
            Password(password = newPassword)
        )
    }
    
    suspend fun observePassword() {
        overlayCache.passwordFlow.collect {
            it?.let {
                if (passwordDao.isPasswordCreated() != 1) {
                    overlayCache.statusFlow.tryEmit(ValidationStatus.FAILURE_NO_PASSWORD_SET)
                } else {
                    when (it) {
                        passwordDao.getPassword().password -> {
                            overlayCache.statusFlow.tryEmit(ValidationStatus.SUCCESS)
                        }
                        else -> {
                            overlayCache.statusFlow.tryEmit(ValidationStatus.FAILURE_WRONG_PASSWORD)
                        }
                    }
                }
            }
        }
    }

}