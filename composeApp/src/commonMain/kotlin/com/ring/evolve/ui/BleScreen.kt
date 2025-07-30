package com.ring.evolve.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ring.evolve.viewmodel.BleViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BleScreen(viewModel: BleViewModel = koinInject()) {
    val devices by viewModel.devices.collectAsState()
    val connected by viewModel.connected.collectAsState()

    var isScanning by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isScanning = true
        viewModel.startScan()
        viewModel.loadLastDevice()
        isScanning = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bluetooth Devices") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Connected Device:", style = MaterialTheme.typography.titleMedium)
            Text(
                connected ?: "None",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    isScanning = true
                    viewModel.startScan()
                    isScanning = false
                },
                enabled = !isScanning,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(if (isScanning) "Scanning..." else "Refresh Devices")
            }

            Spacer(Modifier.height(16.dp))

            if (devices.isEmpty()) {
                Text("No devices found", style = MaterialTheme.typography.bodyMedium)
            } else {
                devices.forEach { device ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.connect(device.address) }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text("Name: ${device.name}")
                        Text("Address: ${device.address}", style = MaterialTheme.typography.bodySmall)
                        Divider(Modifier.padding(vertical = 4.dp))
                    }
                }

                if (connected != null) {
                    Button(
                        onClick = viewModel::disconnect,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Disconnect")
                    }
                }
            }
        }
    }
}