package com.example.shadowview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.core.graphics.withScale


class ShadowLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

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
//        if (!isHardwareAccelerated) {
//            if (!isInEditMode) {
//                setLayerType(LAYER_TYPE_SOFTWARE, null)
//            }
//        }
//        setWillNotDraw(false)
    }

    override fun dispatchDraw(canvas: Canvas) {

        val bitmap = createBitmap(width, height).applyCanvas {
            withScale(0.125f, 0.125f, width / 2f, height / 2f) {
                super.dispatchDraw(this)
            }
        }


        val blurBitmap = blur(context, bitmap, 0f)

        canvas.withScale(16f, 16f, width / 2f, height / 2f) {
            drawBitmap(blurBitmap, 0f, 0f, null)
        }

    }


    private fun blur(context: Context?, image: Bitmap, radius: Float): Bitmap {

        if (radius == 0f) return image

        val outputBitmap = createBitmap(image.width, image.height)
        val rs = RenderScript.create(context)
        val intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val tmpIn = Allocation.createFromBitmap(rs, image)
        val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
        intrinsicBlur.setRadius(radius)
        intrinsicBlur.setInput(tmpIn)
        intrinsicBlur.forEach(tmpOut)
        tmpOut.copyTo(outputBitmap)
        return outputBitmap
    }
}

