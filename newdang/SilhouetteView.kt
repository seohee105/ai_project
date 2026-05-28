package com.yourteam.newdang

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class SilhouetteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paintSkin   = makePaint("#D3C4A8")
    private val paintHair   = makePaint("#5A3E2B")
    private val paintTop    = makePaint("#378ADD")
    private val paintBottom = makePaint("#444441")
    private val paintShoe   = makePaint("#2C2C2A")

    private fun makePaint(hex: String) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor(hex)
    }

    fun setColors(topColor: Int, bottomColor: Int, hairColor: Int) {
        paintTop.color    = topColor
        paintBottom.color = bottomColor
        paintHair.color   = hairColor
        invalidate()
    }

    fun applyProfile(profile: AppearanceProfile) {
        setColors(
            topColor    = profile.topColor.toInt(),
            bottomColor = profile.bottomColor.toInt(),
            hairColor   = profile.hairColor.toInt()
        )
    }

    override fun onDraw(canvas: Canvas) {
        val w  = width.toFloat()
        val h  = height.toFloat()
        val cx = w / 2f

        canvas.drawOval(RectF(cx-42f, h*0.02f, cx+42f, h*0.20f), paintHair)
        canvas.drawOval(RectF(cx-38f, h*0.06f, cx+38f, h*0.25f), paintSkin)
        canvas.drawRoundRect(RectF(cx-50f, h*0.27f, cx+50f, h*0.57f), 12f, 12f, paintTop)
        canvas.drawRoundRect(RectF(cx-72f, h*0.27f, cx-52f, h*0.57f), 10f, 10f, paintTop)
        canvas.drawRoundRect(RectF(cx+52f, h*0.27f, cx+72f, h*0.57f), 10f, 10f, paintTop)
        canvas.drawRoundRect(RectF(cx-50f, h*0.55f, cx+50f, h*0.88f), 10f, 10f, paintBottom)
        canvas.drawRoundRect(RectF(cx-48f, h*0.78f, cx-8f,  h*1.00f), 10f, 10f, paintBottom)
        canvas.drawRoundRect(RectF(cx+8f,  h*0.78f, cx+48f, h*1.00f), 10f, 10f, paintBottom)
        canvas.drawRoundRect(RectF(cx-52f, h*0.94f, cx-4f,  h*1.00f), 8f, 8f, paintShoe)
        canvas.drawRoundRect(RectF(cx+4f,  h*0.94f, cx+52f, h*1.00f), 8f, 8f, paintShoe)
    }
}