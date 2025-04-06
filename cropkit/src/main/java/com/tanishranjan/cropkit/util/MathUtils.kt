package com.tanishranjan.cropkit.util

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import kotlin.math.max
import kotlin.math.min

internal object MathUtils {

    /**
     * Calculate the scaled size of the source image based on the destination size and content scale.
     *
     * @param srcWidth The width of the source image.
     * @param srcHeight The height of the source image.
     * @param dstWidth The width of the destination.
     * @param dstHeight The height of the destination.
     * @param contentScale The content scale.
     */
    fun calculateScaledSize(
        srcWidth: Float,
        srcHeight: Float,
        dstWidth: Float,
        dstHeight: Float,
        contentScale: ContentScale
    ): Size {
        return when (contentScale) {
            ContentScale.Fit -> {
                val widthRatio = dstWidth / srcWidth
                val heightRatio = dstHeight / srcHeight
                val scale = min(widthRatio, heightRatio)
                Size((srcWidth * scale), (srcHeight * scale))
            }

            ContentScale.Crop -> {
                val widthRatio = dstWidth / srcWidth
                val heightRatio = dstHeight / srcHeight
                val scale = max(widthRatio, heightRatio)
                Size((srcWidth * scale), (srcHeight * scale))
            }

            ContentScale.Inside -> {
                if (srcWidth <= dstWidth && srcHeight <= dstHeight) {
                    Size(srcWidth, srcHeight)
                } else {
                    val widthRatio = dstWidth / srcWidth
                    val heightRatio = dstHeight / srcHeight
                    val scale = min(widthRatio, heightRatio)
                    Size((srcWidth * scale), (srcHeight * scale))
                }
            }

            else -> Size(dstWidth, dstHeight) // Default case
        }
    }

}