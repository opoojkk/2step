package org.getbuddies.a2step.ui.settings

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import org.getbuddies.a2step.R
import org.getbuddies.a2step.databinding.ActivitySettingsBinding
import org.getbuddies.a2step.ui.base.ViewBindingActivity
import org.getbuddies.a2step.ui.settings.viewModel.WebDavViewModel

class SettingsActivity : ViewBindingActivity<ActivitySettingsBinding>() {
    private lateinit var mViewModel: WebDavViewModel
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
        initSubmitButton()
        initSyncWayAutoCompleteTextView()
    }

    private fun initSyncWayAutoCompleteTextView() {
        val items = resources.getStringArray(R.array.label_sync_ways)
        val adapter = ArrayAdapter(this, R.layout.item_settings_sync_way, items)
        mBinding.syncAutoCompleteTextView.setAdapter(adapter)
        mBinding.syncAutoCompleteTextView.setText(items[0], false)
    }

    private fun initSubmitButton() {
        mBinding.submitButton.setOnClickListener {
            saveWebDavAccount()
        }
    }

    private fun saveWebDavAccount() {
        val account = mBinding.accountEditText.text.toString()
        if (account.isEmpty()) {
            Toast.makeText(this@SettingsActivity, "账号不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        val password = mBinding.passwordEditText.text.toString()
        if (password.isEmpty()) {
            Toast.makeText(this@SettingsActivity, "密码不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        mViewModel.saveWebDavAccount(account, password)
    }

}