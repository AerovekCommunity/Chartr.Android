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

import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.viewbinding.BindableItem
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.SearchResultItemBinding
import org.aerovek.chartr.ui.adapterItems.viewmodels.SearchItemViewModel
import org.aerovek.chartr.util.setProfileImageUri

class SearchResultItem(val searchItemViewModel: SearchItemViewModel) : BindableItem<SearchResultItemBinding>() {

    private val firebaseStorage = FirebaseStorage.getInstance("gs://aerovek-aviation.appspot.com")
    private val storageRef = firebaseStorage.reference

    override fun bind(viewBinding: SearchResultItemBinding, position: Int) {
        viewBinding.vm = searchItemViewModel

        // Retrieve the image from firebase if we have one
        searchItemViewModel.profileImageUrl?.let { url ->
            storageRef.child(url).downloadUrl.addOnSuccessListener { uri ->
                viewBinding.profileImage.setProfileImageUri(uri)
            }.addOnFailureListener { ex ->
                ex.printStackTrace()
            }
        }

        // If no business name show their username for now
        if (searchItemViewModel.businessName.isNullOrEmpty()) {
            viewBinding.businessNameLabel.text = "<business name unavailable>"
        }
    }

    override fun getLayout(): Int = R.layout.search_result_item

    override fun initializeViewBinding(view: View): SearchResultItemBinding {
        return DataBindingUtil.bind(view)!!
    }
}