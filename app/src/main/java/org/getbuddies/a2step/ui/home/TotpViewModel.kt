package org.getbuddies.a2step.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        viewModelScope.launch {
            val deferredTotpList = withContext(Dispatchers.IO) {
                async { mTotpDao.getAll() }.await()
            }
            updateTotpList(deferredTotpList)
        }
    }

    fun queryTotpList(query: String) {
        viewModelScope.launch {
            val deferredTotpList = withContext(Dispatchers.IO) {
                async { mTotpDao.query(query) }.await()
            }
            updateTotpList(deferredTotpList)
        }
    }

    private fun updateTotpList(newTotpList: List<Totp>?) {
        newTotpList ?: return
        totpList.value ?: let {
            totpList.postValue(newTotpList)
            return
        }
        val oldSize = totpList.value!!.size
        val newSize = newTotpList.size
        // If the size of the list is the same and the content is the same, no update is required
        if (oldSize == newSize && totpList.value!!.containsAll(newTotpList)) {
            return
        }
        totpList.postValue(newTotpList)
    }
}