package org.getbuddies.a2step.ui.settings.activity

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import org.getbuddies.a2step.R
import org.getbuddies.a2step.databinding.ActivitySettingsBinding
import org.getbuddies.a2step.extends.ListExtends.append
import org.getbuddies.a2step.ui.base.ViewBindingActivity
import org.getbuddies.a2step.ui.extendz.TextViewExtends.setTextRes
import org.getbuddies.a2step.ui.settings.delegate.SettingsItemDelegate
import org.getbuddies.a2step.ui.settings.delegate.SettingsTitleDelegate
import org.getbuddies.a2step.ui.settings.model.SettingsItem
import org.getbuddies.a2step.ui.settings.model.SettingsItemAction

class SettingsActivity : ViewBindingActivity<ActivitySettingsBinding>() {
    private val adapter: MultiTypeAdapter by lazy { MultiTypeAdapter() }
    override fun getViewBinding(): ActivitySettingsBinding {
        return ActivitySettingsBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        initToolbar()
        initRecyclerView()
    }

    private fun initToolbar() {
        mBinding.backButton.setOnClickListener { finish() }
        mBinding.submitButton.visibility = View.GONE
        mBinding.title.setTextRes(R.string.label_toolbar_settings)
    }

    private fun initRecyclerView() {
        adapter.run {
            register(String::class.java, SettingsTitleDelegate())
            register(SettingsItem::class.java, SettingsItemDelegate())
        }
        mBinding.settingsRecyclerview.run {
            this.adapter = this@SettingsActivity.adapter.apply { items = generateSettingsList() }
            layoutManager =
                LinearLayoutManager(this@SettingsActivity, LinearLayoutManager.VERTICAL, false)
        }

    }

    private fun generateSettingsList(): List<Any> {
        return ArrayList<Any>()
            .append(R.string.label_settings_header_prefs.inflateString())
            .append(
                SettingsItem(
                    R.string.label_settings_sync.inflateString(),
                    R.string.label_settings_sync.inflateString(),
                    SettingsItemAction.SettingsSync
                )
            ).append(R.string.label_profile_migrate.inflateString())
            .append(
                SettingsItem(
                    R.string.label_settings_import.inflateString(),
                    R.string.description_settings_import.inflateString(),
                    SettingsItemAction.SettingsMigrateImport
                )
            )
            .append(
                SettingsItem(
                    R.string.label_settings_export.inflateString(),
                    R.string.description_settings_export.inflateString(),
                    SettingsItemAction.SettingsMigrateExport
                )
            )
            .append(R.string.label_settings_about.inflateString())
            .append(
                SettingsItem(
                    R.string.label_settings_github.inflateString(),
                    R.string.description_settings_github.inflateString(),
                    SettingsItemAction.SettingAboutGithub("https://github.com/opoojkk")
                )
            )
            .append(
                SettingsItem(
                    R.string.label_settings_repo.inflateString(),
                    R.string.description_settings_repo.inflateString(),
                    SettingsItemAction.SettingAboutGithub(R.string.description_settings_repo.inflateString())
                )
            )
            .append(R.string.label_settings_libraries.inflateString())
            .append(
                SettingsItem(
                    R.string.label_settings_library_androidx.inflateString(),
                    R.string.description_settings_library_androidx.inflateString(),
                    SettingsItemAction.SettingAboutGithub("https://developer.android.com/jetpack/androidx")
                )
            )
            .append(
                SettingsItem(
                    R.string.label_settings_library_multitype.inflateString(),
                    R.string.description_settings_library_multitype.inflateString(),
                    SettingsItemAction.SettingAboutGithub("https://github.com/drakeet/MultiType")
                )
            )
            .append(
                SettingsItem(
                    R.string.label_settings_library_speed_dial.inflateString(),
                    R.string.description_settings_library_speed_dial.inflateString(),
                    SettingsItemAction.SettingAboutGithub("https://github.com/leinardi/FloatingActionButtonSpeedDial")
                )
            )
            .append(
                SettingsItem(
                    R.string.label_settings_library_sardine_android.inflateString(),
                    R.string.description_settings_library_sardine_android.inflateString(),
                    SettingsItemAction.SettingAboutGithub("https://github.com/thegrizzlylabs/sardine-android")
                )
            )
            .append(
                SettingsItem(
                    R.string.label_settings_library_mmkv.inflateString(),
                    R.string.description_settings_library_mmkv.inflateString(),
                    SettingsItemAction.SettingAboutGithub("https://github.com/Tencent/MMKV")
                )
            )
            .append(
                SettingsItem(
                    R.string.label_settings_library_gson.inflateString(),
                    R.string.description_settings_library_gson.inflateString(),
                    SettingsItemAction.SettingAboutGithub("https://github.com/google/gson")
                )
            )
    }

    private fun Int.inflateString(): String {
        return resources.getString(this)
    }
}