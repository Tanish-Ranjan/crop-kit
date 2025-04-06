package com.tanishranjan.cropkit

import androidx.compose.ui.geometry.Rect
import com.tanishranjan.cropkit.internal.DragHandle

/**
 * Represents the handle rectangles of the crop rectangle.
 *
 * @param topLeft The top-left handle.
 * @param topRight The top-right handle.
 * @param bottomLeft The bottom-left handle.
 * @param bottomRight The bottom-right handle.
 * @param top The top handle.
 * @param bottom The bottom handle.
 * @param right The right handle.
 * @param left The left handle.
 */
data class HandlesRect(
    val topLeft: Rect = Rect.Zero,
    val topRight: Rect = Rect.Zero,
    val bottomLeft: Rect = Rect.Zero,
    val bottomRight: Rect = Rect.Zero,
    val top: Rect = Rect.Zero,
    val bottom: Rect = Rect.Zero,
    val right: Rect = Rect.Zero,
    val left: Rect = Rect.Zero
) {

    /**
     * Returns the corner handles of the crop rectangle.
     */
    fun getCornerHandles(): List<Rect> {
        return listOf(topLeft, topRight, bottomLeft, bottomRight)
    }

    /**
     * Returns corner and side handles of the crop rectangle.
     */
    fun getAllHandles(): List<Rect> {
        return listOf(topLeft, topRight, bottomLeft, bottomRight, top, bottom, right, left)
    }

    /**
     * Returns the corner handles of the crop rectangle along with their names.
     */
    internal fun getCornerNamedHandles(): List<Pair<Rect, DragHandle>> {
        return listOf(
            topLeft to DragHandle.TopLeft,
            topRight to DragHandle.TopRight,
            bottomLeft to DragHandle.BottomLeft,
            bottomRight to DragHandle.BottomRight
        )
    }

    /**
     * Returns corner and side handles of the crop rectangle along with their names.
     */
    internal fun getAllNamedHandles(): List<Pair<Rect, DragHandle>> {
        return listOf(
            topLeft to DragHandle.TopLeft,
            topRight to DragHandle.TopRight,
            bottomLeft to DragHandle.BottomLeft,
            bottomRight to DragHandle.BottomRight,
            top to DragHandle.Top,
            bottom to DragHandle.Bottom,
            right to DragHandle.Right,
            left to DragHandle.Left
        )
    }

}
