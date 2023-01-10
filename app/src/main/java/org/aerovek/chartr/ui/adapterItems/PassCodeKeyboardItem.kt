package org.aerovek.chartr.ui.adapterItems

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.DataBindingUtil
import com.xwray.groupie.viewbinding.BindableItem
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.PinPadKeyboardButtonBinding
import org.aerovek.chartr.ui.passcode.PassCodeViewModel
import org.aerovek.chartr.ui.passcode.PinPadModel
import org.aerovek.chartr.ui.passcode.PinPadType

class PassCodeKeyboardItem(
    private val buttonBackgroud: Drawable,
    private val pinPadModel: PinPadModel,
    val passCodeViewModel: PassCodeViewModel
    ): BindableItem<PinPadKeyboardButtonBinding>() {

    override fun getLayout() = R.layout.pin_pad_keyboard_button

    override fun initializeViewBinding(view: View) : PinPadKeyboardButtonBinding {
        return DataBindingUtil.bind(view)!!
    }
    override fun bind(viewBinding: PinPadKeyboardButtonBinding, position: Int) {
        viewBinding.passCodeViewModel = passCodeViewModel
        viewBinding.background = buttonBackgroud
        viewBinding.pinPad = pinPadModel

        if (pinPadModel.type == PinPadType.BackSpace) {
            viewBinding.btn.setPadding(0, 56, 0, 0)
        }
    }
}
