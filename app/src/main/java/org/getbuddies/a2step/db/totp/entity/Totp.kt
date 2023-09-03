package org.getbuddies.a2step.db.totp.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import org.getbuddies.a2step.db.DataBases.TOTP_DB_NAME

@Entity(tableName = TOTP_DB_NAME, primaryKeys = ["name", "account"])
data class Totp(val name: String, val account: String, val secret: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(account)
        parcel.writeString(secret)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Totp> {
        val DEFAULT = Totp("", "", "")
        override fun createFromParcel(parcel: Parcel): Totp {
            return Totp(parcel)
        }

        override fun newArray(size: Int): Array<Totp?> {
            return arrayOfNulls(size)
        }
    }
}
