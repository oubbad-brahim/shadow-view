package com.example.shadowview

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import com.example.shadowview.databinding.ActivityMainBinding
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.TriangleEdgeTreatment
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val shadowBitmap = shadowBitmap(
            500,
            500,
            60f,
            0f,
            0f,
            Color.RED,
            floatArrayOf(12f, 12f, 12f, 12f, 12f, 12f, 12f, 12f)
        )
//        binding.imageView.setImageBitmap(shadowBitmap)

        val shapePathModel = ShapeAppearanceModel.builder()
            .setAllCorners(CornerFamily.ROUNDED, 40.toFloat())
            .setBottomLeftCorner(CornerFamily.CUT, 40.toFloat())
            .setAllEdges(TriangleEdgeTreatment(20.toFloat(), false))
            .setTopEdge(TriangleEdgeTreatment(20.toFloat(), false))
            .setBottomEdge(TriangleEdgeTreatment(20.toFloat(), true))
            .build()

        val backgroundDrawable = MaterialShapeDrawable().apply {
            elevation = 240f
            setShadowColor(Color.GREEN)
            shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_DEFAULT
        }

//        binding.imageView.setImageDrawable(backgroundDrawable)


    }

    fun shadowBitmap(
        shadowWidth: Int, shadowHeight: Int, shadowRadius: Float,
        dx: Float, dy: Float, shadowColor: Int, radii: FloatArray
    ): Bitmap {

        if (shadowWidth <= 0 || shadowHeight <= 0) return createBitmap(1, 1)

        val output = createBitmap(shadowWidth, shadowHeight)
        val canvas = Canvas(output)
        val shadowRect = RectF(
            shadowRadius,
            shadowRadius,
            shadowWidth - shadowRadius,
            shadowHeight - shadowRadius
        )

        shadowRect.top += abs(dy)
        shadowRect.bottom -= abs(dy)
        shadowRect.left += abs(dx)
        shadowRect.right -= abs(dx)

        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        shadowPaint.color = shadowColor
        shadowPaint.setShadowLayer(shadowRadius, dx, dy, shadowColor)

        val path = Path()

        path.addRoundRect(shadowRect, radii, Path.Direction.CW)
        canvas.drawPath(path, shadowPaint)
        return output
    }

}