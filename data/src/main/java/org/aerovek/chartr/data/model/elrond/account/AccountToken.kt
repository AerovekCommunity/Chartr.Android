package org.aerovek.chartr.data.model.elrond.account

data class AccountToken(
    val identifier: String,
    val name: String,
    val ticker: String,
    val owner: String,
    val decimals: Int,
    val isPaused: Boolean,
    val transactions: Int,
    val accounts: Int,
    val canUpgrade: Boolean,
    val canMint: Boolean,
    val canBurn: Boolean,
    val canChangeOwner: Boolean,
    val canPause: Boolean,
    val canFreeze: Boolean,
    val canWipe: Boolean,
    val balance: String?
)
