package com.ring.evolve.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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

            devices.forEach { it->
                Row(modifier = Modifier.padding(8.dp)
                    .clickable(onClick = {bleSupport.connect(it.mac){status->
                    } })){
                    Text(text=it.name)
                    Text(text=it.mac)
                }
            }
        }
    }
}