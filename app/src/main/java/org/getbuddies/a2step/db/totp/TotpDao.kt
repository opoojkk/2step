package org.getbuddies.a2step.db.totp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import org.getbuddies.a2step.db.totp.entity.Totp

@Dao
interface TotpDao {
    @Query("SELECT * FROM totp_db")
    fun getAll(): LiveData<List<Totp>>

    @Insert(entity = Totp::class)
    fun insert(totp: Totp)

    @Insert(entity = Totp::class)
    fun insertAll(vararg totp: Totp)

    @Query("DELETE FROM totp_db WHERE name = :name AND account = :account")
    fun delete(name: String, account: String)

    @Delete
    fun delete(totp: Totp)

    @Delete()
    fun deleteAll(vararg totp: Totp)
}