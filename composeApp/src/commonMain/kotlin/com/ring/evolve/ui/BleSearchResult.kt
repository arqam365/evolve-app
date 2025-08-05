package com.ring.evolve.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ring.evolve.utils.BleConnectivity.BleSupportInterface
import com.ring.evolve.utils.BleConnectivity.ScanDevice
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
            Button(onClick = { bleSupport.getTemperature()})
            {
                Text("Temperature")
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
                        bleSupport.startEcg(0)
                    }){
                        Text("Left Hand")
                    }
                    Button(onClick = {
                        bleSupport.startEcg(1)
                    }){
                        Text("Right Hand")
                    }
                }
            }
        }
    }
}