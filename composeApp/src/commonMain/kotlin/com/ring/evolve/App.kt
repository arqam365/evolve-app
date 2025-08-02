package com.ring.evolve

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.ring.evolve.ui.BleScreen
import com.ring.evolve.ui.BleSearch

@Composable
fun App() {
    MaterialTheme {
        BleSearch()
    }
}