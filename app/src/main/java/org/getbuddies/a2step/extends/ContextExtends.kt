package org.getbuddies.a2step.extends

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

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

inline fun <reified T : Activity> Context.startActivity(
    noinline putExtras: (Intent.() -> Unit)? = null
) {
    val intent = Intent(this, T::class.java)
    if (putExtras != null) {
        putExtras(intent)
    }
    this.startActivity(intent)
}