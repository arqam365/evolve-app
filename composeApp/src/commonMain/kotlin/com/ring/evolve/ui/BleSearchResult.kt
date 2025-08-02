package com.ring.evolve.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ring.evolve.utils.BleConnectivity.BleSupportInterface
import org.koin.compose.koinInject

@Composable
fun BleSearch(){

    val bleSupport: BleSupportInterface = koinInject()

    Box(modifier=Modifier.fillMaxSize()){
        Column {
            Button(onClick = {
                bleSupport.startScan()
            }){
                Text("Search For Devices")
            }
        }
    }
}