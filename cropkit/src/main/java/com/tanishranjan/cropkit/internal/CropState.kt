package com.tanishranjan.cropkit.internal

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import com.tanishranjan.cropkit.HandlesRect

/**
 * Represents the state of the crop operation.
 *
 * @param bitmap The bitmap of the image.
 * @param imageBitmap The image bitmap of the image.
 * @param cropRect The crop rectangle.
 * @param imageRect The image rectangle.
 * @param handles The handles rectangles of the crop rectangle.
 * @param canvasSize The size of the canvas.
 * @param isDragging Whether the user is dragging the crop rectangle or any of its handles.
 * @param gridlinesActive Whether the gridlines are active.
 */
internal data class CropState(
    val bitmap: Bitmap,
    val imageBitmap: ImageBitmap? = null,
    val cropRect: Rect = Rect.Zero,
    val imageRect: Rect = Rect.Zero,
    val handles: HandlesRect = HandlesRect(),
    val canvasSize: Size = Size.Zero,
    val isDragging: Boolean = false,
    val gridlinesActive: Boolean = false
)