package com.yourteam.newdang

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AppearanceViewModel : ViewModel() {

    val topColor    = MutableLiveData(ColorPalette.colors[0])   // 파랑
    val bottomColor = MutableLiveData(ColorPalette.colors[8])   // 회색
    val hairColor   = MutableLiveData(ColorPalette.colors[11])  // 갈색

    val profile get() = AppearanceProfile(
        topColor    = topColor.value!!,
        bottomColor = bottomColor.value!!,
        hairColor   = hairColor.value!!
    )

    fun setColor(part: String, color: ClothingColor) {
        when (part) {
            "top"    -> topColor.value    = color
            "bottom" -> bottomColor.value = color
            "hair"   -> hairColor.value   = color
        }
    }
}

