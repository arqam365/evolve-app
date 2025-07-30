package com.ring.evolve.bluetooth

import android.content.Context
import com.ring.evolve.AppContextHolder.context
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformBluetoothModule(): Module = module {
    single<BluetoothManager> { BluetoothManager(context) }
}

//actual fun platformBluetoothModule(): Module {
//    TODO("Not yet implemented")
//}