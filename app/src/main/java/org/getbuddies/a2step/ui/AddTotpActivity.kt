package org.getbuddies.a2step.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

class AddTotpActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityAddTotpBinding
    private val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> by lazy {
        initBottomSheetBehavior()
    }

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
        initSecretEditText()
        initAddButton()
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
                mSecret = v.text.toString()
                if (mSecret.isEmpty()) {
                    Toast.makeText(this, "请输入密钥", Toast.LENGTH_SHORT).show()
                    return@setOnEditorActionListener true
                }
                try {
                    TotpGenerator.generateNow(mSecret)
                } catch (e: Exception) {
                    Toast.makeText(this, "密钥格式错误", Toast.LENGTH_SHORT).show()
                    return@setOnEditorActionListener true
                }
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun initAddButton() {
        mBinding.submitButton.setOnClickListener {
            val accountName = mBinding.totpNameEditText.text.toString()
            if (accountName.isEmpty()) {
                Toast.makeText(this, "请输入账号名称", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val account = mBinding.totpAccountEditText.text.toString()
            if (account.isEmpty()) {
                Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                launch(Dispatchers.IO) {
                    DataBases.get(TotpDataBase::class.java).add(Totp(accountName, account, mSecret))
                }
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                startActivity(Intent(this@AddTotpActivity, MainActivity::class.java))
            }
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