package org.getbuddies.a2step.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
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
import org.getbuddies.a2step.databinding.DialogMainSettingsBinding
import org.getbuddies.a2step.db.totp.entity.Totp
import org.getbuddies.a2step.ui.base.ViewBindingActivity
import org.getbuddies.a2step.ui.custom.TactfulDialog
import org.getbuddies.a2step.ui.extendz.dpToPx
import org.getbuddies.a2step.ui.home.adapter.TotpDelegate
import org.getbuddies.a2step.ui.home.extends.setRoundedOutlineProvider
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

    override fun initViews() {
        initSearchBar()
        initMenu()
        initRecyclerView()
        initDialView()
        initSearchBarEditText()
    }

    private fun initSearchBar() {
        mBinding.searchBar.setRoundedOutlineProvider(28f.dpToPx().toFloat())
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
            val tactfulDialog = TactfulDialog<DialogMainSettingsBinding>(this)
            tactfulDialog.setContentView(DialogMainSettingsBinding.inflate(layoutInflater))
            tactfulDialog.getViewBinding().settingsSyncText.setOnClickListener {
                this@MainActivity.startActivity(Intent(this, SettingsActivity::class.java))
            }
            tactfulDialog.setCornerRadius(24f.dpToPx().toFloat())
            tactfulDialog.setWidth(ScreenUtil.getScreenWidth() - 15f.dpToPx() * 2)
            tactfulDialog.setAnchorView(mBinding.searchBar, offsetY = 15f.dpToPx())
            tactfulDialog.show()
        }
        mBinding.searchBarClearIcon.setRoundedOutlineProvider(20f.dpToPx().toFloat())
        mBinding.searchBarClearIcon.setOnClickListener {
            mBinding.searchBarEditText.text.clear()
        }
    }

    private fun initSearchBarEditText() {
        mBinding.searchBarEditText.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                mBinding.searchBarClearIcon.visibility = View.GONE
                queryAllTotpsFromTable()
                return@addTextChangedListener
            }
            mBinding.searchBarClearIcon.visibility = View.VISIBLE
            mTotpViewModel.queryTotpList(it.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        queryAllTotpsFromTable()
    }

    private fun queryAllTotpsFromTable() {
        mTotpViewModel.refreshTotpList()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            handleTouchOutsideEditText(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun handleTouchOutsideEditText(ev: MotionEvent) {
        val view = currentFocus
        if (view is EditText) {
            val inRect = Rect()
            mBinding.searchBarClearIcon.getGlobalVisibleRect(inRect)
            if (isTouchEventInsideViewRect(inRect, ev)) {
                // 点击了清除按钮
                return
            }
            val outRect = Rect()
            view.getGlobalVisibleRect(outRect)
            if (isTouchEventOutsideViewRect(outRect, ev)) {
                view.clearFocus()
                hideKeyboard(view)
            }
        }
    }

    private fun isTouchEventOutsideViewRect(outRect: Rect, ev: MotionEvent): Boolean {
        return !outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())
    }

    private fun isTouchEventInsideViewRect(inRect: Rect, ev: MotionEvent): Boolean {
        return inRect.contains(ev.rawX.toInt(), ev.rawY.toInt())
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}