package org.getbuddies.a2step.extends

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment

fun Fragment.startActivity(clazz: Class<*>, putExtras: (Intent.() -> Unit)? = null) {
    val intent = Intent(requireActivity(), clazz)
    if (putExtras != null) {
        putExtras(intent)
    }
    this.startActivity(intent)
}