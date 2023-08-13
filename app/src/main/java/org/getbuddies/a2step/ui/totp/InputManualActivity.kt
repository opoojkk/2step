package org.getbuddies.a2step.ui.totp

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.getbuddies.a2step.databinding.ActivityInputManualBinding
import org.getbuddies.a2step.db.totp.entity.Totp
import org.getbuddies.a2step.totp.TotpGenerator
import org.getbuddies.a2step.ui.base.ViewBindingActivity
import org.getbuddies.a2step.ui.home.TotpViewModel

class InputManualActivity : ViewBindingActivity<ActivityInputManualBinding>() {
    private val mTotpViewModel by lazy { ViewModelProvider(this)[TotpViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
    }

    override fun getViewBinding(): ActivityInputManualBinding {
        return ActivityInputManualBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        initBackButton()
        initSubmitButton()
    }

    private fun initSubmitButton() {
        mBinding.submitButton.setOnClickListener {
            val name = mBinding.nameInputEdit.text.toString()
            if (name.isEmpty()) {
                Toast.makeText(
                    this@InputManualActivity,
                    "请输入两步验证账号名称",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val account = mBinding.accountInputEdit.text.toString()
            if (account.isEmpty()) {
                Toast.makeText(
                    this@InputManualActivity,
                    "请输入两步验证账号",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val secret = mBinding.secretInputEdit.text.toString()
            if (secret.isEmpty()) {
                Toast.makeText(this@InputManualActivity, "请输入两步验证密钥", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            try {
                TotpGenerator.generateNow(secret)
            } catch (e: Exception) {
                Toast.makeText(
                    this@InputManualActivity,
                    "请检查输入的两步验证秘钥",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    mTotpViewModel.insert(Totp(name, account, secret))
                }
                finish()
            }
        }
    }

    private fun initBackButton() {
        mBinding.backButton.setOnClickListener {
            finish()
        }
    }
}