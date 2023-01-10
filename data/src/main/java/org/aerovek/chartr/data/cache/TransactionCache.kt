package org.aerovek.chartr.data.cache

import org.aerovek.chartr.data.model.elrond.transaction.TransactionOnNetwork

object TransactionCache {
    var transactionHistory: List<TransactionOnNetwork> = listOf()
}