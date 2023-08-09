package org.getbuddies.a2step.ui.utils

import androidx.window.layout.WindowMetrics
import androidx.window.layout.WindowMetricsCalculator
import org.getbuddies.a2step.App


object ScreenUtil {
    /**
     * 获取屏幕的宽度（以像素为单位）
     * @param context 应用程序上下文
     * @return 屏幕的宽度
     */
    fun getScreenWidth(): Int {
        val windowMetrics: WindowMetrics =
            WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(App.get())
        return windowMetrics.bounds.width()
    }

    /**
     * 获取屏幕的高度（以像素为单位）
     * @param context 应用程序上下文
     * @return 屏幕的高度
     */
    fun getScreenHeight(): Int {
        val windowMetrics: WindowMetrics =
            WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(App.get())
        return windowMetrics.bounds.height()
    }
}