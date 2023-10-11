package org.getbuddies.a2step.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import org.getbuddies.a2step.R
import org.getbuddies.a2step.databinding.ActivityMainBinding
import org.getbuddies.a2step.ui.base.ViewBindingActivity
import org.getbuddies.a2step.ui.home.fragment.HomeFragment
import org.getbuddies.a2step.ui.home.fragment.ProfileFragment
import org.getbuddies.a2step.ui.home.viewModel.TotpEditViewModel
import org.getbuddies.a2step.ui.home.viewModel.TotpViewModel
import org.getbuddies.a2step.ui.utils.StatusBars


class MainActivity : ViewBindingActivity<ActivityMainBinding>() {
    private val mTotpViewModel by lazy {
        ViewModelProvider(this)[TotpViewModel::class.java]
    }
    private val mTotpEditViewModel by lazy {
        ViewModelProvider(this)[TotpEditViewModel::class.java]
    }
    private var actionMode: ActionMode? = null
    private val mFragmentContainerController: FragmentContainerController by lazy {
        FragmentContainerController().apply {
            registerFragment(R.id.home, HomeFragment(), true)
            registerFragment(R.id.profile, ProfileFragment())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLifecycleObserver()
    }

    private fun registerLifecycleObserver() {
        lifecycle.addObserver(mFragmentContainerController)
    }


    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        initBottomNavigationView()
        StatusBars.setNavigationBarColor(window,getColor(R.color.home_action_mode_navigation_bar_color))
        addDefaultFragment()
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
                StatusBars.setStatusBarColor(window, getColor(R.color.home_action_mode_status_bar_color))
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

    fun exitActionMode() {
        actionMode?.finish()
        actionMode = null
    }

    fun isActionMode(): Boolean {
        return actionMode != null
    }

    private fun clearRecyclerViewActionMode() {
        if (mTotpEditViewModel.isEmpty()) {
            return
        }
        mFragmentContainerController.clearRecyclerViewActionMode()
    }

    private fun initBottomNavigationView() {
        mBinding.bottomNavigationView.setOnItemSelectedListener {
            replaceFragmentWithId(it.itemId)
            true
        }
    }

    private fun replaceFragmentWithId(id: Int) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, tryFindFragment(id))
        }
    }

    private fun tryFindFragment(id: Int): Fragment {
        return mFragmentContainerController.tryFindFragment(id)
    }

    private fun addDefaultFragment(id: Int = R.id.home) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fragment_container_view, tryFindFragment(id))
        }
    }

    class FragmentContainerController : DefaultLifecycleObserver {
        private val mFragmentIdHashMap: HashMap<Int, Fragment> = HashMap()
        private var mDefaultFragmentId = 0
        private var mCurrentFragmentId = 0
        override fun onStart(owner: LifecycleOwner) {
            initFragment(owner)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            mFragmentIdHashMap.clear()
        }

        private fun initFragment(owner: LifecycleOwner) {
            if (owner !is MainActivity) {
                return
            }
            mCurrentFragmentId = mDefaultFragmentId
        }

        fun clearRecyclerViewActionMode() {
            if (mCurrentFragmentId == 0) {
                return
            }
            val currentFragment = tryFindFragment(mCurrentFragmentId)
            if (currentFragment !is HomeFragment) {
                return
            }
            currentFragment.clearRecyclerViewActionMode()
        }

        fun tryFindFragment(id: Int): Fragment {
            return mFragmentIdHashMap[id] ?: mFragmentIdHashMap[mDefaultFragmentId]!!
        }

        fun registerFragment(id: Int, fragment: Fragment, default: Boolean = false) {
            if (default) {
                mDefaultFragmentId = id
            }
            mFragmentIdHashMap[id] = fragment
        }

        fun registerFragments(map: HashMap<Int, Fragment>) {
            mFragmentIdHashMap.putAll(map)
        }
    }
}