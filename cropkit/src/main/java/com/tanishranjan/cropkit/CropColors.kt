package com.tanishranjan.cropkit

import androidx.compose.ui.graphics.Color

/**
 * Colors used to style the [ImageCropper].
 *
 * @param overlay The color of the overlay.
 * @param overlayActive The color of the overlay when active.
 * @param gridlines The color of the gridlines.
 * @param cropRectangle The color of the crop rectangle.
 * @param handle The color of the drag handles.
 */
data class CropColors(
    val overlay: Color,
    val overlayActive: Color,
    val gridlines: Color,
    val cropRectangle: Color,
    val handle: Color
)
