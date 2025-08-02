package com.ring.evolve.di

import android.content.Context
import com.ring.evolve.bluetooth.BluetoothManager
import com.ring.evolve.utils.AndroidBleSupport.BleAndroid
import com.ring.evolve.utils.BleConnectivity.BleSupportInterface
import com.ring.evolve.utils.storage.PlatformSharedPreference
import com.ring.evolve.utils.storage.SharedPreferenceStorageTypes
import org.koin.dsl.module

fun platformBluetoothModule(context: Context) = module {
    single<BluetoothManager> { BluetoothManager(context) }
    single<SharedPreferenceStorageTypes> {
        PlatformSharedPreference().initialize(context)
    }

    single<BleSupportInterface> { BleAndroid() }
}