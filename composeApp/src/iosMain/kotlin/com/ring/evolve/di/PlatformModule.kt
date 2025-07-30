package com.ring.evolve.di

import com.ring.evolve.bluetooth.BluetoothManager
import com.ring.evolve.utils.storage.PlatformSharedPreference
import com.ring.evolve.utils.storage.SharedPreferenceStorageTypes
import org.koin.dsl.module

fun platformBluetoothModule() = module {
    single<BluetoothManager> { BluetoothManager() }
    single<SharedPreferenceStorageTypes> { PlatformSharedPreference() }
}