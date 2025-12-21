package com.group20.codevocab.ui.flashcard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.group20.codevocab.R

class FlashcardChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var knowCount = 0
    private var hardCount = 0
    private var reviewCount = 0

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 30f // Độ dày của vòng tròn
        strokeCap = Paint.Cap.ROUND
    }

    private val rectF = RectF()

    fun setData(know: Int, hard: Int, review: Int) {
        this.knowCount = know
        this.hardCount = hard
        this.reviewCount = review
        invalidate() // Vẽ lại view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val total = knowCount + hardCount + reviewCount
        if (total == 0) return

        val width = width.toFloat()
        val height = height.toFloat()
        val size = if (width < height) width else height
        val margin = 40f
        
        rectF.set(margin, margin, size - margin, size - margin)

        var startAngle = -90f // Bắt đầu từ đỉnh trên cùng

        // Vẽ phần Know (Green)
        if (knowCount > 0) {
            paint.color = ContextCompat.getColor(context, R.color.status_green)
            val sweepAngle = (knowCount.toFloat() / total) * 360f
            canvas.drawArc(rectF, startAngle, sweepAngle, false, paint)
            startAngle += sweepAngle
        }

        // Vẽ phần Review (Yellow)
        if (reviewCount > 0) {
            paint.color = ContextCompat.getColor(context, R.color.status_yellow)
            val sweepAngle = (reviewCount.toFloat() / total) * 360f
            canvas.drawArc(rectF, startAngle, sweepAngle, false, paint)
            startAngle += sweepAngle
        }

        // Vẽ phần Hard (Red)
        if (hardCount > 0) {
            paint.color = ContextCompat.getColor(context, R.color.status_red)
            val sweepAngle = (hardCount.toFloat() / total) * 360f
            canvas.drawArc(rectF, startAngle, sweepAngle, false, paint)
        }
    }
}
