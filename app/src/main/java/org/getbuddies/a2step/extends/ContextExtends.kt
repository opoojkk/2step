package org.getbuddies.a2step.extends

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity

object ContextExtends {
    fun Context.getAppCompatActivity(): AppCompatActivity? {
        return if (this is ContextWrapper) {
            if (this is AppCompatActivity) {
                this
            } else {
                this.baseContext.getAppCompatActivity()
            }
        } else {
            null

        }
    }

}