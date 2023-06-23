package com.northious.a2step.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.northious.a2step.databinding.ActivityMainBinding
import com.northious.a2step.totp.TotpGenerator
import com.northious.a2step.ui.home.adapter.TotpDelegate
import com.northious.a2step.ui.home.bean.Totp

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private val adapter: MultiTypeAdapter by lazy { MultiTypeAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initViews()
    }

    private fun initViews() {
        adapter.register(Totp::class.java, TotpDelegate())
        adapter.items = listOf(
            Totp(
                "Google",
                "gotcha1874@gmail.com",
                "TVOFVGUHCDU4HD4GFVRAIFFASDQFUGXKOLSVQEC7TQINV4JFESNUSB7KG7ENH64UQXMP7GQ77Y5KNQ63AJ6PGVHZXI34LR47LQTN6JQ"
            )
        )
        mBinding.totpRecyclerView.adapter = adapter
        mBinding.totpRecyclerView.layoutManager = LinearLayoutManager(this)

        adapter.notifyDataSetChanged()
    }
}