package com.northious.a2step.ui

import android.Manifest
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.northious.a2step.databinding.ActivityAddTotpBinding
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback

class AddTotpActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityAddTotpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddTotpBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initViews()
    }

    private fun initViews() {
        initZxingView()
        initSecretEditText()
    }

    private fun initZxingView() {
        mBinding.zxingview.setDelegate(object : QRCodeView.Delegate {
            override fun onScanQRCodeSuccess(result: String?) {
                Toast.makeText(this@AddTotpActivity, "result: $result", Toast.LENGTH_SHORT).show()
            }

            override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {

            }

            override fun onScanQRCodeOpenCameraError() {
                Toast.makeText(this@AddTotpActivity, "打开相机出错", Toast.LENGTH_SHORT).show()
            }

        })
        requestCameraPermission()
    }

    private fun requestCameraPermission() {
        PermissionX.init(this).permissions(Manifest.permission.CAMERA)
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    mBinding.zxingview.startSpot()
                } else {
                    Toast.makeText(this, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun initSecretEditText() {
        mBinding.secretEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {

                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }
}