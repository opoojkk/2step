package org.getbuddies.a2step.ui.settings.model

import android.content.Context
import android.widget.Toast
import org.getbuddies.a2step.extends.getAppCompatActivity
import org.getbuddies.a2step.extends.startActivity
import org.getbuddies.a2step.ui.settings.SyncSettingsActivity

sealed class SettingsItemAction {
    open fun clickable(): Boolean = false
    abstract fun execute(context: Context)

    object DEFAULT : SettingsItemAction() {
        override fun execute(context: Context) {
            // do nothing
        }

    }

    abstract class ClickableSettingsItemAction : SettingsItemAction() {
        override fun clickable(): Boolean {
            return true
        }
    }

    object SettingsSync : ClickableSettingsItemAction() {
        override fun execute(context: Context) {
            context.getAppCompatActivity()!!.startActivity<SyncSettingsActivity> { }
        }

    }

    object SettingsMigrateImport : ClickableSettingsItemAction() {
        override fun execute(context: Context) {
            Toast.makeText(context, "not implement", Toast.LENGTH_SHORT).show()
        }

    }

    object SettingsMigrateExport : ClickableSettingsItemAction() {
        override fun execute(context: Context) {
            Toast.makeText(context, "not implement", Toast.LENGTH_SHORT).show()
        }

    }

    object SettingAboutGithub : ClickableSettingsItemAction() {
        override fun execute(context: Context) {
            Toast.makeText(context, "not implement", Toast.LENGTH_SHORT).show()
        }

    }
}
