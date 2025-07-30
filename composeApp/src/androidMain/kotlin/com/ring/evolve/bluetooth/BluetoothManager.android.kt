package com.ring.evolve.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.ring.evolve.AppContextHolder
import com.ring.evolve.data.ScannedDevice
import com.ring.evolve.di.PlatformLogger
import com.yucheng.ycbtsdk.BuildConfig
import com.yucheng.ycbtsdk.YCBTClient
import com.yucheng.ycbtsdk.bean.ScanDeviceBean
import com.yucheng.ycbtsdk.response.BleConnectResponse
import com.yucheng.ycbtsdk.response.BleScanResponse


actual class BluetoothManager(private val context: Context) {

    actual val logger = PlatformLogger()

    private val adapter = BluetoothAdapter.getDefaultAdapter()
    private val foundDevices = mutableMapOf<String, ScannedDevice>()

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    actual fun startScan(onResult: (List<ScannedDevice>) -> Unit) {
        foundDevices.clear()
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val device: BluetoothDevice? =
                    intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                device?.let {
                    val address = it.address
                    val name = try {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                            == PackageManager.PERMISSION_GRANTED
                        ) {
                            it.name ?: "Unknown Device"
                        } else {
                            "Permission Denied"
                        }
                    } catch (e: SecurityException) {
                        "Permission Error"
                    }
                    if (!foundDevices.containsKey(address)) {
                        val scanned = ScannedDevice(name, address)
                        foundDevices[address] = scanned
                        onResult(foundDevices.values.toList())
                    }
                }
            }
        }

        context.registerReceiver(receiver, filter)
        adapter?.startDiscovery()
    }

    actual fun connect(address: String, onConnected: (Boolean) -> Unit) {
        logger.log("Attempting to connect to device: $address")
        // Simulate connection success
        onConnected(true)
    }

    actual fun disconnect() {
        logger.log("Disconnecting from BLE device...")
        // Simulate disconnect
    }
}