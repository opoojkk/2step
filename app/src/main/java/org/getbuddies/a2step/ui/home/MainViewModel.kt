package org.getbuddies.a2step.ui.home

import androidx.lifecycle.ViewModel
import org.getbuddies.a2step.db.DataBases
import org.getbuddies.a2step.db.totp.TotpDataBase

class MainViewModel : ViewModel() {
    private val totpDataBase: TotpDataBase = DataBases.get(TotpDataBase::class.java)
    val totpListLiveData = totpDataBase.getAll()
}