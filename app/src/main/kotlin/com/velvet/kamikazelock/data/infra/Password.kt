package com.velvet.kamikazelock.data.infra

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "password")
data class Password(
    @PrimaryKey val id: Long,
    val truePassword: String,
    val falsePassword: String
) {
    companion object {
        const val MIN_PASSWORD_LENGTH = 4
        const val MAX_PASSWORD_LENGTH = 8
    }
}
