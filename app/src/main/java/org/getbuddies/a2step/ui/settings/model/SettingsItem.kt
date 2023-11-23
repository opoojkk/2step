package org.getbuddies.a2step.ui.settings.model

import androidx.annotation.IdRes

data class SettingsItem(
    val title: String,
    val description: String = "",
    val action: SettingsItemAction = SettingsItemAction.DEFAULT
)
