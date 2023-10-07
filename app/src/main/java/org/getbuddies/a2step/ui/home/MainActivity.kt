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
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.search.SearchView
import com.leinardi.android.speeddial.SpeedDialView
import com.permissionx.guolindev.PermissionX
import org.getbuddies.a2step.R
import org.getbuddies.a2step.databinding.ActivityMainBinding
import org.getbuddies.a2step.databinding.DialogMainSettingsBinding
import org.getbuddies.a2step.db.totp.entity.Totp
import org.getbuddies.a2step.extends.startActivity
import org.getbuddies.a2step.ui.base.ViewBindingActivity
import org.getbuddies.a2step.ui.custom.TactfulDialog
import org.getbuddies.a2step.ui.extendz.dpToPx
import org.getbuddies.a2step.ui.home.adapter.TotpDelegate
import org.getbuddies.a2step.ui.home.viewModel.TotpEditViewModel
import org.getbuddies.a2step.ui.home.viewModel.TotpViewModel
import org.getbuddies.a2step.ui.settings.SettingsActivity
import org.getbuddies.a2step.ui.totp.InputManualActivity
import org.getbuddies.a2step.ui.totp.ScanTotpActivity
import org.getbuddies.a2step.ui.utils.ScreenUtil
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
        initViewModel()
        mTotpEditViewModel.observe(this) {
            // selected list is empty that means exit action mode
            if (it.isEmpty()) {
                exitActionMode()
                return@observe
            }
            // do not exit action mode, just check whether action mode is null
            if (actionMode == null) {
                enterActionMode()
            }
        }
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        setSupportActionBar(mBinding.searchBar)
        initRecyclerView()
        initDialView()
        initSearchView()
    }

    private fun initViewModel() {
        mTotpViewModel.totpList.observe(this) {
            updateRecyclerView(it)
        }
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
                            startActivity(InputManualActivity::class.java) {
                                putExtra(InputManualActivity.EXTRA_TOTP_KEY, totp)
                            }
                        }

                        override fun onDelete(totp: Totp) {
                            mTotpViewModel.remove(totp)
                            adapter.notifyItemRemoved(adapter.items.indexOf(totp))
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

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView(totps: List<Totp>) {
        adapter.items = totps
        adapter.notifyDataSetChanged()
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
                val inflater: MenuInflater = mode.menuInflater
                inflater.inflate(R.menu.example_menu, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                StatusBars.setStatusBarColor(window, getColor(R.color.action_bar_color))
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
                clearRecyclerViewActionMode()
            }
        }
        actionMode = startActionMode(actionModeCallback)
    }

    private fun exitActionMode() {
        actionMode?.finish()
        actionMode = null
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

    @SuppressLint("RestrictedApi")
    private fun initSearchView() {
        mBinding.searchView.setStatusBarSpacerEnabled(false)
        mBinding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.SHOWING) {
                StatusBars.setStatusBarAndNavigationBarColor(window, getColor(R.color.surface_on))
            } else if (newState == SearchView.TransitionState.HIDDEN) {
                StatusBars.setStatusBarAndNavigationBarColor(window, Color.TRANSPARENT)
            }
        }
    }

    private fun clearRecyclerViewActionMode() {
        if (mTotpEditViewModel.isEmpty()) {
            return
        }
        val linearLayoutManager = mBinding.totpRecyclerView.layoutManager as LinearLayoutManager
        for (i in linearLayoutManager.findFirstVisibleItemPosition()..linearLayoutManager.findLastVisibleItemPosition()) {
            val view = linearLayoutManager.findViewByPosition(i)
            // 这里操作view
            view ?: continue
            val holder =
                mBinding.totpRecyclerView.getChildViewHolder(view) as TotpDelegate.ViewHolder
            val totp = holder.getTotp()
            totp ?: continue
            if (mTotpEditViewModel.getSelectedTotpList().contains(totp)) {
                mTotpEditViewModel.remove(totp)
                holder.reset()
            }

        }
    }
}