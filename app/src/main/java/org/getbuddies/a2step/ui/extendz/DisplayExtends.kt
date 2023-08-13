package org.getbuddies.a2step.ui.extendz

import android.content.Context
import org.getbuddies.a2step.App

fun Float.dpToPx(): Float {
    val scale: Float = App.get().resources.displayMetrics.density
    return this * scale + 0.5f
}

fun Float.pxToDp(): Float {
    val scale: Float = App.get().resources.displayMetrics.density
    return this / scale + 0.5f
}