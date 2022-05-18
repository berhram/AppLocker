package com.velvet.applocker.infra

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "password")
data class Password(
    @PrimaryKey val password: String
) {
    companion object {
        const val MIN_PASSWORD_LENGTH = 4
        const val MAX_PASSWORD_LENGTH = 8
    }
}
