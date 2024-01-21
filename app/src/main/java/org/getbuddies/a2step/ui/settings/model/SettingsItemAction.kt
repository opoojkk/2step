package org.getbuddies.a2step.ui.settings.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.getbuddies.a2step.R
import org.getbuddies.a2step.db.DataBases
import org.getbuddies.a2step.db.totp.TotpDataBase
import org.getbuddies.a2step.db.totp.entity.Totp
import org.getbuddies.a2step.extends.getAppCompatActivity
import org.getbuddies.a2step.extends.startActivity
import org.getbuddies.a2step.ui.settings.activity.ImportActivity
import org.getbuddies.a2step.ui.settings.activity.SyncSettingsActivity
import org.getbuddies.a2step.ui.utils.FileExporter

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
            context.startActivity<ImportActivity> { }
        }

    }

    object SettingsMigrateExport : ClickableSettingsItemAction() {
        override fun execute(context: Context) {
            context.getAppCompatActivity()?.lifecycleScope?.launch {
                val list: List<Totp>
                withContext(Dispatchers.IO) {
                    list = DataBases.get(TotpDataBase::class.java).totpDao().getAll() ?: emptyList()
                    if (list.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                context.getText(R.string.toast_totp_is_empty),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        return@withContext
                    }
                    val gson = GsonBuilder().create()
                    val content = gson.toJson(list)
                    FileExporter.exportToFile(
                        context,
                        "${System.currentTimeMillis()}.json",
                        content
                    )
                }
            }

        }

    }

    class SettingAboutGithub(private val url: String) : ClickableSettingsItemAction() {
        override fun execute(context: Context) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }

    }
}
