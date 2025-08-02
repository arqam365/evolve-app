package com.ring.evolve.utils.BleConnectivity

import kotlinx.coroutines.flow.Flow

interface BleSupportInterface {
    fun startScan(addDevice: (device: ScanDevice) -> Unit)
    fun connect(mac: String, statusCodeChange: (statusCode: Int) -> Unit)
    fun disconnect()

    fun connectionStatus(): Flow<String>

    fun startEcg(hand:Int)

    fun stopEcg()

    fun startHeartRateMonitoring()

}

data class ScanDevice(
    val name:String,
    val mac: String
)