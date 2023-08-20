package org.getbuddies.a2step.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.search.SearchView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.permissionx.guolindev.PermissionX
import org.getbuddies.a2step.R
import org.getbuddies.a2step.databinding.ActivityMainBinding
import org.getbuddies.a2step.databinding.DialogMainSettingsBinding
import org.getbuddies.a2step.db.totp.entity.Totp
import org.getbuddies.a2step.ui.base.ViewBindingActivity
import org.getbuddies.a2step.ui.custom.TactfulDialog
import org.getbuddies.a2step.ui.extendz.dpToPx
import org.getbuddies.a2step.ui.home.adapter.TotpDelegate
import org.getbuddies.a2step.ui.settings.SettingsActivity
import org.getbuddies.a2step.ui.totp.InputManualActivity
import org.getbuddies.a2step.ui.totp.ScanTotpActivity
import org.getbuddies.a2step.ui.utils.ScreenUtil


class MainActivity : ViewBindingActivity<ActivityMainBinding>() {
    private val mTotpViewModel by lazy {
        ViewModelProvider(this)[TotpViewModel::class.java]
    }
    private val adapter: MultiTypeAdapter by lazy { MultiTypeAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    @SuppressLint("RestrictedApi")
    override fun initViews() {
        setSupportActionBar(mBinding.searchBar)
        initRecyclerView()
        initDialView()
        mBinding.searchView.setStatusBarSpacerEnabled(false)
        mBinding.searchView.addTransitionListener { searchView, previousState, newState ->
        }
    }

    private fun initViewModel() {
        mTotpViewModel.totpList.observe(this) {
            updateRecyclerView(it)
        }    }

    private fun initRecyclerView() {
        adapter.run {
            register(
                Totp::class.java, TotpDelegate(
                    object : TotpDelegate.OnLongPressListener {
                        override fun onLongClick() {
                            enterActionMode()
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

    private fun updateRecyclerView(totps: List<Totp>) {
        adapter.items = totps
        adapter.notifyDataSetChanged()
    }

    private fun initDialView() {
        val speedDialView: SpeedDialView = mBinding.speedDial
        speedDialView.findViewById<FloatingActionButton>(com.leinardi.android.speeddial.R.id.sd_main_fab)
            .updateLayoutParams<LinearLayout.LayoutParams> {
                topMargin = 0
                bottomMargin = 0
                leftMargin = 0
                rightMargin = 0
            }
        // 添加子菜单
        speedDialView.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_input_scan, R.drawable.icon_input_keybroad)
                .setLabel(R.string.label_input_manual)
                .create()
        )
        speedDialView.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_input_manual, R.drawable.icon_input_scan)
                .setLabel(R.string.label_input_scan)
                .create()
        )
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

    override fun onResume() {
        super.onResume()
        queryAllTotpsFromTable()
    }

    private fun queryAllTotpsFromTable() {
        mTotpViewModel.refreshTotpList()
    }

    private fun enterActionMode() {
        val actionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                // 通过MenuInflater填充操作栏菜单
                val inflater: MenuInflater = mode.menuInflater
                inflater.inflate(R.menu.example_menu, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false // 返回false，如果没有更改，不需要对ActionMode进行任何更新
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                // 在此处响应操作项点击
                return when (item.itemId) {
                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                // 清除与ActionMode相关的任何状态
            }
        }
        startActionMode(actionModeCallback)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_bar_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                showSettingsDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSettingsDialog() {
        val tactfulDialog = TactfulDialog<DialogMainSettingsBinding>(this)
        tactfulDialog.setContentView(DialogMainSettingsBinding.inflate(layoutInflater))
        tactfulDialog.getViewBinding().settingsSyncText.setOnClickListener {
            this@MainActivity.startActivity(Intent(this, SettingsActivity::class.java))
            tactfulDialog.dismiss()
        }
        tactfulDialog.setCornerRadius(24f.dpToPx())
        tactfulDialog.setWidth((ScreenUtil.getScreenWidth() - 15f.dpToPx() * 2).toInt())
        tactfulDialog.setAnchorView(mBinding.searchBar, offsetY = 15f.dpToPx().toInt())
        tactfulDialog.show()
    }
}