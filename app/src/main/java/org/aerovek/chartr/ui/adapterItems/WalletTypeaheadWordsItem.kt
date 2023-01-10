package org.aerovek.chartr.ui.adapterItems

import android.view.View
import androidx.databinding.DataBindingUtil
import com.xwray.groupie.viewbinding.BindableItem
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.WalletTypeaheadWordsItemBinding

class WalletTypeaheadWordsItem(val title: String, val tapCallback: StringItemTapCallback) : BindableItem<WalletTypeaheadWordsItemBinding>() {
    override fun bind(viewBinding: WalletTypeaheadWordsItemBinding, position: Int) {
        viewBinding.item = title
        viewBinding.callback = tapCallback
    }

    override fun getLayout() = R.layout.wallet_typeahead_words_item

    override fun initializeViewBinding(view: View): WalletTypeaheadWordsItemBinding {
        return DataBindingUtil.bind(view)!!
    }
}