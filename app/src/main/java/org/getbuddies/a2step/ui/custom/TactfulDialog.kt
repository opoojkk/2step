package org.getbuddies.a2step.ui.custom

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
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

    override fun show() {
        super.show()
    }
}