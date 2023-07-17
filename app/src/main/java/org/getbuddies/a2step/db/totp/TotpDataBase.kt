package org.getbuddies.a2step.db.totp

import androidx.lifecycle.LiveData
import androidx.room.Database
import androidx.room.RoomDatabase
import org.getbuddies.a2step.db.totp.entity.Totp

@Database(entities = [Totp::class], version = 1)
abstract class TotpDataBase : RoomDatabase() {
    abstract fun totpDao(): TotpDao

    fun getAll(): LiveData<List<Totp>> {
        return totpDao().getAll()
    }

    fun insert(totp: Totp) {
        totpDao().insert(totp)
    }

    fun delete(totp: Totp) {
        totpDao().delete(totp)
    }
}