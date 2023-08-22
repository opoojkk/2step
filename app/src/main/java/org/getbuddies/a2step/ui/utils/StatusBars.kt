package org.getbuddies.a2step.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.get
import androidx.core.view.updateLayoutParams
import org.getbuddies.a2step.R
import org.getbuddies.a2step.ui.extendz.pxToDp

object StatusBars {
    fun configStatusBar(window: Window) {
        transparentStatusBar(window)
        window.findViewById<View>(android.R.id.content)
            .setOnApplyWindowInsetsListener { v, insets ->
                val statusBarHeight = insets.systemWindowInsetTop
                v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin = statusBarHeight
                }
                insets
            }
    }

    private fun transparentStatusBar(window: Window) {
        /**
         * for [Window.setStatusBarColor] to take effect, the window must set the following flag:
         *  [WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS]
         *  and clear the following flag: [WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS]
         */
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // begin at api 21, we can set status bar color
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility =
            systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.decorView.systemUiVisibility = systemUiVisibility

        setStatusBarAndNavigationBarColor(window)

        //设置状态栏文字颜色
        setStatusBarTextColor(window)
    }

    fun setStatusBarColor(window: Window, color: Int = Color.TRANSPARENT) {
        window.statusBarColor = color
    }

    fun setNavigationBarColor(window: Window, color: Int = Color.TRANSPARENT) {
        window.navigationBarColor = color
    }

    fun setStatusBarAndNavigationBarColor(window: Window, color: Int = Color.TRANSPARENT) {
        setStatusBarColor(window, color)
        setNavigationBarColor(window, color)
    }

    private fun isNightMode(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun setStatusBarTextColor(window: Window) {
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility = if (isNightMode(window.context)) {
            // 白色文字
            systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        } else {
            // 黑色文字
            systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.decorView.systemUiVisibility = systemUiVisibility
    }

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    private fun getStatusBarHeight(context: Context): Int {
        val resId = context.resources.getIdentifier(
            "status_bar_height", "dimen", "android"
        )
        return context.resources.getDimensionPixelSize(resId)
    }

    private fun fixStatusBarMargin(vararg views: View) {
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