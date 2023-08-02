package org.getbuddies.a2step.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import org.getbuddies.a2step.R
import org.getbuddies.a2step.databinding.ActivitySettingsBinding
import org.getbuddies.a2step.ui.settings.viewModel.WebDavViewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySettingsBinding
    private lateinit var mViewModel: WebDavViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initViewModel()
        initViews()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(this)[WebDavViewModel::class.java]
    }

    private fun initViews() {
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