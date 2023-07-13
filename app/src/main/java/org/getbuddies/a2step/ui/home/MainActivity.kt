package org.getbuddies.a2step.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import org.getbuddies.a2step.databinding.ActivityMainBinding
import org.getbuddies.a2step.ui.AddTotpActivity
import org.getbuddies.a2step.ui.home.adapter.TotpDelegate
import org.getbuddies.a2step.db.totp.entity.Totp

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mMainViewModel: MainViewModel
    private val adapter: MultiTypeAdapter by lazy { MultiTypeAdapter() }
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
    }

    private fun initViewModel() {
        mMainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mMainViewModel.totpListLiveData.observe(this) {
            updateRecyclerView(it)
            adapter.notifyDataSetChanged()
        }
    }

    private fun initRecyclerView() {
        adapter.register(Totp::class.java, TotpDelegate())
        adapter.items = mMainViewModel.totpListLiveData.value ?: emptyList()
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
}