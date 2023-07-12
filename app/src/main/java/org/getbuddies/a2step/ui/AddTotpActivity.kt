package org.getbuddies.a2step.ui

import android.Manifest
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.permissionx.guolindev.PermissionX
import org.getbuddies.a2step.databinding.ActivityAddTotpBinding

class AddTotpActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityAddTotpBinding
    private val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> by lazy {
        initBottomSheetBehavior()
    }

    private val mZXingView: QRCodeView by lazy { mBinding.zxingview }

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
        mBinding.zxingview.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
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

    private fun initSecretEditText() {
        mBinding.secretEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun initBottomSheetBehavior(): BottomSheetBehavior<LinearLayout> {
        return BottomSheetBehavior.from(mBinding.bottomSheet).apply {
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            mZXingView.stopCamera()
                            mZXingView.stopSpot()
                        }

                        BottomSheetBehavior.STATE_HIDDEN -> {
                            mZXingView.startCamera()
                            mZXingView.startSpot()
                        }

                        else -> {}
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }
            })
        }
    }
}