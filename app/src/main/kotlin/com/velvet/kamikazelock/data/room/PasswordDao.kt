package com.velvet.kamikazelock.data.room

import androidx.room.*
import com.velvet.kamikazelock.data.infra.Password

@Dao
interface PasswordDao {

    @Transaction
    fun createPassword(password: Password) {
        deletePassword()
        insertPassword(password)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPassword(PasswordEntity: Password)

    @Query("SELECT * FROM password LIMIT 1")
    fun getPassword(): Password

    @Query("SELECT count(*) FROM password")
    fun isPasswordCreated(): Int

    @Query("DELETE FROM password")
    fun deletePassword()
}