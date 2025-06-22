package com.tanishranjan.cropkit

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp

/**
 * Options for configuring the [ImageCropper].
 *
 * @param cropShape The shape of the crop area.
 * @param contentScale The scale type of the image content.
 * @param gridLinesVisibility The gridlines visibility mode.
 * @param gridLinesType The type of gridlines to be displayed.
 * @param handleRadius The radius of the drag handles.
 * @param touchPadding The padding around the drag handles to increase the touch area.
 */
data class CropOptions(
    val cropShape: CropShape,
    val contentScale: ContentScale,
    val gridLinesVisibility: GridLinesVisibility,
    val gridLinesType: GridLinesType,
    val handleRadius: Dp,
    val touchPadding: Dp
)
