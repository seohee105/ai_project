package com.yourteam.daangnalarm

class MainActivity {

    private lateinit var bleEngine: BleEngine

    // 앱 시작할 때 호출
    fun onCreate(context: android.content.Context) {
        bleEngine = BleEngine(context)

        // 거리 업데이트될 때마다 실행
        bleEngine.onDistanceUpdated = { distance ->
            println("현재 거리: ${distance}m")

            val color = assignColor(distance)
            if (color != null) {
                println("1m 이내! 배정된 컬러: $color")
            }
        }
    }

    // 스캔 시작 (구매자 모드)
    fun startAsBuyer() {
        bleEngine.startScan()
    }

    // 신호 쏘기 시작 (판매자 모드)
    fun startAsSeller() {
        bleEngine.startAdvertise()
    }

    // 종료할 때
    fun onDestroy() {
        bleEngine.stopScan()
        bleEngine.stopAdvertise()
    }

    // 1m 이내면 랜덤 컬러 반환
    fun assignColor(distance: Double): String? {
        return if (distance <= 1.0) {
            val colors = listOf("#FFD700", "#FF6B6B", "#00CED1", "#98FB98")
            colors.random()
        } else null
    }
}