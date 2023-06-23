package com.example.shadowview

import android.view.View
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap

private fun View.toBitmap() = createBitmap(width, height).applyCanvas(::draw)