package com.tanishranjan.cropkit.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

internal object Extensions {

    fun Offset.isInsideRect(rect: Rect): Boolean {
        return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom
    }

    fun Float.coerceInOrderAgnostic(a: Float, b: Float): Float {
        val min = minOf(a, b)
        val max = maxOf(a, b)
        return this.coerceIn(min, max)
    }
}