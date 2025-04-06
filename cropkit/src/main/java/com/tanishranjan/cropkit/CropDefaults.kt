package com.tanishranjan.cropkit

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object CropDefaults {

    /**
     * Default crop options for [ImageCropper].
     */
    fun cropOptions(
        cropShape: CropShape = CropShape.CIRCLE,
        contentScale: ContentScale = ContentScale.Fit,
        gridlines: Gridlines = Gridlines.ON_TOUCH,
        handleRadius: Dp = 8.dp,
        touchPadding: Dp = 10.dp
    ) = CropOptions(
        cropShape = cropShape,
        contentScale = contentScale,
        gridlines = gridlines,
        handleRadius = handleRadius,
        touchPadding = touchPadding
    )

    /**
     * Default crop colors for [ImageCropper].
     */
    fun cropColors(
        overlay: Color = Color.Black.copy(0.75f),
        overlayActive: Color = Color.Black.copy(0.5f),
        gridlines: Color = Color.White.copy(0.5f),
        cropRectangle: Color = Color.White.copy(0.5f),
        handle: Color = Color.White
    ) = CropColors(
        overlay = overlay,
        overlayActive = overlayActive,
        gridlines = gridlines,
        cropRectangle = cropRectangle,
        handle = handle
    )

}