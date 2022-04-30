package com.velvet.kamikazelock.data.room

import androidx.room.*
import com.velvet.kamikazelock.data.infra.LockedApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface LockedAppsDao {

    @Query("SELECT * FROM locked_app")
    fun downloadLockedApps() : List<LockedApp>

    @Query("SELECT * FROM locked_app")
    fun getLockedApps(): Flow<List<LockedApp>>

    fun getLockedAppsDistinctUntilChanged() = getLockedApps().distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun lockApp(app: LockedApp)

    @Delete
    fun unlockApp(app: LockedApp)
}