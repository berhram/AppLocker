package com.velvet.kamikazelock.data.infra

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "password")
data class Password(
    @PrimaryKey val id: Long,
    val realPassword: String,
    val fakePassword: String
)
