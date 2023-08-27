package org.getbuddies.a2step.ui.totp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
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
import org.getbuddies.a2step.ui.home.viewModel.TotpViewModel
import org.getbuddies.a2step.ui.home.extends.setRoundedOutlineProvider

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
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    mTotpViewModel.insert(Totp(name, account, secret))
                }
                finish()
            }
        }
    }

    private fun setTextViewError(textView: TextView, @StringRes errorRes: Int) {
        textView.requestFocus()
        textView.error = getString(errorRes)
    }

    private fun initBackButton() {
        mBinding.backButton.setRoundedOutlineProvider(20f.dpToPx())
        mBinding.backButton.setOnClickListener {
            finish()
        }
    }
}