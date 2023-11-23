package org.getbuddies.a2step.ui.settings

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
            .append(inflateString(R.string.label_settings_header_prefs))
            .append(
                SettingsItem(
                    inflateString(R.string.label_settings_sync),
                    inflateString(R.string.label_settings_sync),
                    SettingsItemAction.SettingsSync
                )
            ).append(inflateString(R.string.label_profile_migrate))
            .append(
                SettingsItem(
                    inflateString(R.string.label_profile_migrate),
                    inflateString(R.string.label_profile_migrate),
                    SettingsItemAction.SettingsMigrateImport
                )
            )
            .append(
                SettingsItem(
                    inflateString(R.string.label_profile_migrate),
                    inflateString(R.string.label_profile_migrate),
                    SettingsItemAction.SettingsMigrateExport
                )
            )
            .append(inflateString(R.string.label_settings_about))
            .append(
                SettingsItem(
                    inflateString(R.string.label_settings_about),
                    inflateString(R.string.label_settings_about),
                    SettingsItemAction.SettingAboutGithub
                )
            )
    }

    private fun inflateString(resId: Int): String {
        return resources.getString(resId)
    }
}