package org.getbuddies.a2step.extends

import android.content.Context
import android.widget.Toast
import androidx.annotation.IntRange
import org.getbuddies.a2step.App

fun String.toast(
    context: Context? = null,
    duration: Int = Toast.LENGTH_SHORT
) {
    val realContext = context ?: App.getAppContext()
    Toast.makeText(realContext, "$this", duration).show()
}