package com.thomaspayet.bleapp.screens.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thomaspayet.bleapp.data.ble.BluetoothLEManager
import com.thomaspayet.bleapp.ui.components.ListElement
import com.thomaspayet.bleapp.R

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

    BluetoothLEScreenContent(
        modifier = modifier,
        scanItems = scanItems,
        isScanning = isScanning,
        isConnected = isConnected,
        onStartScan = {
            bleViewModel.clearScanItems() // Clear list before new scan
            bleViewModel.startScan()
        },
        onClearScan = { bleViewModel.clearScanItems() },
        onClickElementList = onClickElementList,
        onConnectedDevice = onConnectedDevice
    )
}

@SuppressLint("MissingPermission")
@Composable
private fun BluetoothLEScreenContent(
    modifier: Modifier = Modifier,
    scanItems: List<ScanResult>,
    isScanning: Boolean,
    isConnected: Boolean,
    onStartScan: () -> Unit,
    onClearScan: () -> Unit,
    onClickElementList: (BluetoothDevice) -> Unit,
    onConnectedDevice: (BluetoothDevice?) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isScanning) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        // Buttons to make ble actions (scan, clear list ...)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 5.dp)
        ) {
            Button(
                colors =
                    if (isScanning) ButtonDefaults.buttonColors(containerColor = Color.Red)
                    else ButtonDefaults.buttonColors(),
                onClick = onStartScan,
                enabled = !isScanning
            ) {
                Text(
                    text = if (isScanning) stringResource(R.string.start_scan_button_scanning)
                    else stringResource(R.string.start_scan_button)
                )
            }
            Spacer(modifier = Modifier.padding(5.dp))
            Button(onClick = onClearScan) {
                Text(text = stringResource(R.string.clear_scan_button))
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

@Preview(showBackground = true)
@Composable
fun BluetoothLEScreenNotScanningPreview() {
    MaterialTheme {
        BluetoothLEScreenContent(
            scanItems = emptyList(),
            isScanning = false,
            isConnected = false,
            onStartScan = {},
            onClearScan = {},
            onClickElementList = {},
            onConnectedDevice = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BluetoothLEScreenScanningPreview() {
    MaterialTheme {
        BluetoothLEScreenContent(
            scanItems = emptyList(),
            isScanning = true,
            isConnected = false,
            onStartScan = {},
            onClearScan = {},
            onClickElementList = {},
            onConnectedDevice = {}
        )
    }
}
