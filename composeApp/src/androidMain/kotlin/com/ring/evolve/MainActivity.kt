package com.ring.evolve

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yucheng.ycbtsdk.BuildConfig
import com.yucheng.ycbtsdk.Constants
import com.yucheng.ycbtsdk.YCBTClient
import com.yucheng.ycbtsdk.bean.ScanDeviceBean
import com.yucheng.ycbtsdk.response.BleConnectResponse
import com.yucheng.ycbtsdk.response.BleDataResponse
import com.yucheng.ycbtsdk.response.BleScanResponse
import java.util.HashMap

class MainActivity : ComponentActivity() {

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }


    private val requestCode = 1001

    private fun checkAndRequestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val missing = permissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }

            if (missing.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, missing.toTypedArray(), requestCode)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)


        checkAndRequestBluetoothPermissions()

        YCBTClient.initClient(applicationContext,true, true)
        YCBTClient.settingTime(object : BleDataResponse{
            override fun onDataResponse(
                p0: Int,
                p1: Float,
                p2: HashMap<*, *>?,
            ) {
                Log.d("Time Set", "Code: $p0, Value: $p1, Data: $p2")
            }

        })

//        YCBTClient.startScanBle(object : BleScanResponse {
//            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
//            override fun onScanResponse(i: Int, scanDeviceBean: ScanDeviceBean?) {
//                if (scanDeviceBean != null) {
//                    Log.d("BEEEE", scanDeviceBean?.device?.name ?: "")
//                    Log.d("BEEEE", "Device Mac" + scanDeviceBean.deviceMac)
//                    YCBTClient.connectBle(scanDeviceBean.deviceMac, object : BleConnectResponse {
//                        override fun onConnectResponse(p0: Int) {
//                            Log.d("BEEEE", p0.toString())
//                            Constants.BLEState.Disconnect
//                        }
//                    });
//                }
//            }
//        }, 1000)
        setContent {
            App()
        }
    }
}