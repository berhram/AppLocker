package com.velvet.applocker.infra

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locked_app")
class LockedApp(
    @PrimaryKey
    val packageName: String
)
