package org.aerovek.chartr

import org.aerovek.chartr.data.buildconfig.BuildConfigProvider

class BuildConfigProviderImpl : BuildConfigProvider {
    override val isDebugOrBetaBuild: Boolean
        get() = BuildConfig.DEBUG || BuildConfig.INTERNAL
    override val isProductionBuild: Boolean
        get() = BuildConfig.PRODUCTION
}
