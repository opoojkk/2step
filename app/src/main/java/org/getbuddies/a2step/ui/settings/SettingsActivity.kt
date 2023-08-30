package org.getbuddies.a2step.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.tencent.mmkv.MMKV
import org.getbuddies.a2step.R
import org.getbuddies.a2step.consts.SyncSettingsMMKVs.KEY_SYNC_METHOD
import org.getbuddies.a2step.consts.SyncSettingsMMKVs.KEY_SYNC_SETTINGS
import org.getbuddies.a2step.consts.SyncSettingsMMKVs.VALUE_SYNC_METHOD_NONE
import org.getbuddies.a2step.consts.SyncSettingsMMKVs.VALUE_SYNC_METHOD_WEBDAV
import org.getbuddies.a2step.databinding.ActivitySettingsBinding
import org.getbuddies.a2step.ui.base.ViewBindingActivity
import org.getbuddies.a2step.ui.extendz.TextViewExtends.setTextViewFocusedError
import org.getbuddies.a2step.ui.extendz.dpToPx
import org.getbuddies.a2step.ui.home.extends.setRoundedOutlineProvider
import org.getbuddies.a2step.ui.settings.viewModel.WebDavViewModel

class SettingsActivity : ViewBindingActivity<ActivitySettingsBinding>() {
    private lateinit var mViewModel: WebDavViewModel
    private val mSyncMethods: Array<String> by lazy { resources.getStringArray(R.array.label_sync_methods) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initViews()
    }

    override fun getViewBinding(): ActivitySettingsBinding {
        return ActivitySettingsBinding.inflate(layoutInflater)
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(this)[WebDavViewModel::class.java]
    }

    override fun initViews() {
        initBackButton()
        initSubmitButton()
        initSyncWayAutoCompleteTextView()
        initSyncMethodViews()
    }

    private fun initSyncWayAutoCompleteTextView() {
        val adapter = ArrayAdapter(this, R.layout.item_settings_sync_way, mSyncMethods)
        mBinding.syncAutoCompleteTextView.setAdapter(adapter)

        when (MMKV.mmkvWithID(KEY_SYNC_SETTINGS)
            .getString(KEY_SYNC_METHOD, VALUE_SYNC_METHOD_NONE)) {
            VALUE_SYNC_METHOD_NONE -> {
                mBinding.syncAutoCompleteTextView.setText(mSyncMethods[0], false)
            }

            VALUE_SYNC_METHOD_WEBDAV -> {
                mBinding.syncAutoCompleteTextView.setText(mSyncMethods[1], false)
            }
        }
        mBinding.syncAutoCompleteTextView.addTextChangedListener {
            initSyncMethodViews(it.toString())
        }
    }

    private fun initSubmitButton() {
        mBinding.submitButton.setRoundedOutlineProvider(18f.dpToPx())
        mBinding.submitButton.setOnClickListener {
            when (mBinding.syncAutoCompleteTextView.text.toString()) {
                mSyncMethods[0] -> {
                    MMKV.mmkvWithID(KEY_SYNC_SETTINGS)
                        .putString(KEY_SYNC_METHOD, VALUE_SYNC_METHOD_NONE)
                }

                mSyncMethods[1] -> {
                    if (trySaveWebDavAccount()) {
                        MMKV.mmkvWithID(KEY_SYNC_SETTINGS)
                            .putString(KEY_SYNC_METHOD, VALUE_SYNC_METHOD_WEBDAV)
                    }
                }
            }
            Toast.makeText(this, R.string.toast_settings_save_success, Toast.LENGTH_SHORT).show()
        }
    }

    private fun trySaveWebDavAccount(): Boolean {
        val account = mBinding.accountEditText.text.toString()
        if (account.isEmpty()) {
            mBinding.accountEditText.setTextViewFocusedError(R.string.error_totp_input_account)
            return false
        }
        val password = mBinding.passwordEditText.text.toString()
        if (password.isEmpty()) {
            mBinding.accountEditText.setTextViewFocusedError(R.string.error_totp_input_secret)
            return false
        }
        mViewModel.saveWebDavAccount(account, password)
        return true
    }

    private fun initBackButton() {
        mBinding.backButton.setRoundedOutlineProvider(20f.dpToPx())
        mBinding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun initSyncMethodViews(method: String = "") {
        val syncMethod = method.ifEmpty {
            MMKV.mmkvWithID(KEY_SYNC_SETTINGS)
                .getString(KEY_SYNC_METHOD, VALUE_SYNC_METHOD_NONE)
        }
        when (syncMethod) {
            VALUE_SYNC_METHOD_NONE -> {
                mBinding.syncNoneNoticeTextView.visibility = View.VISIBLE
                mBinding.layerWebdav.visibility = View.GONE
            }

            VALUE_SYNC_METHOD_WEBDAV -> {
                mBinding.syncNoneNoticeTextView.visibility = View.GONE
                mBinding.layerWebdav.visibility = View.VISIBLE
            }
        }
    }
}