package com.ring.evolve.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.dp
import com.ring.evolve.utils.BleConnectivity.BleSupportInterface
import com.ring.evolve.utils.BleConnectivity.ScanDevice
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun BleSearch(){

    val bleSupport: BleSupportInterface = koinInject()

    var devices by remember { mutableStateOf(emptyList<ScanDevice>()) }

    var connectionStatus = bleSupport.connectionStatus().collectAsState("")

    var steps by remember { mutableStateOf("") }
    var distances by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }

    var continuousTemperature by remember { mutableStateOf("") }
    var bloodGlucose by remember { mutableStateOf("") }
    var uricAcid by remember { mutableStateOf("") }

    val EcgList = remember { mutableStateListOf<Int>() }

    var showEcgScreen by remember { mutableStateOf(false) }



    Box(modifier=Modifier.fillMaxSize().padding(20.dp)){
        Column (verticalArrangement = Arrangement.spacedBy(8.dp)){
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)){
                Button(onClick = {
                    bleSupport.startScan() {
                        devices = devices + it
                    }
                }) {
                    Text("Search For Devices")
                }

                Text(connectionStatus.value)
            }

            Button(onClick = {
                bleSupport.disconnect()
            }) {
                Text("Disconnect")
            }

            devices.forEach { it->
                Row(modifier = Modifier.padding(8.dp)
                    .clickable(onClick = {bleSupport.connect(it.mac){status->
                    } })){
                    Text(text=it.name)
                    Text(text=it.mac)
                }
            }

            var showHand by remember { mutableStateOf(false) }

            Row{
                Button(onClick = {
                    bleSupport.startHeartRateMonitoring()
                })
                {
                    Text("Start Heart Rate Monitoring")
                }
                Spacer(modifier=Modifier.width(40.dp))
                Button(onClick = {
                    bleSupport.getHealthData()
                })
                {
                    Text("Get")
                }

            }


            Button(onClick = { bleSupport.startHeartRateMeasurement()})
            {
                Text("Get Heart Data")
            }
            Button(onClick = { bleSupport.startBloodPressure()})
            {
                Text("Blood Pressure")
            }
            Button(onClick = { bleSupport.getBloodOxygen()})
            {
                Text("Blood SpO2")
            }
            Row{
                Button(onClick = { bleSupport.getTemperature() })
                {
                    Text("Temperature")
                }
                Spacer(modifier=Modifier.width(40.dp))
                Button(onClick = { bleSupport.startTemperatureMonitoring() })
                {
                    Text("Temperature Monitoring Start")
                }
            }
            Row{
                Button(onClick = { bleSupport.getContinuousTemperature(){
                    continuousTemperature=it
                } })
                {
                    Text("Continuous Temperature")
                }
                Spacer(modifier=Modifier.width(40.dp))
                Text(continuousTemperature)
            }
            Button(onClick = { bleSupport.enableRealData(){step,distance,calorie->
                steps = step
                distances = distance
                calories = calorie
            } })
            {
                Text("Real Time Data")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                Text("Steps: $steps")
                Text("Distance: $distances")
                Text("Calories: $calories")
            }

            Row{
                Button(onClick = {
                    bleSupport.getBloodGlucose {
                        bloodGlucose=it
                    }
                })
                {
                    Text("Blood Glucose")
                }
                Spacer(modifier=Modifier.width(40.dp))
                Text(bloodGlucose)
            }
            Row{
                Button(onClick = {
                    bleSupport.getUricAcid {
                        uricAcid=it
                    }
                })
                {
                    Text("Uric Acid")
                }
                Spacer(modifier=Modifier.width(40.dp))
                Text(uricAcid)
            }

            Row{
                Button(onClick = { showHand = !showHand })
                {
                    Text("Start ECG")
                }
                Spacer(modifier=Modifier.width(40.dp))
                Button(onClick = {bleSupport.stopEcg()})
                {
                    Text("Stop ECG")
                }
            }
            if (showHand)
            {
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Button(onClick = {
                        showEcgScreen=true
                        bleSupport.startEcg(0){
                            EcgList.addAll(it)
                            if (EcgList.size > 1000) {
                                EcgList.removeRange(0, EcgList.size - 1000)
                            }
                        }
                    }){
                        Text("Left Hand")
                    }
                    Button(onClick = {
                        showEcgScreen=true
                        bleSupport.startEcg(1){
                            EcgList.addAll(it)
                            if (EcgList.size > 1000) {
                                EcgList.removeRange(0, EcgList.size - 1000)
                            }
                        }
                    }){
                        Text("Right Hand")
                    }
                }
            }
        }

        if (showEcgScreen)
            EcgScreen(ecgList = EcgList, hideScreen = {showEcgScreen=false})
    }
}

@Composable
fun EcgScreen(ecgList: SnapshotStateList<Int>, hideScreen: () -> Unit) {

    val maxPoints = 1000

    val visibleData = ecgList
        .takeLast(maxPoints)
        .map { value ->
            when {
                value >= 20000 || value <= -20000 -> null // skip invalid data
                else -> value.toFloat()
            }
        }

    val isSignalLost = visibleData.count { it == null } > maxPoints * 0.6

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(onClick = { hideScreen() }) {
                    Text("Hide Screen")
                }
            }

            if (isSignalLost) {
                Text(
                    text = "\u26A0\uFE0F ECG Contact Lost",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Canvas(
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .background(Color.Gray)
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val path = Path()
                val validPoints = visibleData.filterNotNull()
                val pointCount = validPoints.size.coerceAtLeast(1)
                val stepX = size.width / pointCount
                val midY = size.height / 2
                val maxAmplitude = 30000f

                validPoints.forEachIndexed { index, value ->
                    val x = index * stepX
                    val y = midY - (value / maxAmplitude).coerceIn(-1f, 1f) * midY
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }

                drawPath(
                    path = path,
                    color = Color.Green,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}