package com.ring.evolve.utils.AndroidBleSupport

import android.util.Log
import com.google.gson.Gson
import com.ring.evolve.utils.BleConnectivity.BleSupportInterface
import com.ring.evolve.utils.BleConnectivity.ScanDevice
import com.yucheng.ycbtsdk.Constants
import com.yucheng.ycbtsdk.YCBTClient
import com.yucheng.ycbtsdk.bean.ScanDeviceBean
import com.yucheng.ycbtsdk.response.BleConnectResponse
import com.yucheng.ycbtsdk.response.BleDataResponse
import com.yucheng.ycbtsdk.response.BleDeviceToAppDataResponse
import com.yucheng.ycbtsdk.response.BleRealDataResponse
import com.yucheng.ycbtsdk.response.BleScanResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


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

    override fun disconnect() {
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

        awaitClose {

        }
    }

    override fun startEcg(hand: Int) {
        val hand= if (hand==0) Constants.HandWear.Left else Constants.HandWear.Right
        YCBTClient.settingHandWear(hand, object: BleDataResponse{
            override fun onDataResponse(
                p0: Int,
                p1: Float,
                p2: HashMap<*, *>?,
            ) {
                if (p0==0)
                    Log.d("Android Hand Setup","Left Hand")
                else Log.d("Android Hand Setup","Left Hand")

            }
        })

        YCBTClient.appEcgTestStart( object : BleDataResponse {
            override fun onDataResponse(
                p0: Int,
                p1: Float,
                hashMap:  java.util.HashMap<*, *>?,
            ) {
                Log.d("ECG TEST" ,"Started")
                Log.d("ECG TEST" ,"Started")
                Log.d("ECG TEST" ,"Started")
                Log.d("ECG TEST" ,"${hashMap}")

                if (hashMap != null) {
                    when (p0) {
                        Constants.DATATYPE.Real_UploadECG -> {
                            val data = hashMap["data"] as? List<Int> ?: return
                            Log.d("ECG_DATA", "ECG Data: $data")
                        }

                        Constants.DATATYPE.Real_UploadECGHrv -> {
                            val hrv = (hashMap["data"] as? Float) ?: 0f
                            Log.d("ECG_HRV", "HRV: $hrv")
                        }

                        Constants.DATATYPE.Real_UploadECGRR -> {
                            val param = hashMap["data"] as? Float ?: 0f
                            Log.d("ECG_RR", "RR Interval: $param")
                        }

                        Constants.DATATYPE.Real_UploadBlood -> {
                            val heart = (hashMap["heartValue"] as? Int) ?: 0
                            val dbp = (hashMap["bloodDBP"] as? Int) ?: 0
                            val sbp = (hashMap["bloodSBP"] as? Int) ?: 0
                            val hrv = (hashMap["hrv"] as? Int) ?: 0

                            Log.d("ECG_BLOOD", "Heart: $heart, DBP: $dbp, SBP: $sbp, HRV: $hrv")
                        }

                        Constants.DATATYPE.AppECGPPGStatus -> {
                            Log.d("ECG_PPG", "PPG status received: $hashMap")
                        }

                        else -> {
                            Log.d("ECG_UNKNOWN", "Unknown type ($p0): $hashMap")
                        }
                    }
                }
            }

        },object : BleRealDataResponse {
            override fun onRealDataResponse(
                p0: Int,
                hashMap:  java.util.HashMap<*, *>?,
            ) {
                Log.d("ECG TEST" ,"${hashMap}")

                if (hashMap != null) {
                    when (p0) {
                        Constants.DATATYPE.Real_UploadECG -> {
                            val data = hashMap["data"] as? List<Int> ?: return
                            Log.d("ECG_DATA", "ECG Data: $data")
                        }

                        Constants.DATATYPE.Real_UploadECGHrv -> {
                            val hrv = (hashMap["data"] as? Float) ?: 0f
                            Log.d("ECG_HRV", "HRV: $hrv")
                        }

                        Constants.DATATYPE.Real_UploadECGRR -> {
                            val param = hashMap["data"] as? Float ?: 0f
                            Log.d("ECG_RR", "RR Interval: $param")
                        }

                        Constants.DATATYPE.Real_UploadBlood -> {
                            val heart = (hashMap["heartValue"] as? Int) ?: 0
                            val dbp = (hashMap["bloodDBP"] as? Int) ?: 0
                            val sbp = (hashMap["bloodSBP"] as? Int) ?: 0
                            val hrv = (hashMap["hrv"] as? Int) ?: 0

                            Log.d("ECG_BLOOD", "Heart: $heart, DBP: $dbp, SBP: $sbp, HRV: $hrv")
                        }

                        Constants.DATATYPE.AppECGPPGStatus -> {
                            Log.d("ECG_PPG", "PPG status received: $hashMap")
                        }

                        else -> {
                            Log.d("ECG_UNKNOWN", "Unknown type ($p0): $hashMap")
                        }
                    }
                }
            }

        })
    }

    override fun stopEcg() {
        YCBTClient.appEcgTestEnd(object:BleDataResponse{
            override fun onDataResponse(p0: Int, p1: Float, p2: HashMap<*, *>?) {
                Log.d("Stop ECG", "ECG Stopped Successfully")
            }
        })
    }

    override fun startHeartRateMonitoring() {

        Log.d("Heart Rate", "Starting Capture")
        YCBTClient.settingHeartMonitor(0x00,30, object : BleDataResponse {
            override fun onDataResponse(p0: Int, p1: Float, p2: java.util.HashMap<*, *>?) {
                Log.d("Heart Rate", p2.toString())
            }
        })
    }

    override fun registerRealTimeData() {
        YCBTClient.appRegisterRealDataCallBack(object : BleRealDataResponse {
            override fun onRealDataResponse(p0: Int, p1: java.util.HashMap<*, *>?) {
                Log.d("Real Time Data", "${p0}, ${p1}")
            }

        })
        //Enable Access
        YCBTClient.appRealDataFromDevice(0x01, 0x00, object : BleDataResponse {
            override fun onDataResponse(
                p0: Int,
                p1: Float,
                p2: java.util.HashMap<*, *>?,
            ) {
                Log.d("Real Data Enabled", "${p0}, ${p1}, ${p2}")

            }

        })

    }

    override fun getHealthData() {
        YCBTClient.healthHistoryData(Constants.DATATYPE.Health_HistoryHeart, object : BleDataResponse{
            override fun onDataResponse(
                p0: Int,
                p1: Float,
                p2: java.util.HashMap<*, *>?,
            ) {
                    Log.d("Health Data", "Code: $p0, Value: $p1, Data: $p2 Health Data: ${Gson().toJson(p2)}")
            }

        })
    }

    override fun startHeartRateMeasurement() {

        YCBTClient.appRegisterRealDataCallBack(object : BleRealDataResponse {
            override fun onRealDataResponse(dataType: Int, dataMap: HashMap<*, *>?) {
                if (dataType == Constants.DATATYPE.Real_UploadHeart) {
                    val heartRate = dataMap?.get("heartValue") as? Int
                    Log.d("HeartRate", "Measured heart rate: $heartRate bpm")
                }
            }
        })

        YCBTClient.appStartMeasurement(
            1,
            0x00,
            object : BleDataResponse {
                override fun onDataResponse(code: Int, value: Float, map: HashMap<*, *>?) {
                    if (code == 0) {
                        Log.d("HeartRate", "Heart rate measurement started successfully.")
                    } else {
                        Log.e("HeartRate", "Failed to start measurement. Code: $code")
                    }
                }
            }
        )

        YCBTClient.deviceToApp(object : BleDeviceToAppDataResponse {
            override fun onDataResponse(i: Int, dataMap: HashMap<*, *>?) {
                if (i == 0 && dataMap != null && dataMap["dataType"] == Constants.DATATYPE.DeviceMeasurementResult) {
                    val resultData = dataMap["datas"] as? ByteArray
                    when (resultData?.get(1)?.toInt()) {
                        0 -> Log.d("HeartRate", "User exited measurement.")
                        1 -> Log.d("HeartRate", "Heart rate measurement completed.")
                        2 -> Log.e("HeartRate", "Heart rate measurement failed.")
                    }
                }
            }
        })
    }

    override fun startBloodPressure() {

        YCBTClient.appRegisterRealDataCallBack(object : BleRealDataResponse {
            override fun onRealDataResponse(dataType: Int, dataMap: HashMap<*, *>?) {
                if (dataType == Constants.DATATYPE.Real_UploadBlood) {
                    val SBP = dataMap?.get("bloodSBP").toString()
                    val DBP = dataMap?.get("bloodDBP").toString()
                    Log.d("SystolicBP", "Blood Pressure Systolic: $SBP")
                    Log.d("SystolicBP", "Blood Pressure Diastolic: $DBP")
                }
            }
        })

        YCBTClient.appStartMeasurement(
            1,
            0x01,
            object : BleDataResponse {
                override fun onDataResponse(code: Int, value: Float, map: HashMap<*, *>?) {
                    if (code == 0) {
                        Log.d("Blood Pressure", "Blood Pressure measurement started successfully.")
                    } else {
                        Log.e("Blood Pressure", "Failed to start measurement. Code: $code")
                    }
                }
            }
        )

        YCBTClient.deviceToApp(object : BleDeviceToAppDataResponse {
            override fun onDataResponse(i: Int, dataMap: HashMap<*, *>?) {
                if (i == 0 && dataMap != null && dataMap["dataType"] == Constants.DATATYPE.DeviceMeasurementResult) {
                    val resultData = dataMap["datas"] as? ByteArray
                    when (resultData?.get(1)?.toInt()) {
                        0 -> Log.d("Blood Pressure", "User exited measurement.")
                        1 -> Log.d("Blood Pressure", "Blood Pressure measurement completed.")
                        2 -> Log.e("Blood Pressure", "Blood Pressure measurement failed.")
                    }
                }
            }
        })
    }


}