package com.ring.evolve.viewmodel

import androidx.lifecycle.ViewModel
import com.ring.evolve.bluetooth.BluetoothManager
import com.ring.evolve.data.ScannedDevice
//import com.ring.evolve.sdk.YuChengSdkManager
import com.ring.evolve.utils.storage.SharedPreferenceStorageTypes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BleViewModel(
    private val bleManager: BluetoothManager,
    private val prefs: SharedPreferenceStorageTypes
) : ViewModel() {

    val _devices = MutableStateFlow<List<ScannedDevice>>(emptyList())
    val devices: StateFlow<List<ScannedDevice>> = _devices

    private val _connected = MutableStateFlow<String?>(null)
    val connected: StateFlow<String?> = _connected

    fun startScan() {
        bleManager.startScan {
            _devices.value = it
        }
    }

    fun connect(address: String) {
        bleManager.logger.log("BleViewModel: Connecting to $address")
        bleManager.connect(address) { success ->
            if (success) {
                bleManager.logger.log("Connected successfully to $address")
                _connected.value = address
                prefs.setString("last_connected_device", address)
            } else {
                bleManager.logger.log("Failed to connect to $address")
            }
        }
    }

    fun disconnect() {
        bleManager.logger.log("BleViewModel: Disconnecting...")
        bleManager.disconnect()
        _connected.value = null
    }

    fun loadLastDevice() {
        val last = prefs.getString("last_connected_device")
        bleManager.logger.log("BleViewModel: Loaded last device $last")
        _connected.value = last
    }
}

//class BleViewModel(
//    private val sdk: YuChengSdkManager
//) : ViewModel() {
//
//    fun scanDevices() {
//        sdk.startScan { name, mac ->
//            println("Found: $name ($mac)")
//        }
//    }
//
//    fun connectDevice(mac: String) {
//        sdk.connect(mac) { success ->
//            println("Connection success = $success")
//        }
//    }
//
//    fun disconnect() {
//        sdk.disconnect()
//    }
//}