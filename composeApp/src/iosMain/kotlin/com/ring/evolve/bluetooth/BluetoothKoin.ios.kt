package com.ring.evolve.bluetooth

import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformBluetoothModule(): Module = module {
    single<BluetoothManager> { BluetoothManager() }
}