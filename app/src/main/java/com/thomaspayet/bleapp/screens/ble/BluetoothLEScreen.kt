package com.thomaspayet.bleapp.screens.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thomaspayet.bleapp.ApplicationRoot
import com.thomaspayet.bleapp.data.ble.BluetoothLEManager
import com.thomaspayet.bleapp.ui.components.ListElement

@SuppressLint("MissingPermission")
@Composable
fun BluetoothLEScreen(
    modifier: Modifier = Modifier,
    bleViewModel: BluetoothLEViewModel = viewModel(),
    onClickElementList: (BluetoothDevice) -> Unit = {},
    onConnectedDevice: (BluetoothDevice?) -> Unit = {}
) {
    // List of scanned device
    val scanItems by bleViewModel.scanItemsFlow.collectAsStateWithLifecycle()
    // Scanning state
    val isScanning by bleViewModel.isScanningFlow.collectAsStateWithLifecycle()
    // Connection state
    val isConnected by bleViewModel.isConnectedToDeviceFlow.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Buttons to make ble actions (scan, clear list ...)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 5.dp)
        ) {
            Button(
                colors =
                    if (isScanning) ButtonDefaults.buttonColors(containerColor = Color.Red)
                    else ButtonDefaults.buttonColors(),
                onClick = { bleViewModel.startScan() },
                enabled = !isScanning
            ) {
                if (isScanning) Text(text = "Scan en cours") else Text(text = "Débuter le scan")
            }
            Spacer(modifier = Modifier.padding(5.dp))
            Button(onClick = { bleViewModel.clearScanItems() }) {
                Text(text = "Vider la liste")
            }
        }

        // Display devices found with scan
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                scanItems
                    .filter {
                        !it.device.name.isNullOrBlank() and !it.device.address.isNullOrBlank()
                    }
                    .sortedBy {
                        it.scanRecord?.deviceName ?: it.device.name ?: it.device.address
                    }
            ) { item ->
                ListElement(
                    title = item.device.name,
                    content = item.device.address,
                    image = Icons.Filled.Bluetooth,
                    onClick = {
                        onClickElementList(item.device)
                    }
                )
                if (isConnected) {
                    onConnectedDevice(BluetoothLEManager.currentDevice)
                }
            }
        }
    }
}
