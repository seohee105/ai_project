package com.yourteam.daangnalarm

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager

class MainActivity : AppCompatActivity() {

    private lateinit var bleEngine: BleEngine
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var tvDistance: TextView
    private lateinit var btnBuyer: Button
    private lateinit var btnSeller: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 레이아웃 구성
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 100, 50, 50)
        }

        tvDistance = TextView(this).apply {
            text = "역할을 선택하세요"
            textSize = 30f
        }

        btnBuyer = Button(this).apply {
            text = "구매자 (거리 측정)"
            textSize = 18f
        }

        btnSeller = Button(this).apply {
            text = "판매자 (신호 송출)"
            textSize = 18f
        }

        layout.addView(tvDistance)
        layout.addView(btnBuyer)
        layout.addView(btnSeller)
        setContentView(layout)

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
            setupButtons()
        } else {
            Toast.makeText(this, "블루투스 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupButtons() {
        bleEngine = BleEngine(this)
        webSocketManager = WebSocketManager()
        webSocketManager.connect()
        bleEngine.webSocketManager = webSocketManager

        // 구매자 버튼
        btnBuyer.setOnClickListener {
            bleEngine.startScan()
            tvDistance.text = "거리: 측정 중..."
            btnBuyer.setBackgroundColor(Color.BLUE)
            btnSeller.setBackgroundColor(Color.GRAY)

            bleEngine.onDistanceUpdated = { distance, _ ->
                runOnUiThread {
                    tvDistance.text = "거리: ${String.format("%.2f", distance)}m"
                }
            }
        }

        // 판매자 버튼
        btnSeller.setOnClickListener {
            bleEngine.startAdvertise()
            tvDistance.text = "신호 송출 중..."
            btnSeller.setBackgroundColor(Color.GREEN)
            btnBuyer.setBackgroundColor(Color.GRAY)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::bleEngine.isInitialized) {
            bleEngine.stopScan()
            bleEngine.stopAdvertise()
        }
        if (::webSocketManager.isInitialized) {
            webSocketManager.disconnect()
        }
    }
}