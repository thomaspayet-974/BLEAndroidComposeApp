package com.thomaspayet.bleapp.screens.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thomaspayet.bleapp.R
import com.thomaspayet.bleapp.data.ble.BluetoothLEManager
import com.thomaspayet.bleapp.ui.components.ListElement

@SuppressLint("MissingPermission")
@Composable
fun BluetoothLEDeviceDetailScreen(
    bleViewModel : BluetoothLEViewModel = viewModel(),
    deviceAddr: String = stringResource(R.string.default_ble_device_address),
    isDeviceConnected: Boolean = BluetoothLEManager.currentDevice != null,
    onClickDisconnect: () -> Unit = {}
) {
    val scannedDevices = bleViewModel.scanItemsFlow.collectAsStateWithLifecycle()
    val selectedDevice = scannedDevices.value.find {
        it.device.address == deviceAddr
    }?.device
    if (selectedDevice == null) {
        onClickDisconnect()
    } else {
        BluetoothLEDeviceDetailContent(
            deviceName = selectedDevice.name ?: stringResource(R.string.unknown_ble_device),
            deviceAddress = selectedDevice.address,
            isDeviceConnected = isDeviceConnected,
            onClickDisconnect = onClickDisconnect
        )
    }
}

@Composable
private fun BluetoothLEDeviceDetailContent(
    deviceName: String = "N/A",
    deviceAddress: String,
    isDeviceConnected: Boolean,
    onClickDisconnect: () -> Unit
) {
    Column {
        //TODO: ajouter des widgets pour chaque Characteristic
        ListElement(
            title = deviceName,
            content = deviceAddress,
            image =
                if (isDeviceConnected) Icons.Filled.BluetoothConnected
                else Icons.Filled.BluetoothDisabled,
            onClick = onClickDisconnect
        )
        Button(
            onClick = onClickDisconnect,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Image(
                    imageVector = Icons.Filled.BluetoothDisabled,
                    contentDescription = stringResource(R.string.disconnect_ble_device)
                )
                Text(text = stringResource(R.string.disconnect_button))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BluetoothLEDeviceDetailScreenPreview() {
    BluetoothLEDeviceDetailContent(
        deviceName = "Bluetooth device",
        deviceAddress = "11:22:33:AA:BB:CC",
        isDeviceConnected = true,
        onClickDisconnect = {}
    )
}