package org.getbuddies.a2step.ui.custom

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import androidx.viewbinding.ViewBinding
import org.getbuddies.a2step.R
import org.getbuddies.a2step.ui.home.extends.getActivityFromView
import org.getbuddies.a2step.ui.home.extends.setRoundedOutlineProvider

class TactfulDialog<T : ViewBinding> : Dialog {
    private lateinit var mBinding: T
    private lateinit var mContentView: View

    constructor(context: Context) : this(context, R.style.TactfulDialog)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)
    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener)

    @Deprecated(
        "Use setContentView instead",
        ReplaceWith("setContentView(ViewBinding)", "org.getbuddies.a2step.ui.custom.TactfulDialog")
    )
    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
    }

    fun setWidth(width: Int) {
        window?.attributes?.width = width
    }

    fun setAnchorView(view: View, offsetX: Int = 0, offsetY: Int = 0) {
        val activity = view.getActivityFromView() ?: return
        val window = window ?: return

        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val y = location[1]

        val windowInsets = activity.window.decorView.rootWindowInsets

        val attributes = window.attributes ?: return
        attributes.y = y + view.height - windowInsets.systemWindowInsetTop + offsetY
        attributes.x = attributes.x + offsetX
        window.setGravity(Gravity.TOP)
    }

    fun setContentView(binding: T) {
        mBinding = binding
        mContentView = binding.root
        super.setContentView(binding.root)
    }

    @Deprecated(
        "Use setContentView instead",
        ReplaceWith("setContentView(ViewBinding)", "org.getbuddies.a2step.ui.custom.TactfulDialog")
    )
    override fun setContentView(view: View) {
        mContentView = view
        super.setContentView(view)
    }

    fun setCornerRadius(radius: Float) {
        mContentView.setRoundedOutlineProvider(radius)
    }

    fun getViewBinding(): T {
        return mBinding
    }

    override fun show() {
        super.show()
    }
}