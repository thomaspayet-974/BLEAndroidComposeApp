package com.thomaspayet.bleapp.screens.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import com.thomaspayet.bleapp.ApplicationRoot
import com.thomaspayet.bleapp.data.ble.BluetoothLEManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class BluetoothLEViewModel : ViewModel() {
    /**
     * List of scanned ble devices
     */
    val scanItemsFlow = MutableStateFlow<List<ScanResult>>(emptyList())

    /**
     * State to indicate if we are currently scanning
     */
    val isScanningFlow = MutableStateFlow(false)

    /**
     * Scan job process
     */
    private var scanJob: Job? = null

    /**
     * BLE scan duration
     */
    private val scanDuration = 10000L

    /**
     * Bluetooth service manager
     */
    private val bluetoothLeManager by lazy {
        ApplicationRoot
            .getContext()
            .getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    /**
     * Bluetooth Low Energy Adapter
     */
    private val bluetoothLeAdapter = bluetoothLeManager.adapter

    /**
     * Bluetooth Low Energy Scanner
     */
    private val bluetoothLeScanner = bluetoothLeAdapter?.bluetoothLeScanner

    /**
     * Scan Filters that are applied directly on scan results
     */
    private val scanFilters: List<ScanFilter> = emptyList()

    /**
     * Scan settings
     */
    private val scanSettings =
        ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

    /**
     * Scan results set
     */
    private val scanResultsSet = mutableMapOf<String, ScanResult>()

    /**
     * current connected Bluetooth Gatt
     */
    private var currentBluetoothGatt : BluetoothGatt? = null

    /**
     * State to indicate if we are currently connecting to a device
     */
    val isConnectingFlow = MutableStateFlow(false)

    /**
     * State to indicate if we are currently connected to a device
     */
    val isConnectedToDeviceFlow = MutableStateFlow(false)

    /**
     * Called whenever a new scan result is available
     */
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            // Add the new ScanResult to set if not already present,
            // this returns null if it is not present else it returns the ScanResult.
            if (scanResultsSet.put(result.device.address, result) == null) {
                // Store the new list of scanned devices
                scanItemsFlow.value = scanResultsSet.values.toList()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        if(isScanningFlow.value) return

        scanJob = CoroutineScope(Dispatchers.IO).launch {
            // Scanning
            isScanningFlow.value = true

            // start scan with filters, settings and custom callbacks
            bluetoothLeScanner?.startScan(scanFilters, scanSettings, scanCallback)

            delay(scanDuration)

            // When the scan duration is over, stop scanning
            stopScan()
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        bluetoothLeScanner?.stopScan(scanCallback)
        // No more scanning
        isScanningFlow.value = false
        // Cancel the scan job
        scanJob?.cancel()
    }

    fun clearScanItems() {
        scanResultsSet.clear()
        scanItemsFlow.value = scanResultsSet.values.toList()
    }

    @SuppressLint("MissingPermission")
    fun connect(context: Context, bluetoothDevice: BluetoothDevice) {
        // Stop the scan.
        stopScan()

        // Indicate that we are connecting to a device.
        isConnectingFlow.value = true

        // Référencer l'appareil en cours de connexion
        BluetoothLEManager.currentDevice = bluetoothDevice

        // Try to connect to BLE device
        // Use the GattCallback to manage BLE events (connections, disconnections, notifications).
        currentBluetoothGatt = bluetoothDevice.connectGatt(
            context,
            false,
            BluetoothLEManager.GattCallback (
                // Connection successful
                onConnect = {
                    isConnectedToDeviceFlow.value = true
                    isConnectingFlow.value = false
                    // Here we can activate notifications
                    // TODO:
                    // enableNotify()
                },

                // New value received on a notification type characteristic.
                onNotify = { characteristic, value ->
                    when (characteristic.uuid) {
//                        BluetoothLEManager.CHARACTERISTIC_NOTIFY_STATE -> connectedDeviceLedStateFlow.value = value == "1"
                        // TODO: Implémenter les autres caractéristiques ici
                    }
                },

                // Disconnected from the ble device (BluetoothGatt.STATE_DISCONNECTED).
                onDisconnect = {
                    isConnectedToDeviceFlow.value = false
                    BluetoothLEManager.currentDevice = null
                }
            )
        )
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        // Try a disconnection
        isConnectedToDeviceFlow.value = false
        currentBluetoothGatt?.disconnect()
    }

    private fun getMainService(): BluetoothGattService? =
        currentBluetoothGatt?.getService(BluetoothLEManager.DEVICE_UUID)

    @SuppressLint("MissingPermission")
    private fun enableNotify() {
        getMainService()?.let { service ->
            // Indicate that the GATT Client is ready to listen to notifications
            val notificationStatus = service.getCharacteristic(BluetoothLEManager.CHARACTERISTIC_NOTIFY_STATE)
            val notificationLedCount = service.getCharacteristic(BluetoothLEManager.CHARACTERISTIC_GET_COUNT)
            val wifiScan = service.getCharacteristic(BluetoothLEManager.CHARACTERISTIC_GET_SET_WIFI)

            listOf(notificationStatus, notificationLedCount, wifiScan).forEach { characteristic ->
                currentBluetoothGatt?.setCharacteristicNotification(characteristic, true)
                characteristic.getDescriptor(BluetoothLEManager.CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID)?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        currentBluetoothGatt?.writeDescriptor(it, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                    } else {
                        it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        currentBluetoothGatt?.writeDescriptor(it)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun writeCharacteristic(uuid: UUID, value: String) {
        // Get main service
        getMainService()?.let { service ->
            // get characteristic from service
            val characteristic = service.getCharacteristic(uuid)

            if (characteristic == null) {
                Log.e(
                    "BluetoothLEManager",
                    "La caractéristique $uuid n'a pas été trouvée"
                )
                return
            }

            Log.i(
                "BluetoothLEManager",
                "Ecriture de la valeur $value dans la caractéristique $uuid"
            )

            // Use the method adapted to OS version to write the value into characteristic.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                currentBluetoothGatt?.writeCharacteristic(characteristic, value.toByteArray(),
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
            } else {
                characteristic.setValue(value)
                currentBluetoothGatt?.writeCharacteristic(characteristic)

            }
        }
    }

    // TODO: Add readCharacteristic function
}