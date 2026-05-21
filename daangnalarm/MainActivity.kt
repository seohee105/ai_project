package com.yourteam.daangnalarm

import android.location.Location

class MainActivity {

    // 두 좌표 사이 거리 계산 (미터 단위)
    fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0].toDouble()
    }

    // 1m 이내면 랜덤 컬러 반환, 아니면 null
    fun assignColor(distance: Double): String? {
        return if (distance <= 1.0) {
            val colors = listOf("#FFD700", "#FF6B6B", "#00CED1", "#98FB98")
            colors.random()
        } else null
    }

    // RSSI 값으로 거리 계산 (블루투스용)
    fun calculateBleDistance(rssi: Int): Double {
        val txPower = -59 // 블루투스 기본 신호 세기
        if (rssi == 0) return -1.0
        val ratio = rssi * 1.0 / txPower
        return if (ratio < 1.0) {
            Math.pow(ratio, 10.0)
        } else {
            0.89976 * Math.pow(ratio, 7.7095) + 0.111
        }
    }
}
