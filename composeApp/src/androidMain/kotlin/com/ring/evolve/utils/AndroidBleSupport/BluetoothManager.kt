package com.ring.evolve.utils.AndroidBleSupport

import android.bluetooth.BluetoothManager
import android.content.Context

object BluetoothManager {

    private lateinit var bluetoothManager: BluetoothManager
    private val bluetoothAdapter by lazy { bluetoothManager.adapter }
    private val bluetoothLeScanner by lazy { bluetoothAdapter.bluetoothLeScanner }
    private val TAG= "BLEObserver"

    fun init(context: Context) {
        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }
}