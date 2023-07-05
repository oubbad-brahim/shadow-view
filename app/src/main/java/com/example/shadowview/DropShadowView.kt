package com.example.shadowview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.DITHER_FLAG
import android.graphics.RectF
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import androidx.core.graphics.withScale
import androidx.core.graphics.withTranslation
import com.example.shadowview.Utils.BlurStackOptimized
import java.lang.Integer.max
import kotlin.system.measureTimeMillis

class DropShadowView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val TAG = this.javaClass.simpleName

    companion object {
        const val BITMAP_SIZE = 128
        const val SHADOW_COLOR = Color.BLACK
        const val SHADOW_RADIUS = 8
        const val SHADOW_CORNER = 0f
        const val SHADOW_SHIFT_X = 0f
        const val SHADOW_SHIFT_Y = 0f
        const val BLUR_SCALE = 0.5f
        const val SHADOW_SCALE = 1f / BLUR_SCALE
        const val SHADOW_SPREAD = 1
    }

    private val rectF = RectF()
    val blurEngine = BlurStackOptimized()

    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG or DITHER_FLAG)
    var shadowColor = SHADOW_COLOR

    var shadowRadius = SHADOW_RADIUS
    var shadowCorner = SHADOW_CORNER

    var shadowShiftX = SHADOW_SHIFT_X
    var shadowShiftY = SHADOW_SHIFT_Y

    var shadowSpread = SHADOW_SPREAD
        set(value) {
            bitmapSize /= value.coerceAtLeast(1)
            field = value
        }

    var shadowScale = SHADOW_SCALE
    var blurScale = BLUR_SCALE

    private var bitmap: Bitmap? = null
    private var blurBitmap: Bitmap? = null

    var bitmapSize = BITMAP_SIZE
        set(value) {
            field = value
            requestLayout()
        }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.DropShadowView).apply {
            shadowColor = getColor(R.styleable.DropShadowView_dsv_color, SHADOW_COLOR)

            shadowRadius = getInteger(R.styleable.DropShadowView_dsv_radius, SHADOW_RADIUS)
            shadowCorner = getDimension(R.styleable.DropShadowView_dsv_corner, SHADOW_CORNER)

            shadowShiftX = getDimension(R.styleable.DropShadowView_dsv_shift_x, SHADOW_SHIFT_X)
            shadowShiftY = getDimension(R.styleable.DropShadowView_dsv_shift_y, SHADOW_SHIFT_Y)

            shadowSpread = getInteger(R.styleable.DropShadowView_dsv_spread, SHADOW_SPREAD)

            shadowScale = getFloat(R.styleable.DropShadowView_dsv_scale, SHADOW_SCALE)
            blurScale = getFloat(R.styleable.DropShadowView_dsv_blur_scale, BLUR_SCALE)

        }.recycle()
        setWillNotDraw(false)
    }

    private var ratio = 0f


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        ratio = width / height.toFloat()
        rectF.set(0f, 0f, bitmapSize.toFloat(), (bitmapSize / ratio))
        bitmap = createBitmap(bitmapSize, bitmapSize)

    }

    override fun onDraw(canvas: Canvas) {
        measureTimeMillis {

            shadowPaint.color = shadowColor

            bitmap?.applyCanvas {
                withScale(blurScale, blurScale, width / 2f, height / 2f) {
                    drawColor(Color.parseColor("#FFFFFFFF"))
                    withTranslation((width - rectF.width()) / 2f, (height - rectF.height()) / 2f) {
                        drawRoundRect(rectF, shadowCorner, shadowCorner, shadowPaint)
                    }
                }
            }

            if (blurBitmap == null) blurBitmap = blurEngine.blur(bitmap!!, shadowRadius)

            val maxSize = max(width, height)

            val newScaledBitmap = blurBitmap!!.scale(maxSize, maxSize)

            canvas.withTranslation(
                shadowShiftX - (maxSize - width) / 2f,
                shadowShiftY - (maxSize - height) / 2f
            ) {
                withScale(shadowScale, shadowScale, width / 2f, width / 2f) {
                    drawBitmap(newScaledBitmap, 0f, 0f, null)
                }
            }
        }.let {
            Log.e(TAG, "onDraw time: $it")
        }
    }
}