package com.ring.evolve.utils.AndroidBleSupport

import android.util.Log
import com.ring.evolve.utils.BleConnectivity.BleSupportInterface
import com.ring.evolve.utils.BleConnectivity.ScanDevice
import com.yucheng.ycbtsdk.Constants
import com.yucheng.ycbtsdk.YCBTClient
import com.yucheng.ycbtsdk.bean.ScanDeviceBean
import com.yucheng.ycbtsdk.response.BleConnectResponse
import com.yucheng.ycbtsdk.response.BleScanResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

class BleAndroid: BleSupportInterface {
    override fun startScan(addDevice: (ScanDevice) -> Unit) {
        YCBTClient.disconnectBle()
        YCBTClient.stopScanBle()
        Log.d("Android Ble Search", "${ YCBTClient.connectState()== Constants.BLEState.ReadWriteOK }")
        YCBTClient.startScanBle(object: BleScanResponse{
            override fun onScanResponse(
                p0: Int,
                device: ScanDeviceBean?,
            ) {
                if (device!=null) {
                    Log.d("Android Ble Search","Device Found: ${device.deviceName}")
                    Log.d("Android Ble Search","p0: ${p0}")
                    addDevice(ScanDevice(device.deviceName?:"Unknown",device.deviceMac))
                }
                else{
                    Log.d("Android Ble Search","Device Found: null")
                    Log.d("Android Ble Search","p0: ${p0}")

                }
            }

        },10)
    }

    override fun connect(mac: String, statusCodeChange: (Int) -> Unit) {
        YCBTClient.connectBle(mac,object : BleConnectResponse{
            override fun onConnectResponse(p0: Int) {
                Log.d("connectResponse", p0.toString())
                statusCodeChange(p0)
            }
        })
    }

    override fun disconnect(mac: String) {
        YCBTClient.disconnectBle()
    }

    override fun connectionStatus(): Flow<String> = callbackFlow {
        val callback = object : BleConnectResponse {
            override fun onConnectResponse(code: Int) {
                when (code) {
                    Constants.BLEState.Disconnect -> trySend("Disconnect")
                    Constants.BLEState.ReadWriteOK -> trySend("Connected")
                    Constants.BLEState.TimeOut -> trySend("Timeout")
                    Constants.BLEState.Disconnecting -> trySend("Disconnecting")
                    else -> trySend("Unknown")
                }
            }
        }

        YCBTClient.registerBleStateChange(callback)
    }


}