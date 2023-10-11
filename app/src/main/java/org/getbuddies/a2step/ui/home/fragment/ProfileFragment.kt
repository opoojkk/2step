package org.getbuddies.a2step.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.getbuddies.a2step.databinding.FragmentProfileBinding
import org.getbuddies.a2step.extends.startActivity
import org.getbuddies.a2step.ui.settings.SyncSettingsActivity

class ProfileFragment : Fragment() {
    private lateinit var mBinding: FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentProfileBinding.inflate(inflater, container, false)
        initViews()
        return mBinding.root
    }

    private fun initViews() {
        mBinding.profileCloudSync.setOnClickListener {
            startActivity(SyncSettingsActivity::class.java)
        }
    }
}