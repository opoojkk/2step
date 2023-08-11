package org.getbuddies.a2step.ui.totp

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.permissionx.guolindev.PermissionX
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
                Toast.makeText(this@ScanTotpActivity, "打开相机出错", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun checkPermissionAndSpot() {
        if (PermissionX.isGranted(this, Manifest.permission.CAMERA)) {
            mZXingView.startSpot()
            return
        }
        PermissionX.init(this).permissions(Manifest.permission.CAMERA)
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    mZXingView.startSpot()
                } else {
                    Toast.makeText(this, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT).show()
                }
            }
    }


    override fun onResume() {
        super.onResume()
        checkPermissionAndSpot()
    }

    override fun onPause() {
        super.onPause()
        mZXingView.stopCamera()
    }
}