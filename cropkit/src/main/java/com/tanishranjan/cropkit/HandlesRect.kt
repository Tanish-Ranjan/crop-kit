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
internal data class HandlesRect(
    val topLeft: Rect = Rect.Zero,
    val topRight: Rect = Rect.Zero,
    val bottomLeft: Rect = Rect.Zero,
    val bottomRight: Rect = Rect.Zero,
    val top: Rect = Rect.Zero,
    val bottom: Rect = Rect.Zero,
    val right: Rect = Rect.Zero,
    val left: Rect = Rect.Zero
) {

    val cornerHandles: List<Rect> by lazy {
        listOf(topLeft, topRight, bottomLeft, bottomRight)
    }

    val allHandles: List<Rect> by lazy {
        listOf(topLeft, topRight, bottomLeft, bottomRight, top, bottom, right, left)
    }

    val cornerNamedHandles: List<Pair<Rect, DragHandle>> by lazy {
        listOf(
            topLeft to DragHandle.TopLeft,
            topRight to DragHandle.TopRight,
            bottomLeft to DragHandle.BottomLeft,
            bottomRight to DragHandle.BottomRight
        )
    }

    val allNamedHandles: List<Pair<Rect, DragHandle>> by lazy {
        listOf(
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
