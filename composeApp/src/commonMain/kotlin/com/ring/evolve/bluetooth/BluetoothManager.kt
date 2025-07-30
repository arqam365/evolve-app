package com.ring.evolve.bluetooth

import com.ring.evolve.data.ScannedDevice
import com.ring.evolve.di.PlatformLogger


expect class BluetoothManager {

    val logger: PlatformLogger
    fun startScan(onResult: (List<ScannedDevice>) -> Unit)
    fun connect(address: String, onConnected: (Boolean) -> Unit)
    fun disconnect()
}