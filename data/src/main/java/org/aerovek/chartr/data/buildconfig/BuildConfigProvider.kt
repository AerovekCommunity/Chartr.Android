package org.aerovek.chartr.data.buildconfig

/** Abstracts values from the application generated BuildConfig to be accessible anywhere and improve testability */
interface BuildConfigProvider {
    /** True if this is a debug development or beta build, both should use the devnet connection */
    val isDebugOrBetaBuild: Boolean

    /** True if this is a production build, and should use the mainnet connection */
    val isProductionBuild: Boolean
}