package org.getbuddies.a2step.ui.home.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
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

        private var mTotp: Totp? = null

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
                        mEditStateListener.onUnselected(mTotp ?: Totp.DEFAULT)
                        itemView.setBackgroundColor(Color.TRANSPARENT)
                        setEditState(EditState.NORMAL)
                    }
                }
            }
            itemView.setOnLongClickListener { it ->
                when (getEditState()) {
                    EditState.NORMAL -> {
                        val popupMenu = PopupMenu(it.context, it)
                        popupMenu.inflate(R.menu.totp_long_press_options)
                        popupMenu.gravity = Gravity.END
                        popupMenu.show()
                        popupMenu.setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.menu_item_totp_modify -> {
                                    mEditStateListener.onModify(mTotp ?: Totp.DEFAULT)
                                    return@setOnMenuItemClickListener true
                                }

                                R.id.menu_item_totp_delete -> {
                                    mEditStateListener.onDelete(mTotp ?: Totp.DEFAULT)
                                    return@setOnMenuItemClickListener true
                                }

                                R.id.menu_item_totp_select -> {
                                    mEditStateListener.onSelected(mTotp ?: Totp.DEFAULT)
                                    itemView.setBackgroundColor(itemView.context.getColor(R.color.surface_on))
                                    setEditState(EditState.SELECTED)
                                    return@setOnMenuItemClickListener true
                                }

                                else -> {
                                    return@setOnMenuItemClickListener false
                                }
                            }
                        }
                    }

                    EditState.SELECTED -> {
                        mEditStateListener.onUnselected(mTotp ?: Totp.DEFAULT)
                        itemView.setBackgroundColor(Color.TRANSPARENT)
                        setEditState(EditState.NORMAL)
                    }
                }
                true
            }
        }

        fun bind(item: Totp) {
            mTotp = item
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
            itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        fun getEditState(): EditState {
            return mEditState
        }

        fun setEditState(state: EditState) {
            mEditState = state
        }

        fun getTotp(): Totp? {
            return mTotp
        }
    }

    interface EditStateListener {
        // call when item is selected
        fun onSelected(totp: Totp)

        // call when item is unselected
        fun onUnselected(totp: Totp)

        fun onModify(totp: Totp)
        fun onDelete(totp: Totp)
    }
}