package org.getbuddies.a2step.extends

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

inline fun <reified T : Activity> Fragment.startActivity(
    noinline putExtras: (Intent.() -> Unit)? = null
) {
    val intent = Intent(requireActivity(), T::class.java)
    if (putExtras != null) {
        putExtras(intent)
    }
    this.startActivity(intent)
}

inline fun <reified T : Activity> Fragment.getActivity(): T {
    val activity = requireActivity()
    if (activity !is T) {
        throw IllegalStateException("Fragment $this cannot get specified type Activity.")
    }
    return activity
}

fun Fragment.getAppCompatActivity(): AppCompatActivity? {
    val activity = activity
    if (activity !is AppCompatActivity) {
        return null
    }
    return activity
}

fun Fragment.requireAppCompatActivity(): AppCompatActivity {
    val activity = activity
    if (activity !is AppCompatActivity) {
        throw IllegalStateException("Fragment $this not attached to a context.")
    }
    return activity
}