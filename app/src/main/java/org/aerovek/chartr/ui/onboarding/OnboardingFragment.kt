package org.aerovek.chartr.ui.onboarding

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import org.aerovek.chartr.R
import org.aerovek.chartr.databinding.OnboardingFragmentBinding
import org.aerovek.chartr.ui.BaseFragment

class OnboardingFragment(
    private val _title: String,
    private val _body: String,
    private val _contentDesc: String,
    private val _imageRes: Int
): BaseFragment() {

    private lateinit var binding: OnboardingFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return DataBindingUtil.inflate<OnboardingFragmentBinding>(inflater, R.layout.onboarding_fragment, container, false).apply {

            binding = this
            lifecycleOwner = this@OnboardingFragment.viewLifecycleOwner

            binding.title.text = _title
            binding.description.text = _body
            binding.imageView.contentDescription = _contentDesc
            binding.imageView.setImageResource(_imageRes)
        }.root
    }
}