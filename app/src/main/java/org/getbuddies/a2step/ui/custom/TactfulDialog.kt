package org.getbuddies.a2step.ui.custom

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import org.getbuddies.a2step.R

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

    fun setAnchorView(view: View) {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val y = location[1]
        window ?: return
        window!!.attributes?.y = y + view.height
        window!!.setGravity(Gravity.TOP)
    }

    override fun show() {
        super.show()
    }
}