package org.getbuddies.a2step.db.totp.entity

import androidx.room.Entity
import org.getbuddies.a2step.db.DataBases.TOTP_DB_NAME

@Entity(tableName = TOTP_DB_NAME, primaryKeys = ["account"])
data class Totp(val account: String, val secret: String)
