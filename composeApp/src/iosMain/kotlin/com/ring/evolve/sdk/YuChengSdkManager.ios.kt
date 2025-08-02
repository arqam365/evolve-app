package com.ring.evolve.sdk
import platform.Foundation.*
import kotlinx.cinterop.ExportObjCClass
import platform.CoreBluetooth.*
import platform.darwin.NSObject

//@ExportObjCClass
//actual class YuChengSdkManager : NSObject() {
////    actual override fun init(): YuChengSdkManager {
//////        val manager = YCProduct.shared
////        // You can also observe notification here
////        return this
////    }
//
////    actual fun init() {
////        // iOS specific initialization already handled in `init`
////    }
//
//    actual fun startScan(onDeviceFound: (String, String) -> Unit) {
////        YCProduct.scanningDeviceWithDelayTime(
////            delayTime = 3.0,
////            completion = { devices, error ->
////                (devices as? List<CBPeripheral>)?.forEach { peripheral ->
////                    onDeviceFound(peripheral.name ?: "Unknown", peripheral.identifier.UUIDString)
////                }
////            }
////        )
//    }
//
//    actual fun connect(mac: String, onResult: (Boolean) -> Unit) {
//        // You will need to map mac to CBPeripheral from scan result and call connect
//        // SDK doesn't directly support connect by MAC on iOS
//    }
//
//    actual fun disconnect() {
//        // Use YCProduct.shared to disconnect current connected device
//    }
//}