package org.getbuddies.a2step.ui.home.viewModel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import org.getbuddies.a2step.db.totp.entity.Totp

class TotpEditViewModel : ViewModel() {
    private val mSelectedTotpList = MutableLiveData<ArrayList<Totp>>()

    fun observe(owner: LifecycleOwner, observer: Observer<ArrayList<Totp>>) {
        mSelectedTotpList.value = ArrayList()
        mSelectedTotpList.observe(owner, observer)
    }

    fun add(totp: Totp) {
        val oldValue = mSelectedTotpList.value!!
        oldValue.add(totp)
        mSelectedTotpList.value = oldValue
    }

    fun remove(totp: Totp) {
        val oldValue = mSelectedTotpList.value!!
        oldValue.remove(totp)
        mSelectedTotpList.value = oldValue
    }

    fun isEmpty(): Boolean {
        return mSelectedTotpList.value?.isEmpty() ?: true
    }

    fun getSelectedTotpList(): ArrayList<Totp> {
        return mSelectedTotpList.value?.let { ArrayList(it) } ?: ArrayList()
    }
}