package com.ring.evolve


import android.app.Application
import com.ring.evolve.di.commonModule
import com.ring.evolve.di.platformBluetoothModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class EvolveApplication : Application()  {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@EvolveApplication)
            modules(
                commonModule(),
                platformBluetoothModule(this@EvolveApplication)
            )
        }
    }
}