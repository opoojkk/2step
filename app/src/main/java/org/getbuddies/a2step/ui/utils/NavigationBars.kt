package org.getbuddies.a2step.ui.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.lang.ref.WeakReference


object NavigationBars {
    private class NavigationViewInfo(
        val hostRef: WeakReference<View>,
        val viewRef: WeakReference<View>,
        val rawBottom: Int,
        val onNavHeightChangeListener: (View, Int, Int) -> Unit
    )

    private val navigationViewInfoList = mutableListOf<NavigationViewInfo>()

    private val onApplyWindowInsetsListener = View.OnApplyWindowInsetsListener { v, insets ->
        val windowInsetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets, v)
        val navHeight =
            windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
        val it = navigationViewInfoList.iterator()
        while (it.hasNext()) {
            val info = it.next()
            val host = info.hostRef.get()
            val view = info.viewRef.get()
            if (host == null || view == null) {
                it.remove()
                continue
            }

            if (host == v) {
                info.onNavHeightChangeListener(view, info.rawBottom, navHeight)
            }
        }
        insets
    }

    private val actionMarginNavigation: (View, Int, Int) -> Unit =
        { view, rawBottomMargin, navHeight ->
            (view.layoutParams as? ViewGroup.MarginLayoutParams)?.let {
                it.bottomMargin = rawBottomMargin + navHeight
                view.requestLayout()
            }
        }

    private val actionPaddingNavigation: (View, Int, Int) -> Unit =
        { view, rawBottomPadding, navHeight ->
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                rawBottomPadding + navHeight
            )
        }

    fun fixNavBarMargin(vararg views: View) {
        views.forEach {
            fixSingleNavBarMargin(it)
        }
    }

    private fun fixSingleNavBarMargin(view: View) {
        val lp = view.layoutParams as? ViewGroup.MarginLayoutParams ?: return
        val rawBottomMargin = lp.bottomMargin

        val viewForCalculate = getViewForCalculate(view)

        if (viewForCalculate.isAttachedToWindow) {
            val realNavigationBarHeight = getRealNavigationBarHeight(viewForCalculate)
            lp.bottomMargin = rawBottomMargin + realNavigationBarHeight
            view.requestLayout()
        }

        //isAttachedToWindow方法并不能保证此时的WindowInsets是正确的，仍然需要添加监听
        val hostRef = WeakReference(viewForCalculate)
        val viewRef = WeakReference(view)
        val info = NavigationViewInfo(hostRef, viewRef, rawBottomMargin, actionMarginNavigation)
        navigationViewInfoList.add(info)
        viewForCalculate.setOnApplyWindowInsetsListener(onApplyWindowInsetsListener)
    }

    fun paddingByNavBar(view: View) {
        val rawBottomPadding = view.paddingBottom

        val viewForCalculate = getViewForCalculate(view)

        if (viewForCalculate.isAttachedToWindow) {
            val realNavigationBarHeight = getRealNavigationBarHeight(viewForCalculate)
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                rawBottomPadding + realNavigationBarHeight
            )
        }

        //isAttachedToWindow方法并不能保证此时的WindowInsets是正确的，仍然需要添加监听
        val hostRef = WeakReference(viewForCalculate)
        val viewRef = WeakReference(view)
        val info =
            NavigationViewInfo(hostRef, viewRef, rawBottomPadding, actionPaddingNavigation)
        navigationViewInfoList.add(info)
        viewForCalculate.setOnApplyWindowInsetsListener(onApplyWindowInsetsListener)
    }

    /**
     * Dialog下的View在低版本机型中获取到的WindowInsets值有误，
     * 所以尝试去获得Activity的contentView，通过Activity的contentView获取WindowInsets
     */
    @SuppressLint("ContextCast")
    private fun getViewForCalculate(view: View): View {
        return (view.context as? ContextWrapper)?.let {
            return@let (it.baseContext as? Activity)?.findViewById<View>(android.R.id.content)?.rootView
        } ?: view.rootView
    }

    /**
     * 仅当view attach window后生效
     */
    private fun getRealNavigationBarHeight(view: View): Int {
        val insets = ViewCompat.getRootWindowInsets(view)
            ?.getInsets(WindowInsetsCompat.Type.navigationBars())
        return insets?.bottom ?: getNavigationBarHeight(view.context)
    }

    private fun getNavigationBarHeight(context: Context): Int {
        val rid: Int =
            context.resources.getIdentifier("config_showNavigationBar", "bool", "android")
        return if (rid != 0) {
            val resourceId: Int =
                context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

}