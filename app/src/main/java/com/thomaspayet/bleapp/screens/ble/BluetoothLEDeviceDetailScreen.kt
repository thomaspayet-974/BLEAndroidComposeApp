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
import androidx.compose.ui.tooling.preview.Preview
import com.thomaspayet.bleapp.data.ble.BluetoothLEManager
import com.thomaspayet.bleapp.ui.components.ListElement

@SuppressLint("MissingPermission")
@Composable
fun BluetoothLEDeviceDetailScreen(
    device: BluetoothDevice? = BluetoothLEManager.currentDevice,
    isDeviceConnected: Boolean = BluetoothLEManager.currentDevice != null,
    onClickDisconnect: () -> Unit = {}
) {
    Column {
        //TODO: ajouter des widgets pour chaque Characteristic
        ListElement(
            title = device?.name ?: "Unknown device",
            content = device?.address ?: "00:00:00:00:00:00",
            image =
                if(isDeviceConnected) Icons.Filled.BluetoothConnected
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
                    contentDescription = "Disconnect BluetoothLE device"
                )
                Text(text = "Disconnect")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BluetoothLEDeviceDetailScreenPreview() {
    BluetoothLEDeviceDetailScreen()
}