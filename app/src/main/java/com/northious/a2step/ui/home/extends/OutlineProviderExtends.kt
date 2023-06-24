package com.northious.a2step.ui.home.extends

import android.view.View
import android.view.ViewOutlineProvider

fun View.setRoundedOutlineProvider(radius: Float) {
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: android.graphics.Outline?) {
            outline?.setRoundRect(0, 0, view!!.width, view.height, radius)
            clipToOutline = true
        }
    }
}