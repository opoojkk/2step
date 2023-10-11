package org.getbuddies.a2step.extends

import android.app.Activity
import android.content.Intent

inline fun <reified T : Activity> Activity.startActivity(
    noinline putExtras: (Intent.() -> Unit)? = null
) {
    val intent = Intent(this, T::class.java)
    if (putExtras != null) {
        putExtras(intent)
    }
    this.startActivity(intent)
}