package com.ring.evolve.utils.BleConnectivity

import kotlinx.coroutines.flow.Flow

interface BleSupportInterface {
    fun startScan(addDevice: (device: ScanDevice) -> Unit)
    fun connect(mac: String, statusCodeChange: (statusCode: Int) -> Unit)
    fun disconnect()
    fun connectionStatus(): Flow<String>
    fun startEcg(hand:Int,updateChunk:(List<Int>)-> Unit)
    fun stopEcg()
    fun startHeartRateMonitoring()
    fun getHealthData()
    fun startHeartRateMeasurement()
    fun startBloodPressure()
    fun getBloodOxygen()
    fun getTemperature()
    fun startTemperatureMonitoring()
    fun enableRealData(changeData:(String,String,String)->Unit)
    fun getBloodGlucose(onDataUpdate:(String)-> Unit)
    fun getUricAcid(onDataUpdate:(String)-> Unit)
    fun getContinuousTemperature(updateData:(String)-> Unit)
}

data class ScanDevice(
    val name:String,
    val mac: String
)