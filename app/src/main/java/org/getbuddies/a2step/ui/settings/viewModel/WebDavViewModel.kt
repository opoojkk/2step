package org.getbuddies.a2step.ui.settings.viewModel

import androidx.lifecycle.ViewModel
import com.tencent.mmkv.MMKV
import org.getbuddies.a2step.consts.WebDavMMKVs.WEBDAV_KEY
import org.getbuddies.a2step.consts.WebDavMMKVs.WEBDAV_ACCOUNT
import org.getbuddies.a2step.consts.WebDavMMKVs.WEBDAV_ENABLED
import org.getbuddies.a2step.consts.WebDavMMKVs.WEBDAV_PASSWORD

class WebDavViewModel : ViewModel() {
    fun saveWebDavAccount(account: String, password: String) {
        MMKV.mmkvWithID(WEBDAV_KEY).putString(WEBDAV_ACCOUNT, account)
        MMKV.mmkvWithID(WEBDAV_KEY).putString(WEBDAV_PASSWORD, password)
        MMKV.mmkvWithID(WEBDAV_KEY).putBoolean(WEBDAV_ENABLED, true)
    }

    companion object {
        private const val TAG = "WebDavViewModel"
    }
}