package com.yourteam.newdang

import android.graphics.Color

data class ClothingColor(
    val nameKr: String,
    val hexColor: String
) {
    fun toInt(): Int = Color.parseColor(hexColor)
}

data class AppearanceProfile(
    val topColor: ClothingColor,
    val bottomColor: ClothingColor,
    val hairColor: ClothingColor
) {
    val matchingColor: ClothingColor get() = topColor
}

object ColorPalette {
    val colors = listOf(
        ClothingColor("파랑",   "#378ADD"),
        ClothingColor("하늘",   "#B5D4F4"),
        ClothingColor("초록",   "#1D9E75"),
        ClothingColor("주황",   "#D85A30"),
        ClothingColor("분홍",   "#D4537E"),
        ClothingColor("보라",   "#7F77DD"),
        ClothingColor("빨강",   "#E24B4A"),
        ClothingColor("노랑",   "#EF9F27"),
        ClothingColor("회색",   "#888780"),
        ClothingColor("검정",   "#2C2C2A"),
        ClothingColor("흰색",   "#F1EFE8"),
        ClothingColor("갈색",   "#5A3E2B")
    )
}