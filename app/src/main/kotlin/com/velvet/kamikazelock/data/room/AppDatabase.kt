package com.velvet.kamikazelock.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.velvet.kamikazelock.infra.LockedApp
import com.velvet.kamikazelock.infra.Password

@Database(entities = [LockedApp::class, Password::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): LockedAppsDao

    abstract fun passwordDao() : PasswordDao

    companion object {
        const val DB_NAME = "app-database"
    }
}