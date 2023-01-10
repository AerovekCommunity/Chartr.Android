package org.aerovek.chartr.ui.adapterItems

import android.content.Context
import android.view.View
import androidx.databinding.DataBindingUtil
import com.xwray.groupie.viewbinding.BindableItem
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.MoreItemBinding
import org.aerovek.chartr.ui.adapterItems.viewmodels.MoreItemViewModel

class MoreItem(private val context: Context, private val viewModel: MoreItemViewModel, private val tapCallbacks: MoreItemTapCallbacks) : BindableItem<MoreItemBinding>() {
    override fun bind(viewBinding: MoreItemBinding, position: Int) {
        viewBinding.imageView.setImageDrawable(viewModel.imageDrawable)
        viewBinding.title = viewModel.title
        viewBinding.vm = viewModel
        viewBinding.callback = tapCallbacks
    }

    override fun getLayout() = R.layout.more_item

    override fun initializeViewBinding(view: View): MoreItemBinding {
        return DataBindingUtil.bind(view)!!
    }
}