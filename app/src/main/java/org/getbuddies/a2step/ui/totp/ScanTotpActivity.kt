package org.getbuddies.a2step.ui.totp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import cn.bingoogolapple.qrcode.core.QRCodeView
import org.getbuddies.a2step.R
import org.getbuddies.a2step.databinding.ActivityScanTotpBinding
import org.getbuddies.a2step.ui.base.ViewBindingActivity


class ScanTotpActivity : ViewBindingActivity<ActivityScanTotpBinding>() {
    private val mZXingView: QRCodeView by lazy { mBinding.zxingview }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
    }

    override fun getViewBinding(): ActivityScanTotpBinding {
        return ActivityScanTotpBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        initZxingView()
    }

    private fun initZxingView() {
        mBinding.zxingview.setDelegate(object : QRCodeView.Delegate {
            override fun onScanQRCodeSuccess(result: String?) {

                startActivity(Intent(this@ScanTotpActivity, ScanTopVerifyActivity::class.java))
            }

            override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {

            }

            override fun onScanQRCodeOpenCameraError() {
                Toast.makeText(
                    this@ScanTotpActivity,
                    R.string.toast_scan_check_camera,
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun startSpot() {
        mZXingView.startSpot()
    }


    override fun onResume() {
        super.onResume()
        startSpot()
    }

    override fun onPause() {
        super.onPause()
        mZXingView.stopCamera()
    }
}