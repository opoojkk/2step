package org.getbuddies.a2step.ui.home.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.search.SearchView
import com.leinardi.android.speeddial.SpeedDialView
import com.permissionx.guolindev.PermissionX
import org.getbuddies.a2step.R
import org.getbuddies.a2step.databinding.FragmentBottomNavHomeBinding
import org.getbuddies.a2step.db.totp.entity.Totp
import org.getbuddies.a2step.extends.startActivity
import org.getbuddies.a2step.ui.extendz.dpToPx
import org.getbuddies.a2step.ui.home.MainActivity
import org.getbuddies.a2step.ui.home.adapter.TotpDelegate
import org.getbuddies.a2step.ui.home.viewModel.TotpEditViewModel
import org.getbuddies.a2step.ui.home.viewModel.TotpViewModel
import org.getbuddies.a2step.ui.totp.InputManualActivity
import org.getbuddies.a2step.ui.totp.ScanTotpActivity
import org.getbuddies.a2step.ui.utils.StatusBars

class HomeFragment : Fragment(R.layout.fragment_bottom_nav_home) {
    private lateinit var mBinding: FragmentBottomNavHomeBinding
    private val mTotpViewModel by lazy {
        ViewModelProvider(activity as AppCompatActivity)[TotpViewModel::class.java]
    }
    private val mTotpEditViewModel by lazy {
        ViewModelProvider(this)[TotpEditViewModel::class.java]
    }
    private val adapter: MultiTypeAdapter by lazy { MultiTypeAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentBottomNavHomeBinding.inflate(inflater, container, false)
        initViews()
        return mBinding.root
    }

    private fun initViews() {
        (activity as AppCompatActivity).setSupportActionBar(mBinding.searchBar)
        initRecyclerView()
        initDialView()
        initSearchView()
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
                            activity!!.startActivity(InputManualActivity::class.java) {
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
                requireContext(),
                RecyclerView.VERTICAL
            ).apply {
                this.dividerInsetStart = 15f.dpToPx().toInt()
                dividerInsetEnd = 20f.dpToPx().toInt()
            }
        )
        mBinding.totpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initDialView() {
        val speedDialView: SpeedDialView = mBinding.speedDial
        // 添加子菜单
        speedDialView.inflate(R.menu.add_totp_options)
        speedDialView.setOnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_input_scan -> {
                    speedDialView.close()
                    startActivity(Intent(requireContext(), InputManualActivity::class.java))
                }

                R.id.fab_input_manual -> {
                    speedDialView.close()
                    checkCameraPermissionAndScan()
                }
            }
            true
        }
    }

    private fun initViewModel() {
        mTotpViewModel.totpList.observe(this) {
            updateRecyclerView(it)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView(totps: List<Totp>) {
        adapter.items = totps
        adapter.notifyDataSetChanged()
    }

    private fun checkCameraPermissionAndScan() {
        if (PermissionX.isGranted(requireContext(), Manifest.permission.CAMERA)) {
            startActivity(Intent(requireContext(), ScanTotpActivity::class.java))
            return
        }
        PermissionX.init(this).permissions(Manifest.permission.CAMERA)
            .request { allGranted, _, _ ->
                if (allGranted) {
                    startActivity(Intent(requireContext(), ScanTotpActivity::class.java))
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.toast_camera_permission_denied,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
//
//    private fun updateSearchViewAnchorPosition() {
//        mBinding.searchBar.post {
//            val mainActivity = activity as MainActivity
//            mainActivity.updateSearchViewAnchorPosition(mBinding.searchBar.y)
//        }
//    }

    @SuppressLint("RestrictedApi")
    private fun initSearchView() {
        mBinding.searchView.setStatusBarSpacerEnabled(false)
        mBinding.searchView.addTransitionListener { _, _, newState ->
            val window = requireActivity().window
            if (newState == SearchView.TransitionState.SHOWING) {
                StatusBars.setStatusBarAndNavigationBarColor(
                    window,
                    requireContext().getColor(R.color.surface_on)
                )
            } else if (newState == SearchView.TransitionState.HIDDEN) {
                StatusBars.setStatusBarAndNavigationBarColor(window, Color.TRANSPARENT)
            }
        }
    }

    fun clearRecyclerViewActionMode() {
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