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
package org.aerovek.chartr.util

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import org.aerovek.chartr.R

@BindingAdapter("dataItems")
fun RecyclerView.setDataItems(dataItems: List<Item<*>>) {
    findOrAddDataSection()?.update(dataItems)
    adapter?.notifyDataSetChanged()
}

@BindingAdapter("profileImageUri")
fun ImageView.setProfileImageUri(profileImageUri: Uri?) {
    if (profileImageUri != null) {
        Glide.with(this)
            .load(profileImageUri)
            .placeholder(R.drawable.ic_person)
            .error(R.drawable.ic_person)
            .circleCrop()
            .into(this)
    }
}

@BindingAdapter("profileImageBitmap")
fun ImageView.setProfileImageBitmap(profileImageBitmap: Bitmap?) {
    if (profileImageBitmap != null) {
        val options = RequestOptions()

        Glide.with(this)
            .asBitmap()
            .load(profileImageBitmap)
            .placeholder(R.drawable.ic_person)
            .error(R.drawable.ic_person)
            .transition(withCrossFade())
            .circleCrop()
            .into(this)
    }
}

@BindingAdapter("imageUri")
fun ImageView.setImageUri(imageUri: String?) {
    if (!imageUri.isNullOrEmpty()) {
        Glide.with(this)
            .load(imageUri)
            .placeholder(drawable)
            .error(drawable)
            .into(this)
    }
}

private fun RecyclerView.findOrAddDataSection(): Section? {
    with(adapter) {
        if (this !is GroupAdapter<*>) {
            return null
        }
        with(this as GroupAdapter) {
            if (getGroupCount() > 0) {
                with(getTopLevelGroup(0)) {
                    if (this is Section) {
                        return this
                    }
                }
            }
            val dataHolderSection = Section()
            this.add(0, dataHolderSection)
            return dataHolderSection
        }
    }
}