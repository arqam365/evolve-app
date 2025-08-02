package com.ring.evolve.utils.BleConnectivity

import kotlinx.coroutines.flow.Flow

interface BleSupportInterface {
    fun startScan(addDevice: (device: ScanDevice) -> Unit)
    fun connect(mac: String, statusCodeChange: (statusCode: Int) -> Unit)
    fun disconnect(mac: String)

    fun connectionStatus(): Flow<String>
}

data class ScanDevice(
    val name:String,
    val mac: String
)