package org.aerovek.chartr

import android.app.Application
import org.aerovek.chartr.data.di.DataModules
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidContext

/**
 * Entry point for the application
 */
class ChartrApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        println("[ChartrApplication onCreate]")

        startKoin {
            androidContext(this@ChartrApplication)
            modules(
                listOf(
                    AppModules.appModule,
                    DataModules.dataModule,
                    DataModules.networkModule
                )
            )
        }
    }
}