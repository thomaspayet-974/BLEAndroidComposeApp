package com.thomaspayet.bleapp.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.util.Log
import java.util.UUID

class BluetoothLEManager {

    companion object {
        var currentDevice: BluetoothDevice? = null

        /**
         * UUIDs are unique IDs that allow to identify services and characteristics.
         * Those UUIDs are defined in the code of the advertising device (here ESP32).
         */
        // TODO: Modifier les UUIDS pour correspondre aux besoins
        val DEVICE_UUID: UUID =
            UUID.fromString("795090c7-420d-4048-a24e-18e60180e23c")
        val CHARACTERISTIC_TOGGLE_LED_UUID: UUID =
            UUID.fromString("59b6bf7f-44de-4184-81bd-a0e3b30c919b")
        val CHARACTERISTIC_NOTIFY_STATE: UUID =
            UUID.fromString("d75167c8-e6f9-4f0b-b688-09d96e195f00")
        val CHARACTERISTIC_GET_COUNT: UUID =
            UUID.fromString("a877d87f-60bf-4ad5-ba61-56133b2cd9d4")
        val CHARACTERISTIC_GET_SET_WIFI: UUID =
            UUID.fromString("10f83060-64f8-11ee-8c99-0242ac120002")
        val CHARACTERISTIC_SET_DEVICE_NAME: UUID =
            UUID.fromString("1497b8a8-64f8-11ee-8c99-0242ac120002")
        val CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID: UUID =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    }

    /**
     * Definition of GattCallback class that allow to manage different BLE events.
     * It extends BluetoothGattCallback given by Android.
     */
    open class GattCallback(
        val onConnect: () -> Unit,
        val onNotify: (characteristic: BluetoothGattCharacteristic, value: String) -> Unit,
        val onDisconnect: () -> Unit
    ) : BluetoothGattCallback() {

        /**
         * This method is called when services has been discovered.
         */
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                onConnect()
            } else {
                onDisconnect()
            }
        }

        /**
         * This method is called whenever the state changes in the BLE stack.
         */
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> gatt.discoverServices()
                BluetoothProfile.STATE_DISCONNECTED -> onDisconnect()
            }
        }

        /**
         * Method called when a characteristic has been modified.
         */
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            onNotify(characteristic, value.toString(Charsets.UTF_8))
        }

        /**
         * This method is deprecated, prefer using
         * onCharacteristicChanged(
         *     gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic,
         *     value: ByteArray
         * )
         *
         * Old method called when a characteristic has been modified.
         */
        @Deprecated(
            "Deprecated",
            ReplaceWith("super.onCharacteristicChanged(gatt, characteristic, value)"),
            level = DeprecationLevel.WARNING
        )
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            onNotify(
                characteristic,
                characteristic.value.toString(Charsets.UTF_8)
            )
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(
                    "BLE",
                    "Descriptor write successful for ${descriptor?.characteristic?.uuid}"
                )
            } else {
                Log.e(
                    "BLE",
                    "Descriptor write failed for ${descriptor?.characteristic?.uuid},"
                            + "status: $status"
                )
            }
        }
    }
}