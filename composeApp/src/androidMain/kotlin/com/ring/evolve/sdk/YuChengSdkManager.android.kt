package com.ring.evolve.sdk

import android.content.Context
import com.yucheng.ycbtsdk.BuildConfig
import com.yucheng.ycbtsdk.YCBTClient
import com.yucheng.ycbtsdk.bean.ScanDeviceBean
import com.yucheng.ycbtsdk.response.BleConnectResponse
import com.yucheng.ycbtsdk.response.BleScanResponse

//actual class YuChengSdkManager(
//    private val context: Context
//) {
//    actual fun init() {
//        YCBTClient.initClient(context, true, BuildConfig.DEBUG)
//    }
//
//    actual fun startScan(onDeviceFound: (String, String) -> Unit) {
//        YCBTClient.startScanBle(object : BleScanResponse {
//            override fun onScanResponse(code: Int, device: ScanDeviceBean?) {
//                device?.let {
//                    onDeviceFound(it.deviceName ?: "Unknown", it.deviceMac)
//                }
//            }
//        }, 6)
//    }
//
//    actual fun connect(mac: String, onResult: (Boolean) -> Unit) {
//        YCBTClient.connectBle(mac, object : BleConnectResponse {
//            override fun onConnectResponse(code: Int) {
//                onResult(code == 0x0a) // ReadWriteOK
//            }
//        })
//    }
//
//    actual fun disconnect() {
//        YCBTClient.disconnectBle()
//    }
//}