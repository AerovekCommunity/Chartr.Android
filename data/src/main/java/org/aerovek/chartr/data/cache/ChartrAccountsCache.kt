package org.aerovek.chartr.data.cache

import org.aerovek.chartr.data.model.ChartrAccount

object ChartrAccountsCache {
    var businessAccounts: List<ChartrAccount> = listOf()
    var userAccounts: List<ChartrAccount> = listOf()
}
