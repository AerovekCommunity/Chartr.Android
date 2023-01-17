/*
The MIT License (MIT)

Copyright (c) 2023-present Aerovek

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
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