package org.getbuddies.a2step.ui.totp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.getbuddies.a2step.R
import org.getbuddies.a2step.databinding.ActivityInputManualBinding

class InputManualActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityInputManualBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityInputManualBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initViews()
    }

    private fun initViews() {

    }
}