package org.getbuddies.a2step.ui.home

import androidx.lifecycle.ViewModel
import org.getbuddies.a2step.db.DataBases
import org.getbuddies.a2step.db.totp.TotpDao
import org.getbuddies.a2step.db.totp.TotpDataBase
import org.getbuddies.a2step.db.totp.entity.Totp

class TotpViewModel : ViewModel() {
    private val mTotpDao: TotpDao = DataBases.get(TotpDataBase::class.java).totpDao()
    val totpListLiveData = mTotpDao.getAll()

    fun insert(totp: Totp) {
        mTotpDao.insert(totp)
    }
}