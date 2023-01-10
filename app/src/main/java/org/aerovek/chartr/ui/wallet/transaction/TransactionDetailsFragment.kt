package org.aerovek.chartr.ui.wallet.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.aerovek.chartr.R
import org.aerovek.chartr.ui.BaseFragment
import org.koin.android.ext.android.inject

class TransactionDetailsFragment : BaseFragment() {
    private val viewModel: TransactionDetailsViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.transaction_details_fragment, container, false)
    }
}