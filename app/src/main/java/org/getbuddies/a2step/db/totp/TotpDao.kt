package org.getbuddies.a2step.db.totp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.getbuddies.a2step.db.totp.entity.Totp

@Dao
interface TotpDao {
    @Query("SELECT * FROM totp_db")
    fun getAll(): List<Totp>?

    @Query("SELECT * FROM totp_db")
    fun getAllFlow(): Flow<List<Totp>>

    @Insert(entity = Totp::class)
    fun insert(totp: Totp)

    @Insert(entity = Totp::class)
    fun insertAll(vararg totp: Totp)

    @Query("DELETE FROM totp_db Where name = :name and account = :account")
    fun delete(name: String, account: String)

    @Delete()
    fun deleteAll(vararg totp: Totp)

    @Query("SELECT * FROM totp_db WHERE name LIKE '%' || :name || '%' OR account LIKE '%' || :account || '%'")
    fun query(name: String,account:String): List<Totp>?
}