package com.ring.evolve.utils.AndroidBleSupport

import android.util.Log
import com.ring.evolve.utils.BleConnectivity.BleSupportInterface
import com.yucheng.ycbtsdk.Constants
import com.yucheng.ycbtsdk.YCBTClient
import com.yucheng.ycbtsdk.bean.ScanDeviceBean
import com.yucheng.ycbtsdk.response.BleScanResponse

class BleAndroid: BleSupportInterface {
    override fun startScan() {
        Log.d("Android Ble Search", "${ YCBTClient.connectState()== Constants.BLEState.ReadWriteOK }")
        YCBTClient.startScanBle(object: BleScanResponse{
            override fun onScanResponse(
                p0: Int,
                device: ScanDeviceBean?,
            ) {
                if (device!=null) {
                    Log.d("Android Ble Search","Device Found: ${device.deviceName}")
                    Log.d("Android Ble Search","p0: ${p0}")
                }
                else{
                    Log.d("Android Ble Search","Device Found: null")
                    Log.d("Android Ble Search","p0: ${p0}")

                }
            }

        },10)
    }
}