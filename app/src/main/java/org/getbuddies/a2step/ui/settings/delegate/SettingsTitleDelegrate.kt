package org.getbuddies.a2step.ui.settings.delegate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import org.getbuddies.a2step.R
import org.getbuddies.a2step.databinding.ItemSettingsTitleBinding

class SettingsTitleDelegate : ItemViewDelegate<String, SettingsTitleViewHolder>() {
    override fun onCreateViewHolder(context: Context, parent: ViewGroup): SettingsTitleViewHolder {
        return SettingsTitleViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_settings_title, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SettingsTitleViewHolder, item: String) {
        holder.mBinding.title.text = item
    }
}

class SettingsTitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val mBinding: ItemSettingsTitleBinding

    init {
        mBinding = ItemSettingsTitleBinding.bind(itemView)
    }
}