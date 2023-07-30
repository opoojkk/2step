package org.getbuddies.a2step.ui.totp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.getbuddies.a2step.R
import org.getbuddies.a2step.databinding.ActivityInputManualBinding
import org.getbuddies.a2step.db.totp.entity.Totp
import org.getbuddies.a2step.totp.TotpGenerator
import org.getbuddies.a2step.ui.home.TotpViewModel
import java.lang.Exception

class InputManualActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityInputManualBinding
    private val mTotpViewModel by lazy { ViewModelProvider(this)[TotpViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityInputManualBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initViews()
    }

    private fun initViews() {
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
}