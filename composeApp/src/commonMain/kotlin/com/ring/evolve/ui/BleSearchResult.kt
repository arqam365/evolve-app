package com.ring.evolve.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ring.evolve.utils.BleConnectivity.BleSupportInterface
import com.ring.evolve.utils.BleConnectivity.ScanDevice
import org.koin.compose.koinInject

@Composable
fun BleSearch(){

    val bleSupport: BleSupportInterface = koinInject()

    var devices by remember { mutableStateOf(emptyList<ScanDevice>()) }


    Box(modifier=Modifier.fillMaxSize()){
        Column {
            Button(onClick = {
                bleSupport.startScan(){
                    devices=devices+it
                }
            }){
                Text("Search For Devices")
            }
        }
    }
}