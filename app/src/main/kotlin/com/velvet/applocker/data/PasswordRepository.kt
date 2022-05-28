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
    
    suspend fun checkPassword() {
        if (passwordDao.isPasswordCreated() != 1) {
            overlayCache.status.send(ValidationStatus.FAILURE_NO_PASSWORD_SET)
        } else {
            when (overlayCache.password.receive()) {
                passwordDao.getPassword().password -> {
                    overlayCache.status.send(ValidationStatus.SUCCESS)
                }
                else -> {
                    overlayCache.status.send(ValidationStatus.FAILURE_WRONG_PASSWORD)
                }
            }
        }
    }
}