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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BleAndroid: BleSupportInterface
{

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

        YCBTClient.settingDataCollect(0x01,0x03,90,60,object : BleDataResponse{
            override fun onDataResponse(
                p0: Int,
                p1: Float,
                p2: java.util.HashMap<*, *>?,
            ) {
                Log.d ("Config Set","$p0, $p1, $p2")
            }

        })

        YCBTClient.appRegisterRealDataCallBack(object : BleRealDataResponse {
            override fun onRealDataResponse(p0: Int, p1: java.util.HashMap<*, *>?) {
                Log.d("Real Time Heart Data", "${p0}, ${p1}")
            }

        })

        Log.d("Heart Rate", "Starting Capture")
        YCBTClient.settingHeartMonitor(0x01,60, object : BleDataResponse {
            override fun onDataResponse(p0: Int, p1: Float, p2: java.util.HashMap<*, *>?) {
                Log.d("Heart Rate", p0.toString())
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
        YCBTClient.healthHistoryData(Constants.DATATYPE.Health_HistoryAll, object : BleDataResponse{
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


    override fun getBloodOxygen() {

        YCBTClient.appRegisterRealDataCallBack(object : BleRealDataResponse {
            override fun onRealDataResponse(dataType: Int, dataMap: HashMap<*, *>?) {
                if (dataType == Constants.DATATYPE.Real_UploadBloodOxygen) {
                    val bloodOxygen = dataMap?.get("bloodOxygenValue") as Int
                    Log.d("BloodOxygen", "Blood Oxygen Value: $bloodOxygen")
                }
            }
        })

        YCBTClient.appStartMeasurement(
            1,
            0x02,
            object : BleDataResponse {
                override fun onDataResponse(code: Int, value: Float, map: HashMap<*, *>?) {
                    if (code == 0) {
                        Log.d("BloodOxygen", "Blood Oxygen measurement started successfully.")
                    } else {
                        Log.e("BloodOxygen", "Blood Oxygen to start measurement. Code: $code")
                    }
                }
            }
        )

        YCBTClient.deviceToApp(object : BleDeviceToAppDataResponse {
            override fun onDataResponse(i: Int, dataMap: HashMap<*, *>?) {
                if (i == 0 && dataMap != null && dataMap["dataType"] == Constants.DATATYPE.DeviceMeasurementResult) {
                    val resultData = dataMap["datas"] as? ByteArray
                    when (resultData?.get(1)?.toInt()) {
                        0 -> Log.d("BloodOxygen", "User exited measurement.")
                        1 -> Log.d("BloodOxygen", "Blood Oxygen measurement completed.")
                        2 -> Log.e("BloodOxygen", "BloodOxygen measurement failed.")
                    }
                }
            }
        })
    }


    override fun getTemperature() {

        YCBTClient.appRegisterRealDataCallBack(object : BleRealDataResponse {
            override fun onRealDataResponse(dataType: Int, dataMap: HashMap<*, *>?) {
                if (dataType == Constants.DATATYPE.Real_UploadComprehensive) {
                    val tempInt = dataMap?.get("tempInteger") as Int
                    val tempFloat = dataMap?.get("tempFloat") as Int
                    val totalTemp= "$tempInt.$tempFloat".toDouble()
                    Log.d("Temperature", "Body Temperature: $totalTemp")
                }
            }
        })

        YCBTClient.appStartMeasurement(
            1,
            0x04,
            object : BleDataResponse {
                override fun onDataResponse(code: Int, value: Float, map: HashMap<*, *>?) {
                    if (code == 0) {
                        Log.d("Temperature", "Temperature measurement started successfully.")
                    } else {
                        Log.e("Temperature", "Temperature to start measurement. Code: $code")
                    }
                }
            }
        )

        YCBTClient.deviceToApp(object : BleDeviceToAppDataResponse {
            override fun onDataResponse(i: Int, dataMap: HashMap<*, *>?) {
                if (i == 0 && dataMap != null && dataMap["dataType"] == Constants.DATATYPE.DeviceMeasurementResult) {
                    val resultData = dataMap["datas"] as? ByteArray
                    when (resultData?.get(1)?.toInt()) {
                        0 -> Log.d("Temperature", "User exited measurement.")
                        1 -> Log.d("Temperature", "Temperature measurement completed.")
                        2 -> Log.e("Temperature", "Temperature measurement failed.")
                    }
                }
            }
        })
    }

    override fun enableRealData(changeData:(String,String,String)->Unit){

        YCBTClient.appRegisterRealDataCallBack(object:BleRealDataResponse{
            override fun onRealDataResponse(p0: Int, p1: java.util.HashMap<*, *>?) {
                Log.d("Real Time Data", "${p0}, ${p1}")
                changeData("${p1?.get("sportStep")}","${p1?.get("sportDistance")}", "${p1?.get("sportCalorie")}")
            }

        })

        YCBTClient.appRealDataFromDevice(1, 0x00, object: BleDataResponse{
            override fun onDataResponse(
                p0: Int,
                p1: Float,
                p2: java.util.HashMap<*, *>?,
            ) {
                Log.d("Real Data Enabled", "${p0}, ${p1}, ${p2}")
            }

        })
    }

    override fun getBloodGlucose(onDataUpdate:(String)-> Unit) {

        YCBTClient.appRegisterRealDataCallBack(object : BleRealDataResponse {
            override fun onRealDataResponse(dataType: Int, dataMap: HashMap<*, *>?) {
                Log.d("Blood Glucose", "$dataType, ${dataMap}")
                if (dataType == Constants.DATATYPE.Real_UploadComprehensive) {
                    val bloodGlucose = dataMap?.get("bloodSugar") as Int
                    Log.d("Blood Glucose", "Blood Glucose Value: $bloodGlucose")
                    onDataUpdate(bloodGlucose.toString())
                }
            }
        })

        YCBTClient.appStartMeasurement(
            1,
            0x05,
            object : BleDataResponse {
                override fun onDataResponse(code: Int, value: Float, map: HashMap<*, *>?) {
                    if (code == 0) {
                        Log.d("Blood Glucose", "Blood Glucose measurement started successfully.")
                    } else {
                        Log.e("Blood Glucose", "Blood Glucose to start measurement. Code: $code")
                    }
                }
            }
        )

        YCBTClient.deviceToApp(object : BleDeviceToAppDataResponse {
            override fun onDataResponse(i: Int, dataMap: HashMap<*, *>?) {
                if (i == 0 && dataMap != null && dataMap["dataType"] == Constants.DATATYPE.DeviceMeasurementResult) {
                    val resultData = dataMap["datas"] as? ByteArray
                    when (resultData?.get(1)?.toInt()) {
                        0 -> Log.d("Blood Glucose", "User exited measurement.")
                        1 -> Log.d("Blood Glucose", "Blood Glucose measurement completed.")
                        2 -> Log.e("Blood Glucose", "Blood Glucose measurement failed.")
                    }
                }
            }
        })
    }
    override fun getUricAcid(onDataUpdate:(String)-> Unit) {

        val isSupported= YCBTClient.isSupportFunction(Constants.FunctionConstant.ISHASURICACIDMEASUREMENT)
        val isSupportedRes= YCBTClient.isSupportFunction(Constants.FunctionConstant.ISHASRESPIRATORYRATE)
        val isSupportedM= YCBTClient.isSupportFunction(Constants.FunctionConstant.ISHASIMPRECISEBLOODFAT)
        val isSupportedRealSleep= YCBTClient.isSupportFunction(Constants.FunctionConstant.ISHASSLEEP)
        Log.d("Uric Acid", "Uric Acid Supported: $isSupported")
        Log.d("Respiration Rate", "Respiratory Supported: $isSupportedRes")
        Log.d("Fat Data", "Fat Data Precision Supported: $isSupportedM")
        Log.d("Sleep Data", "Sleep Supported: $isSupportedRealSleep")
    }

    override fun getContinuousTemperature(updateData: (String) -> Unit) {

        val dataTypes = listOf(
            Constants.DATATYPE.Health_HistorySport,
            Constants.DATATYPE.Health_HistorySleep,
            Constants.DATATYPE.Health_HistoryHeart,
            Constants.DATATYPE.Health_HistoryBlood,
            Constants.DATATYPE.Health_HistoryAll,
            Constants.DATATYPE.Health_HistoryComprehensiveMeasureData
        )

        for (dataType in dataTypes) {
            YCBTClient.healthHistoryData(dataType, object : BleDataResponse {
                override fun onDataResponse(code: Int, ratio: Float, resultMap: HashMap<*, *>?) {
                    if (code != 0 || resultMap == null) {
                        Log.w("YCBT-History", "Failed or no data for type=$dataType, code=$code")
                        return
                    }

                    val json = Gson().toJson(resultMap)
                    Log.i("YCBT-History", "📦 DataType=$dataType\n$json")
                }
            })
        }


    }
    fun tempDataLoop() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(3000) // Adjust interval as needed
                withContext(Dispatchers.Main) {
                    YCBTClient.getRealTemp(object : BleDataResponse {
                        override fun onDataResponse(
                            p0: Int,
                            p1: Float,
                            p2: java.util.HashMap<*, *>?,
                        ) {
                            if (p0 == 0) {
                                Log.d("Temp Loop", "Temp data: $p2")
                            } else {
                                Log.w("Temp Loop", "Error code: $p0")
                            }
                        }
                    })
                }
            }
        }
    }


}