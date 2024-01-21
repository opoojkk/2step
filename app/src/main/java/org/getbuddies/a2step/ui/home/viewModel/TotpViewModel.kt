package org.getbuddies.a2step.ui.home.viewModel

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
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
    val totpList = MutableLiveData<ArrayList<Totp>>()

    fun insertOrReplace(totp: Totp, old: Totp = Totp.DEFAULT) {
        if (old == Totp.DEFAULT) {
            mTotpDao.insert(totp)
            return
        }
        if (old == totp) {
            return
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mTotpDao.delete(old.name, old.account)
                mTotpDao.insert(totp)
            }
        }
    }

    fun remove(totp: Totp) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mTotpDao.delete(totp.name, totp.account)
            }
        }
        totpList.value?.remove(totp)
    }

    fun observerDBTotp(lifecycleOwner: LifecycleOwner) {
        mTotpDao.getAllFlow().asLiveData().observe(lifecycleOwner) {
            updateTotpList(it)
        }
    }

    private fun updateTotpList(newTotpList: List<Totp>?) {
        newTotpList ?: return
        totpList.value ?: let {
            totpList.postValue(ArrayList(newTotpList))
            return
        }
        val oldSize = totpList.value!!.size
        val newSize = newTotpList.size
        // If the size of the list is the same and the content is the same, no update is required
        if (oldSize == newSize && totpList.value!!.containsAll(newTotpList)) {
            return
        }
        totpList.postValue(ArrayList(newTotpList))
    }
}