package com.yourteam.daangnalarm

import okhttp3.*
import org.json.JSONObject

class WebSocketManager(
    private val onDistanceReceived: ((Double, String) -> Unit)? = null
) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect() {
        val request = Request.Builder()
            .url("ws://192.168.45.149:8080/ws/distance")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("서버 연결 성공")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val json = JSONObject(text)
                val distance = json.getDouble("distance")
                val zone = json.getString("zone")
                onDistanceReceived?.invoke(distance, zone)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("연결 실패: ${t.message}")
            }
        })
    }

    // 거리값 서버로 전송
    fun sendDistance(distance: Double, zone: String) {
        val message = """{"distance": $distance, "zone": "$zone"}"""
        webSocket?.send(message)
    }

    fun disconnect() {
        webSocket?.close(1000, "종료")
    }
}