package org.getbuddies.a2step.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.getbuddies.a2step.databinding.ActivityAddTotpBinding
import org.getbuddies.a2step.db.DataBases
import org.getbuddies.a2step.db.totp.TotpDataBase
import org.getbuddies.a2step.db.totp.entity.Totp
import org.getbuddies.a2step.totp.TotpGenerator
import org.getbuddies.a2step.ui.home.MainActivity
import org.getbuddies.a2step.ui.home.TotpViewModel


class AddTotpActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityAddTotpBinding
    private val mZXingView: QRCodeView by lazy { mBinding.zxingview }
    private lateinit var mSecret: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddTotpBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initViews()
    }

    private fun initViews() {
        initZxingView()
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
        mBinding.zxingview.setOnClickListener {
        }
        requestCameraPermission()
    }

    private fun requestCameraPermission() {
        PermissionX.init(this).permissions(Manifest.permission.CAMERA)
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    mZXingView.startSpot()
                } else {
                    Toast.makeText(this, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT).show()
                }
            }
    }
}