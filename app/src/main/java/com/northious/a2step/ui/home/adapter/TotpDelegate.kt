package com.northious.a2step.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import com.northious.a2step.R
import com.northious.a2step.extends.clip
import com.northious.a2step.databinding.LayoutTotpItemBinding
import com.northious.a2step.totp.TotpGenerator
import com.northious.a2step.ui.home.bean.Totp
import com.northious.a2step.ui.home.extends.setRoundedOutlineProvider

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
            itemView.setRoundedOutlineProvider(40f)
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