package org.getbuddies.a2step.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import org.getbuddies.a2step.ui.utils.StatusBars

abstract class ViewBindingActivity<T : ViewBinding> : AppCompatActivity() {
    protected lateinit var mBinding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = getViewBinding()
        setContentView(mBinding.root)
        StatusBars.configStatusBar(window)
        initViews()
    }

    abstract fun getViewBinding(): T
    abstract fun initViews()
}