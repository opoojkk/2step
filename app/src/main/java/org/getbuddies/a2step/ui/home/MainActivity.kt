package org.getbuddies.a2step.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import org.getbuddies.a2step.R
import org.getbuddies.a2step.databinding.ActivityMainBinding
import org.getbuddies.a2step.db.totp.entity.Totp
import org.getbuddies.a2step.ui.extendz.dpToPx
import org.getbuddies.a2step.ui.home.adapter.TotpDelegate
import org.getbuddies.a2step.ui.home.extends.setRoundedOutlineProvider
import org.getbuddies.a2step.ui.settings.SettingsActivity
import org.getbuddies.a2step.ui.totp.InputManualActivity
import org.getbuddies.a2step.ui.totp.ScanTotpActivity
import org.getbuddies.a2step.ui.utils.NavigationBars.fixNavBarMargin
import org.getbuddies.a2step.ui.utils.StatusBars.configStatusBar

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private val mTotpViewModel by lazy {
        ViewModelProvider(this)[TotpViewModel::class.java]
    }
    private val adapter: MultiTypeAdapter by lazy { MultiTypeAdapter() }
    private val speedDialView: SpeedDialView by lazy { mBinding.speedDial }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initViewModel()
        configStatusBar(window, mBinding.root)
        fixNavBarMargin(mBinding.root)
        initViews()
    }

    private fun initViews() {
        initSearchBar()
        initRecyclerView()
        initDialView()
        initMenu()
    }

    private fun initSearchBar() {
        mBinding.searchBar.setRoundedOutlineProvider(28f.dpToPx().toFloat())
        initMenu()
    }

    private fun initViewModel() {
        mTotpViewModel.totpList.observe(this) {
            updateRecyclerView(it)
        }
    }

    private fun initRecyclerView() {
        adapter.register(Totp::class.java, TotpDelegate())
        adapter.items = mTotpViewModel.totpList.value ?: emptyList()
        mBinding.totpRecyclerView.adapter = adapter
        mBinding.totpRecyclerView.addItemDecoration(
            MaterialDividerItemDecoration(
                this,
                RecyclerView.VERTICAL
            ).apply {
                this.dividerInsetStart = 15f.dpToPx()
                dividerInsetEnd = 20f.dpToPx()
            }
        )
        mBinding.totpRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun updateRecyclerView(totps: List<Totp>) {
        adapter.items = totps
        adapter.notifyDataSetChanged()
    }

    private fun initDialView() {
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
                    startActivity(Intent(this, InputManualActivity::class.java))
                }

                R.id.fab_input_manual -> {
                    startActivity(Intent(this, ScanTotpActivity::class.java))
                }
            }
            false
        }
    }

    private fun initMenu() {
        mBinding.searchBarMenuIcon.setRoundedOutlineProvider(20f.dpToPx().toFloat())
        mBinding.searchBarMenuIcon.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshTotps()
    }

    private fun refreshTotps() {
        mTotpViewModel.refreshTotpList()
    }
}