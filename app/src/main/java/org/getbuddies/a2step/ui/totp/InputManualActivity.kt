package org.getbuddies.a2step.ui.totp

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
import org.getbuddies.a2step.ui.base.ViewBindingActivity
import org.getbuddies.a2step.ui.extendz.TextViewExtends.setTextViewFocusedError
import org.getbuddies.a2step.ui.extendz.dpToPx
import org.getbuddies.a2step.ui.home.extends.setRoundedOutlineProvider
import org.getbuddies.a2step.ui.home.viewModel.TotpViewModel

class InputManualActivity : ViewBindingActivity<ActivityInputManualBinding>() {
    private val mTotpViewModel by lazy { ViewModelProvider(this)[TotpViewModel::class.java] }

    override fun getViewBinding(): ActivityInputManualBinding {
        return ActivityInputManualBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        initBackButton()
        initSubmitButton()
        initEditTexts()
    }

    private fun initSubmitButton() {
        mBinding.submitButton.setRoundedOutlineProvider(18f.dpToPx())
        mBinding.submitButton.setOnClickListener {
            val name = mBinding.nameInputEdit.text.toString()
            if (name.isEmpty()) {
                mBinding.nameInputEdit.setTextViewFocusedError(R.string.error_totp_input_name)
                return@setOnClickListener
            }
            val account = mBinding.accountInputEdit.text.toString()
            if (account.isEmpty()) {
                mBinding.accountInputEdit.setTextViewFocusedError(R.string.error_totp_input_account)
                return@setOnClickListener
            }
            val secret = mBinding.secretInputEdit.text.toString()
            if (secret.isEmpty()) {
                mBinding.secretInputEdit.setTextViewFocusedError(R.string.error_totp_input_secret)
                return@setOnClickListener
            }
            try {
                TotpGenerator.generateNow(secret)
            } catch (e: Exception) {
                Toast.makeText(
                    this@InputManualActivity,
                    R.string.toast_totp_check_secret,
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val digits = mBinding.digitsInputEdit.text.toString().toInt()
            if (secret.isEmpty()) {
                mBinding.secretInputEdit.setTextViewFocusedError(R.string.error_totp_check_digits)
                return@setOnClickListener
            }
            val period = mBinding.periodInputEdit.text.toString().toInt()
            if (secret.isEmpty()) {
                mBinding.secretInputEdit.setTextViewFocusedError(R.string.error_totp_check_period)
                return@setOnClickListener
            }
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val old = intent.extras?.getParcelable<Totp>(EXTRA_TOTP_KEY) ?: Totp.DEFAULT
                    mTotpViewModel.insertOrReplace(Totp(name, account, secret, digits, period), old)
                }
                finish()
            }
        }
    }

    private fun initBackButton() {
        mBinding.backButton.setRoundedOutlineProvider(20f.dpToPx())
        mBinding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun initEditTexts() {
        val extras = intent.extras ?: return
        val totp = extras.getParcelable<Totp>(EXTRA_TOTP_KEY) ?: return
        mBinding.nameInputEdit.setText(totp.name)
        mBinding.accountInputEdit.setText(totp.account)
        mBinding.secretInputEdit.setText(totp.secret)
    }

    companion object {
        const val EXTRA_TOTP_KEY = "extra_totp_key"
    }
}