package org.aerovek.chartr.data.model.elrond.esdt

data class EsdtToken(
    val identifier: String,
    val name: String,
    val ticker: String,
    val owner: String,
    val minted: String,
    val burnt: String,
    val initialMinted: String,
    val decimals: Int,
    val isPaused: Boolean,
    val assets: EsdtAssets?,
    val transactions: Int,
    val accounts: Int,
    val canUpgrade: Boolean,
    val canMint: Boolean,
    val canBurn: Boolean,
    val canChangeOwner: Boolean,
    val canPause: Boolean,
    val canFreeze: Boolean,
    val canWipe: Boolean,
    val price: Double,
    val marketCap: Double,
    val supply: String,
    val circulatingSupply: String
) {
    data class EsdtAssets(
        val website: String,
        val description: String,
        val social: EsdtSocialInfo,
        val status: String,
        val pngUrl: String,
        val svgUrl: String
    )

    data class EsdtSocialInfo(
        val email: String,
        val blog: String,
        val twitter: String,
        val whitepaper: String,
        val coinmarketcap: String,
        val coingecko: String
    )
}