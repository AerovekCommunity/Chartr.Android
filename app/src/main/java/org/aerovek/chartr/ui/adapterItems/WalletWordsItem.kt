package org.aerovek.chartr.ui.adapterItems

import android.view.View
import androidx.databinding.DataBindingUtil
import com.xwray.groupie.viewbinding.BindableItem
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.WalletWordsItemBinding

class WalletWordsItem(val title: String) : BindableItem<WalletWordsItemBinding>() {
    override fun bind(viewBinding: WalletWordsItemBinding, position: Int) {
        viewBinding.item = title
    }

    override fun getLayout() = R.layout.wallet_words_item

    override fun initializeViewBinding(view: View): WalletWordsItemBinding {
        return DataBindingUtil.bind(view)!!
    }
}