package org.getbuddies.a2step.ui.custom

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import android.view.WindowInsets
import org.getbuddies.a2step.R
import org.getbuddies.a2step.ui.home.extends.getActivityFromView

class TactfulDialog : Dialog {
    constructor(context: Context) : this(context, R.style.TactfulDialog)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)
    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener)

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

    override fun show() {
        super.show()
    }
}