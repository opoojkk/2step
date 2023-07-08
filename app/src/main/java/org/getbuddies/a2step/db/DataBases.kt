package org.getbuddies.a2step.db

import androidx.room.Room
import androidx.room.RoomDatabase
import org.getbuddies.a2step.App
import org.getbuddies.a2step.db.totp.TotpDataBase

object DataBases {
    private const val DB_NAME_SUFFIX = "_db"

    const val TOTP_DB_NAME = "totp$DB_NAME_SUFFIX"

    private val db2NameMaps = hashMapOf<Class<out RoomDatabase>, String>()

    init {
        // TODO: 不需要反射，Transformer更好
        db2NameMaps[TotpDataBase::class.java] = TOTP_DB_NAME
    }

    fun <T : RoomDatabase> get(clazz: Class<T>): T {
        val dbName = db2NameMaps[clazz]
        dbName ?: throw IllegalArgumentException("No database name found for $clazz")

        return Room.databaseBuilder(
            App.get(),
            clazz,
            db2NameMaps[clazz]
        ).build()
    }
}