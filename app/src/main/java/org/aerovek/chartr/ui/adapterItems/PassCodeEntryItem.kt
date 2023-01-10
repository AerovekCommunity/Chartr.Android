package org.aerovek.chartr.ui.adapterItems

import android.view.View
import androidx.databinding.DataBindingUtil
import com.xwray.groupie.viewbinding.BindableItem
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.PasscodeButtonItemBinding

class PassCodeEntryItem (val id : Int, var isActive : Boolean ): BindableItem<PasscodeButtonItemBinding> (){
    override fun getLayout() = R.layout.passcode_button_item

    override fun initializeViewBinding(view: View) : PasscodeButtonItemBinding{
        return DataBindingUtil.bind(view)!!
    }
    override fun bind(viewBinding: PasscodeButtonItemBinding, position: Int) {
        viewBinding.isActive = isActive
    }

}