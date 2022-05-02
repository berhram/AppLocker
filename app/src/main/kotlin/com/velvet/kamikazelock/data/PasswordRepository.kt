package com.velvet.kamikazelock.data

import com.velvet.kamikazelock.data.cache.overlay.OverlayCacheContract
import com.velvet.kamikazelock.data.infra.Password
import com.velvet.kamikazelock.data.infra.ValidationStatus
import com.velvet.kamikazelock.data.room.PasswordDao

class PasswordRepository(
    private val passwordDao: PasswordDao,
    private val overlayCache: OverlayCacheContract.RepositoryCache
) {
    fun setNewPassword(newTrue: String, newFalse: String) {
        passwordDao.createPassword(
            Password(id = System.currentTimeMillis(),
                truePassword = newTrue, falsePassword = newFalse)
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