package org.getbuddies.a2step.extends

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

fun Context.clip(content: String) {
    val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip: ClipData = ClipData.newPlainText(content, content)
    clipboard.setPrimaryClip(clip)
}