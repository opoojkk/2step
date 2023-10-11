package org.getbuddies.a2step.ui.totp

import android.os.Bundle
import android.widget.Toast
import cn.bingoogolapple.qrcode.core.QRCodeView
import org.getbuddies.a2step.R
import org.getbuddies.a2step.databinding.ActivityScanTotpBinding
import org.getbuddies.a2step.extends.startActivity
import org.getbuddies.a2step.totp.Otpauth.tryParseTotp
import org.getbuddies.a2step.ui.base.ViewBindingActivity
import org.getbuddies.a2step.ui.totp.InputManualActivity.Companion.EXTRA_TOTP_KEY


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
                val totp = try {
                    tryParseTotp(result)
                } catch (e: Exception) {
                    Toast.makeText(
                        this@ScanTotpActivity,
                        R.string.toast_totp_parse_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                startActivity<InputManualActivity>() {
                    putExtra(EXTRA_TOTP_KEY, totp)
                }
                this@ScanTotpActivity.finish()
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