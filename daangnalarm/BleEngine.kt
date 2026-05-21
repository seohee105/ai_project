package com.yourteam.daangnalarm

import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.ParcelUuid
import androidx.core.content.ContextCompat

class BleEngine(private val context: Context) {
    var webSocketManager:WebSocketManager?=null

    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val scanner = bluetoothAdapter.bluetoothLeScanner
    private val advertiser = bluetoothAdapter.bluetoothLeAdvertiser

    var onDistanceUpdated: ((Double, DistanceZone) -> Unit)? = null

    private val kalmanFilter = KalmanFilter()

    enum class DistanceZone {
        FAR,
        ZONE_10,
        ZONE_5,
        ZONE_3,
        ZONE_1,
        ZONE_HALF
    }

    private fun classifyZone(distance: Double): DistanceZone {
        return when {
            distance >= 10.0 -> DistanceZone.FAR
            distance >= 5.0  -> DistanceZone.ZONE_10
            distance >= 3.0  -> DistanceZone.ZONE_5
            distance >= 1.0  -> DistanceZone.ZONE_3
            distance >= 0.5  -> DistanceZone.ZONE_1
            else             -> DistanceZone.ZONE_HALF
        }
    }

    fun startScan() {
        if (ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val filter = android.bluetooth.le.ScanFilter.Builder()
                .setServiceUuid(ParcelUuid.fromString("00002026-0000-1000-8000-00805f9b34fb"))
                .build()
            val settings = android.bluetooth.le.ScanSettings.Builder()
                .setScanMode(android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()
            scanner.startScan(listOf(filter), settings, scanCallback)
        }
    }

    fun stopScan() {
        if (ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            scanner.stopScan(scanCallback)
        }
    }

    fun startAdvertise() {
        if (ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.BLUETOOTH_ADVERTISE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false)
                .build()
            val data = AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(true)  // txPower 동적 포함
                .addServiceUuid(ParcelUuid.fromString("00002026-0000-1000-8000-00805f9b34fb"))
                .build()
            advertiser.startAdvertising(settings, data, advertiseCallback)
        }
    }

    fun stopAdvertise() {
        if (ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.BLUETOOTH_ADVERTISE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            advertiser.stopAdvertising(advertiseCallback)
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val rssi = result.rssi
            val txPower = result.scanRecord?.txPowerLevel ?: -59

            val smoothed = kalmanFilter.filter(rssi.toDouble())
            val distance = calculateDistance(smoothed, txPower)
            val zone = classifyZone(distance)
            onDistanceUpdated?.invoke(distance, zone)
        }
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {}
        override fun onStartFailure(errorCode: Int) {}
    }

    private fun calculateDistance(rssi: Double, txPower: Int): Double {
        if (rssi == 0.0) return -1.0
        val ratio = rssi / txPower
        return if (ratio < 1.0) {
            Math.pow(ratio, 10.0)
        } else {
            0.89976 * Math.pow(ratio, 7.7095) + 0.111
        }
    }

    inner class KalmanFilter {
        private var estimate = -70.0
        private var errorEstimate = 1.0
        private val errorMeasure = 2.0
        private val q = 0.1

        fun filter(measurement: Double): Double {
            val kalmanGain = errorEstimate / (errorEstimate + errorMeasure)
            estimate += kalmanGain * (measurement - estimate)
            errorEstimate = (1 - kalmanGain) * errorEstimate + q
            return estimate
        }
    }
}