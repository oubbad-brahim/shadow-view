package com.example.shadowview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toRectF

class ShadowLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    val TAG = javaClass.simpleName

    private val shadowPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect: Rect = Rect()
    private val cornerRadius: Float
    private val shadowColor: Int
    private val shadowRadius: Float
    private val shadowDx: Float
    private val shadowDy: Float

    init {
        context.obtainStyledAttributes(attrs, R.styleable.ShadowLayout).apply {
            cornerRadius = getDimension(R.styleable.ShadowLayout_shadowCornerRadius, 0f)
            shadowColor = getColor(R.styleable.ShadowLayout_shadowColor, Color.BLACK)
            shadowRadius = getDimension(R.styleable.ShadowLayout_shadowRadius, 0f)
            shadowDx = getDimension(R.styleable.ShadowLayout_shadowDx, 0f)
            shadowDy = getDimension(R.styleable.ShadowLayout_shadowDy, 0f)

        }.recycle()
        if (!isHardwareAccelerated) {
            if (!isInEditMode) {
                setLayerType(LAYER_TYPE_SOFTWARE, null)
            }
        }
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {

        val view = findViewById<View>(R.id.v_box)
        view.getDrawingRect(rect)
        rect.offset(view.x.toInt(), view.y.toInt())
        shadowPaint.color = shadowColor
        shadowPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)
        val bitmap = createBitmap(width, height).applyCanvas {
            drawRoundRect(rect.toRectF(), cornerRadius, cornerRadius, shadowPaint)
        }

        canvas.drawBitmap(bitmap, 0f, 0f, null)
    }


}

