package com.example.weatherapp.ui

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class VerticalTextView: AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth) // Поменяли ширину и высоту местами
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.translate(0F, height.toFloat()) // Переместили начало координат вниз
        canvas.rotate(-90F) // Повернули текст на -90 градусов против часовой стрелки
        super.onDraw(canvas)
        canvas.restore()
    }
}