package org.getbuddies.a2step.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.getbuddies.a2step.db.DataBases
import org.getbuddies.a2step.db.totp.TotpDao
import org.getbuddies.a2step.db.totp.TotpDataBase
import org.getbuddies.a2step.db.totp.entity.Totp

class TotpViewModel : ViewModel() {
    private val mTotpDao: TotpDao = DataBases.get(TotpDataBase::class.java).totpDao()
    val totpList = MutableLiveData<List<Totp>>()

    fun insert(totp: Totp) {
        mTotpDao.insert(totp)
    }

    fun refreshTotpList() {
        val newTotpList = mTotpDao.getAll()
        newTotpList ?: return
        totpList.value ?: let {
            totpList.postValue(newTotpList)
            return
        }
        val oldSize = totpList.value!!.size
        val newSize = newTotpList.size
        if (oldSize == newSize) {
            return
        }
        if (totpList.value!!.containsAll(newTotpList)) {
            return
        }
        totpList.postValue(newTotpList)
    }
}