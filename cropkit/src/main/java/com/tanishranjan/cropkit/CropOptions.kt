package com.tanishranjan.cropkit

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp

/**
 * Options for configuring the [ImageCropper].
 *
 * @param cropShape The shape of the crop area.
 * @param contentScale The scale type of the image content.
 * @param gridlines The gridlines visibility mode.
 * @param handleRadius The radius of the drag handles.
 * @param touchPadding The padding around the drag handles to increase the touch area.
 */
data class CropOptions(
    val cropShape: CropShape,
    val contentScale: ContentScale,
    val gridlines: Gridlines,
    val handleRadius: Dp,
    val touchPadding: Dp
)
