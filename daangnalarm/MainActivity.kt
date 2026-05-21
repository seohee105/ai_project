package com.yourteam.daangnalarm

import android.os.Bundle
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager

class MainActivity : AppCompatActivity() {

    private lateinit var bleEngine: BleEngine
    private lateinit var webSocketManager:WebSocketManager
    private lateinit var tvDistance: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tvDistance = TextView(this).apply {
            text = "거리: 측정 중..."
            textSize = 30f
        }
        setContentView(tvDistance)

        requestBluetoothPermissions()
    }

    private fun requestBluetoothPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_ADVERTISE,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startBle()
        } else {
            Toast.makeText(this, "블루투스 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startBle() {
        bleEngine = BleEngine(this)

        // 웹소켓 연결 ← 추가
        webSocketManager = WebSocketManager()
        webSocketManager.connect()
        bleEngine.webSocketManager = webSocketManager
        bleEngine.onDistanceUpdated = { distance, _ ->
            runOnUiThread {
                tvDistance.text = "거리: ${String.format("%.2f", distance)}m"
            }
        }

        bleEngine.startScan()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::bleEngine.isInitialized) {
            bleEngine.stopScan()
            bleEngine.stopAdvertise()
        }
        webSocketManager.disconnect()
    }
}