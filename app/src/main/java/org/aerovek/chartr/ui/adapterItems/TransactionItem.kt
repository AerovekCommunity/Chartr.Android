package org.aerovek.chartr.ui.adapterItems

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.xwray.groupie.viewbinding.BindableItem
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.TransactionItemBinding
import org.aerovek.chartr.ui.adapterItems.viewmodels.TransactionItemViewModel
import java.util.*

class TransactionItem(private val context: Context, private val viewModel: TransactionItemViewModel)
    : BindableItem<TransactionItemBinding>() {

    override fun bind(viewBinding: TransactionItemBinding, position: Int) {
        viewBinding.vm = viewModel

        // If receiving let's change the text color to green to denote an addition of funds
        if (viewModel.isReceiveType) {
            viewBinding.aeroAmountLabel.setTextColor(ContextCompat.getColor(context, R.color.green))
        } else {
            viewBinding.aeroAmountLabel.setTextColor(ContextCompat.getColor(context, R.color.red))
        }

        when (viewModel.status.lowercase(Locale.getDefault())) {
            "fail" -> viewBinding.status.setTextColor(ContextCompat.getColor(context, R.color.red))
            "invalid" -> viewBinding.status.setTextColor(ContextCompat.getColor(context, R.color.red))
            "success" -> viewBinding.status.setTextColor(ContextCompat.getColor(context, R.color.green))
            "pending" -> viewBinding.status.setTextColor(ContextCompat.getColor(context, R.color.orange))
        }
    }

    override fun getLayout() = R.layout.transaction_item

    override fun initializeViewBinding(view: View): TransactionItemBinding {
        return DataBindingUtil.bind(view)!!
    }
}