package org.getbuddies.a2step.ui.extendz

import android.widget.TextView
import androidx.annotation.StringRes

object TextViewExtends {
    fun TextView.setTextViewFocusedError(@StringRes errorRes: Int) {
        this.requestFocus()
        this.error = this.context.getString(errorRes)
    }

    fun TextView.setTextRes(@StringRes resId: Int) {
        this.text = this.context.resources.getString(resId)
    }
}