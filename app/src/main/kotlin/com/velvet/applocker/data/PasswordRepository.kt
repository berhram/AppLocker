package com.velvet.applocker.data

import com.velvet.applocker.data.cache.overlay.OverlayCacheContract
import com.velvet.applocker.infra.Password
import com.velvet.applocker.infra.ValidationStatus
import com.velvet.applocker.data.room.PasswordDao

class PasswordRepository(
    private val passwordDao: PasswordDao,
    private val overlayCache: OverlayCacheContract.RepositoryCache
) {
    fun setNewPassword(newTrue: String, newFalse: String) {
        passwordDao.createPassword(
            Password(truePassword = newTrue, falsePassword = newFalse)
        )
    }
    
    suspend fun observePassword() {
        overlayCache.passwordFlow.collect {
            it?.let {
                if (passwordDao.isPasswordCreated() != 1) {
                    overlayCache.statusFlow.tryEmit(ValidationStatus.FAILURE_NO_PASSWORD_SET)
                } else {
                    when (it) {
                        passwordDao.getPassword().truePassword -> {
                            overlayCache.statusFlow.tryEmit(ValidationStatus.SUCCESS_REAL_PASSWORD)
                        }
                        passwordDao.getPassword().falsePassword -> {
                            overlayCache.statusFlow.tryEmit(ValidationStatus.SUCCESS_FAKE_PASSWORD)
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