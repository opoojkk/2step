package org.getbuddies.a2step.ui.utils

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager

object StatusBars {
    fun configStatusBar(window: Window, root: View) {
        transparentStatusBar(window)
        fixStatusBarMargin(root)
    }

    fun transparentStatusBar(window: Window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility =
            systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.decorView.systemUiVisibility = systemUiVisibility
        window.statusBarColor = Color.TRANSPARENT

        //设置状态栏文字颜色
        setStatusBarTextColor(window, isNightMode(window.context))
    }

    private fun isNightMode(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun setStatusBarTextColor(window: Window, light: Boolean) {
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility = if (light) { //白色文字
            systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        } else { //黑色文字
            systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.decorView.systemUiVisibility = systemUiVisibility
    }

    private fun getStatusBarHeight(context: Context): Int {
        val resId = context.resources.getIdentifier(
            "status_bar_height", "dimen", "android"
        )
        return context.resources.getDimensionPixelSize(resId)
    }

    fun fixStatusBarMargin(vararg views: View) {
        views.forEach { view ->
            (view.layoutParams as? ViewGroup.MarginLayoutParams)?.let { lp ->
                lp.topMargin = lp.topMargin + getStatusBarHeight(view.context)
                view.requestLayout()
            }
        }
    }

    fun paddingByStatusBar(view: View) {
        view.setPadding(
            view.paddingLeft,
            view.paddingTop + getStatusBarHeight(view.context),
            view.paddingRight,
            view.paddingBottom
        )
    }
}