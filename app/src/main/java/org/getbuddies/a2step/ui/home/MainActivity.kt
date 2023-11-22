package org.getbuddies.a2step.ui.home

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.leinardi.android.speeddial.SpeedDialView
import com.permissionx.guolindev.PermissionX
import org.getbuddies.a2step.R
import org.getbuddies.a2step.databinding.ActivityMainBinding
import org.getbuddies.a2step.db.totp.entity.Totp
import org.getbuddies.a2step.extends.startActivity
import org.getbuddies.a2step.ui.base.ViewBindingActivity
import org.getbuddies.a2step.ui.extendz.dpToPx
import org.getbuddies.a2step.ui.home.adapter.TotpDelegate
import org.getbuddies.a2step.ui.home.viewModel.TotpEditViewModel
import org.getbuddies.a2step.ui.home.viewModel.TotpViewModel
import org.getbuddies.a2step.ui.totp.InputManualActivity
import org.getbuddies.a2step.ui.totp.ScanTotpActivity
import org.getbuddies.a2step.ui.utils.StatusBars


class MainActivity : ViewBindingActivity<ActivityMainBinding>() {
    private val mTotpViewModel by lazy {
        ViewModelProvider(this)[TotpViewModel::class.java]
    }
    private val mTotpEditViewModel by lazy {
        ViewModelProvider(this)[TotpEditViewModel::class.java]
    }
    private val adapter: MultiTypeAdapter by lazy { MultiTypeAdapter() }
    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerTotpEditViewModelObserver()
    }


    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        StatusBars.setNavigationBarColor(
            window,
            getColor(R.color.home_action_mode_navigation_bar_color)
        )
        setSupportActionBar(mBinding.materialToolbar)
        initRecyclerView()
        initDialView()
    }

    private fun initRecyclerView() {
        adapter.run {
            register(
                Totp::class.java, TotpDelegate(
                    object : TotpDelegate.EditStateListener {
                        override fun onSelected(totp: Totp) {
                            if (totp == Totp.DEFAULT) {
                                return
                            }
                            mTotpEditViewModel.add(totp)
                        }

                        override fun onUnselected(totp: Totp) {
                            if (totp == Totp.DEFAULT) {
                                return
                            }
                            mTotpEditViewModel.remove(totp)
                        }

                        override fun onModify(totp: Totp) {
                            startActivity<InputManualActivity> {
                                putExtra(InputManualActivity.EXTRA_TOTP_KEY, totp)
                                putExtra(InputManualActivity.EXTRA_EDIT_KEY, true)
                            }
                        }

                        override fun onDelete(totp: Totp) {
                            adapter.notifyItemRemoved(adapter.items.indexOf(totp))
                            mTotpViewModel.remove(totp)
                        }

                    })
            )
        }
        adapter.items = mTotpViewModel.totpList.value ?: emptyList()
        mBinding.totpRecyclerView.adapter = adapter
        mBinding.totpRecyclerView.addItemDecoration(
            MaterialDividerItemDecoration(
                this,
                RecyclerView.VERTICAL
            ).apply {
                this.dividerInsetStart = 15f.dpToPx().toInt()
                dividerInsetEnd = 20f.dpToPx().toInt()
            }
        )
        mBinding.totpRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun initDialView() {
        val speedDialView: SpeedDialView = mBinding.speedDial
        // 添加子菜单
        speedDialView.inflate(R.menu.add_totp_options)
        speedDialView.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_input_scan -> {
                    speedDialView.close()
                    startActivity(Intent(this, InputManualActivity::class.java))
                }

                R.id.fab_input_manual -> {
                    speedDialView.close()
                    checkCameraPermissionAndScan()
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        queryAllTotpsFromTable()
    }

    private fun queryAllTotpsFromTable() {
        mTotpViewModel.refreshTotpList()
    }

    fun enterActionMode() {
        val actionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                val inflater: MenuInflater = mode.menuInflater
                inflater.inflate(R.menu.example_menu, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                StatusBars.setStatusBarColor(
                    window,
                    getColor(R.color.home_action_mode_status_bar_color)
                )
                return true
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                // 在此处响应操作项点击
                return when (item.itemId) {
                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                StatusBars.setStatusBarColor(window, Color.TRANSPARENT)
            }
        }
        actionMode = startActionMode(actionModeCallback)
    }

    fun exitActionMode() {
        actionMode?.finish()
        actionMode = null
    }

    fun isActionMode(): Boolean {
        return actionMode != null
    }

    private fun checkCameraPermissionAndScan() {
        if (PermissionX.isGranted(this, Manifest.permission.CAMERA)) {
            startActivity(Intent(this, ScanTotpActivity::class.java))
            return
        }
        PermissionX.init(this).permissions(Manifest.permission.CAMERA)
            .request { allGranted, _, _ ->
                if (allGranted) {
                    startActivity(Intent(this, ScanTotpActivity::class.java))
                } else {
                    Toast.makeText(
                        this,
                        R.string.toast_camera_permission_denied,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun registerTotpEditViewModelObserver() {
        mTotpEditViewModel.observe(this) {
            // selected list is empty that means exit action mode
            if (it.isEmpty()) {
                exitActionMode()
                return@observe
            }
            // do not exit action mode, just check whether action mode is null
            if (!isActionMode()) {
                enterActionMode()
            }
        }
    }
}