package org.aerovek.chartr.data.cache

import org.aerovek.chartr.data.model.elrond.account.Account
import org.aerovek.chartr.data.model.elrond.account.AccountToken
import org.aerovek.chartr.data.model.elrond.esdt.EsdtToken
import org.aerovek.chartr.data.model.elrond.network.NetworkEconomics

object WalletCache {
    var accountTokenDetails: AccountToken? = null
    var aeroDetails: EsdtToken? = null
    var networkEconomics: NetworkEconomics? = null
    var walletAccount: Account? = null
}