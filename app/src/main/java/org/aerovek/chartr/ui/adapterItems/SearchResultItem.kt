package org.aerovek.chartr.ui.adapterItems

import android.net.Uri
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
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