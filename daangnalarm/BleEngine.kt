package com.yourteam.daangnalarm

import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.BluetoothAdapter

class BleEngine {

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var scanner: BluetoothLeScanner? = null
    var onDistanceUpdated: ((Double) -> Unit)? = null

    // 블루투스 스캔 시작
    fun startScan() {
        scanner = bluetoothAdapter.bluetoothLeScanner
        scanner?.startScan(scanCallback)
    }

    // 블루투스 스캔 중지
    fun stopScan() {
        scanner?.stopScan(scanCallback)
    }

    // 신호 잡혔을 때
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val rssi = result.rssi
            val distance = calculateDistance(rssi)
            onDistanceUpdated?.invoke(distance)
        }
    }

    // RSSI로 거리 계산
    private fun calculateDistance(rssi: Int): Double {
        val txPower = -59
        if (rssi == 0) return -1.0
        val ratio = rssi * 1.0 / txPower
        return if (ratio < 1.0) {
            Math.pow(ratio, 10.0)
        } else {
            0.89976 * Math.pow(ratio, 7.7095) + 0.111
        }
    }
}