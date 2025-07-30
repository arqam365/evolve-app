package com.ring.evolve.di

import com.ring.evolve.bluetooth.platformBluetoothModule
import org.koin.core.context.startKoin

//object KoinInitializer {
//    fun startKoinAndroid(context: Context) {
//        startKoin {
//            modules(platformBluetoothModule(context))
//        }
//    }
//
//    fun startKoinIos() {
//        startKoin {
//            modules(platformBluetoothModule())
//        }
//    }
//}