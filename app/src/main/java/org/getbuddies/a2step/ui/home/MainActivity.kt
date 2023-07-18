package org.getbuddies.a2step.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import org.getbuddies.a2step.R
import org.getbuddies.a2step.databinding.ActivityMainBinding
import org.getbuddies.a2step.db.totp.entity.Totp
import org.getbuddies.a2step.ui.AddTotpActivity
import org.getbuddies.a2step.ui.home.adapter.TotpDelegate

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mTotpViewModel: TotpViewModel
    private val adapter: MultiTypeAdapter by lazy { MultiTypeAdapter() }
    private val speedDialView: SpeedDialView by lazy { mBinding.speedDial }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initViewModel()
        initViews()
    }

    private fun initViews() {
        initRecyclerView()
        initFab()
        initDialView()
    }

    private fun initViewModel() {
        mTotpViewModel = ViewModelProvider(this)[TotpViewModel::class.java]
        mTotpViewModel.totpListLiveData.observe(this) {
            updateRecyclerView(it)
            adapter.notifyDataSetChanged()
        }
    }

    private fun initRecyclerView() {
        adapter.register(Totp::class.java, TotpDelegate())
        adapter.items = mTotpViewModel.totpListLiveData.value ?: emptyList()
        mBinding.totpRecyclerView.adapter = adapter
        mBinding.totpRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun updateRecyclerView(totps: List<Totp>) {
        adapter.items = totps
    }

    private fun initFab() {
        mBinding.addTotpFab.setOnClickListener {
            startActivity(Intent(this, AddTotpActivity::class.java))
        }
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
                    startActivity(Intent(this, AddTotpActivity::class.java))
                }

                R.id.fab_input_manual -> {
                    startActivity(Intent(this, AddTotpActivity::class.java))
                }
            }
            false
        }
    }
}