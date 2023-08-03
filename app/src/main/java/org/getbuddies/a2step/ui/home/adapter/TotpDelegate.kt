package org.getbuddies.a2step.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import org.getbuddies.a2step.R
import org.getbuddies.a2step.extends.clip
import org.getbuddies.a2step.databinding.LayoutTotpItemBinding
import org.getbuddies.a2step.totp.TotpGenerator
import org.getbuddies.a2step.db.totp.entity.Totp
import org.getbuddies.a2step.ui.extendz.dpToPx
import org.getbuddies.a2step.ui.home.extends.setRoundedOutlineProvider

class TotpDelegate : ItemViewDelegate<Totp, TotpDelegate.ViewHolder>() {
    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.layout_totp_item, parent, false),
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Totp) {
        holder.bind(item)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding: LayoutTotpItemBinding

        init {
            mBinding = LayoutTotpItemBinding.bind(itemView)
            itemView.setRoundedOutlineProvider(20f.dpToPx().toFloat())
            itemView.setOnClickListener {
                it.context.clip(mBinding.totpCode.text.toString())
                Toast.makeText(it.context, "已复制", Toast.LENGTH_SHORT).show()
            }
        }

        fun bind(item: Totp) {
            mBinding.totpName.text = item.name
            mBinding.totpAccount.text = item.account
            mBinding.totpCode.text = TotpGenerator.generateNow(item.secret)
        }
    }
}