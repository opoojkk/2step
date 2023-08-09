package org.getbuddies.a2step.ui.home.extends

import android.app.Activity
import android.content.ContextWrapper
import android.view.View

fun View.getActivityFromView(): Activity? {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext;
    }
    return null
}