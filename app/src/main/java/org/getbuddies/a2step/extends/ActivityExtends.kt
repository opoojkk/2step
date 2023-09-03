package org.getbuddies.a2step.extends

import android.app.Activity
import android.content.Intent

fun Activity.startActivity(clazz: Class<*>, putExtras: (Intent.() -> Unit)? = null) {
    val intent = Intent(this, clazz)
    if (putExtras != null) {
        putExtras(intent)
    }
    this.startActivity(intent)
}