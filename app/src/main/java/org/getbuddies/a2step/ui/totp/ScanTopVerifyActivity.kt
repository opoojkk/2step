package org.getbuddies.a2step.ui.totp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.getbuddies.a2step.R
import org.getbuddies.a2step.databinding.ActivityScanTopVerifyBinding
import org.getbuddies.a2step.ui.base.ViewBindingActivity

class ScanTopVerifyActivity : ViewBindingActivity<ActivityScanTopVerifyBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
    }

    override fun getViewBinding(): ActivityScanTopVerifyBinding {
        return ActivityScanTopVerifyBinding.inflate(layoutInflater)
    }

    override fun initViews() {
    }
}