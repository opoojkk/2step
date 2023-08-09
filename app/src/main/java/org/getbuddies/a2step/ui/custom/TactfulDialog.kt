package org.getbuddies.a2step.ui.custom

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.icu.text.ListFormatter.Width
import android.view.View
import org.getbuddies.a2step.R
import org.getbuddies.a2step.ui.extendz.dpToPx
import org.getbuddies.a2step.ui.home.extends.setRoundedOutlineProvider
import org.getbuddies.a2step.ui.utils.ScreenUtil

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