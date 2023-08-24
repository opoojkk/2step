package org.getbuddies.a2step.ui.home.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import com.google.android.material.snackbar.Snackbar
import org.getbuddies.a2step.R
import org.getbuddies.a2step.databinding.LayoutTotpItemBinding
import org.getbuddies.a2step.db.totp.entity.Totp
import org.getbuddies.a2step.extends.clip
import org.getbuddies.a2step.totp.TotpGenerator
import org.getbuddies.a2step.ui.extendz.dpToPx
import org.getbuddies.a2step.ui.home.EditState
import org.getbuddies.a2step.ui.home.extends.setRoundedOutlineProvider
import org.getbuddies.a2step.ui.home.view.ProgressBar

class TotpDelegate(editStateListener: EditStateListener) :
    ItemViewDelegate<Totp, TotpDelegate.ViewHolder>() {
    private val mOnLongPressListener = editStateListener

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.layout_totp_item, parent, false)
        return ViewHolder(itemView, mOnLongPressListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Totp) {
        holder.bind(item)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        holder.reset()
    }

    class ViewHolder(itemView: View, editStateListener: EditStateListener) :
        RecyclerView.ViewHolder(itemView) {
        private val mBinding: LayoutTotpItemBinding
        private val mEditStateListener = editStateListener
        private var mEditState = EditState.NORMAL

        init {
            mBinding = LayoutTotpItemBinding.bind(itemView)
            itemView.setRoundedOutlineProvider(20f.dpToPx())
            itemView.setOnClickListener {
                when (getEditState()) {
                    EditState.NORMAL -> {
                        it.context.clip(mBinding.totpCode.text.toString())
                        Snackbar.make(it, R.string.snackbar_totp_copy, Snackbar.LENGTH_SHORT).show()
                    }

                    EditState.SELECTED -> {
                        mEditStateListener.onUnselected()
                        setEditState(EditState.NORMAL)
                    }
                }
            }
            itemView.setOnLongClickListener {
                when (getEditState()) {
                    EditState.NORMAL -> {
                        mEditStateListener.onSelected()
                        itemView.setBackgroundColor(it.context.getColor(R.color.surface_on))
                        setEditState(EditState.SELECTED)
                    }

                    EditState.SELECTED -> {
                        mEditStateListener.onUnselected()
                        itemView.setBackgroundColor(Color.TRANSPARENT)
                        setEditState(EditState.NORMAL)
                    }
                }
                true
            }
        }

        fun bind(item: Totp) {
            mBinding.totpName.text = item.name
            mBinding.totpAccount.text = item.account
            mBinding.totpProgressBar.setOnProgressListener(object : ProgressBar.OnProgressListener {
                override fun generateTotp() {
                    mBinding.totpCode.text = TotpGenerator.generateNow(item.secret)
                }
            })
            mBinding.totpProgressBar.start()
        }

        fun reset() {
            mEditState = EditState.NORMAL
        }

        fun getEditState(): EditState {
            return mEditState
        }

        fun setEditState(state: EditState) {
            mEditState = state
        }
    }

    interface EditStateListener {
        fun onSelected()
        fun onUnselected()
    }
}