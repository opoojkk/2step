package org.getbuddies.a2step.ui.extendz

import android.content.Context
import org.getbuddies.a2step.App

fun Float.dpToPx(): Int {
    val scale: Float = App.get().resources.displayMetrics.density
    return (this * scale + 0.5f).toInt()
}

fun Float.pxToDp(): Int {
    val scale: Float = App.get().resources.displayMetrics.density
    return (this / scale + 0.5f).toInt()
}