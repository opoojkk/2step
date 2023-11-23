package org.getbuddies.a2step.ui.settings.delegate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import org.getbuddies.a2step.R
import org.getbuddies.a2step.databinding.ItemSetttingsContentBinding
import org.getbuddies.a2step.ui.settings.model.SettingsItem

class SettingsItemDelegate : ItemViewDelegate<SettingsItem, SettingsItemViewHolder>() {
    override fun onCreateViewHolder(context: Context, parent: ViewGroup): SettingsItemViewHolder {
        return SettingsItemViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_setttings_content, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SettingsItemViewHolder, item: SettingsItem) {
        holder.mBinding.syncSettingTitle.text = item.title
        holder.mBinding.syncSettingDescription.text = item.description
    }
}

class SettingsItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val mBinding: ItemSetttingsContentBinding

    init {
        mBinding = ItemSetttingsContentBinding.bind(itemView)
    }
}

