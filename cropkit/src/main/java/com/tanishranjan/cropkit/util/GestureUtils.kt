package com.tanishranjan.cropkit.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.tanishranjan.cropkit.HandlesRect
import com.tanishranjan.cropkit.internal.DragHandle

internal object GestureUtils {

    /**
     * Calculate the new crop rectangle based on the drag amount and the active handle.
     *
     * @param activeHandle The handle that is currently being dragged.
     * @param dragAmount The offset by which the handle is dragged.
     * @param imageRect The current image rect.
     * @param cropRect The current crop rect.
     * @param minCropSize The minimum size of crop rectangle that should be maintained.
     * @param aspectRatio The aspect ratio (width/height) to maintain. 0 means free-form.
     *
     * @return The new crop rectangle if the drag is valid, null otherwise.
     */
    fun getNewRectMeasures(
        activeHandle: DragHandle,
        dragAmount: Offset,
        imageRect: Rect,
        cropRect: Rect,
        minCropSize: Float,
        aspectRatio: Float = 0f
    ): Rect? {
        return when (activeHandle) {
            DragHandle.TopLeft -> {
                val newOffset = cropRect.topLeft + dragAmount
                val isXWithinBounds = newOffset.x in imageRect.left..cropRect.right - minCropSize
                val isYWithinBounds = newOffset.y in imageRect.top..cropRect.bottom - minCropSize
                if (isXWithinBounds && isYWithinBounds) Rect(
                    left = newOffset.x,
                    top = newOffset.y,
                    right = cropRect.right,
                    bottom = cropRect.bottom
                ) else cropRect

            }

            DragHandle.TopRight -> {
                val newOffset = cropRect.topRight + dragAmount
                val isXWithinBounds = newOffset.x in cropRect.left + minCropSize..imageRect.right
                val isYWithinBounds = newOffset.y in imageRect.top..cropRect.bottom - minCropSize
                if (isXWithinBounds && isYWithinBounds) Rect(
                    left = cropRect.left,
                    top = newOffset.y,
                    right = newOffset.x,
                    bottom = cropRect.bottom
                )
                else return cropRect

            }

            DragHandle.BottomLeft -> {
                val newOffset = cropRect.bottomLeft + dragAmount
                val isXWithinBounds = newOffset.x in imageRect.left..cropRect.right - minCropSize
                val isYWithinBounds = newOffset.y in cropRect.top + minCropSize..imageRect.bottom
                if (isXWithinBounds && isYWithinBounds) Rect(
                    left = newOffset.x,
                    top = cropRect.top,
                    right = cropRect.right,
                    bottom = newOffset.y
                )
                else cropRect

            }

            DragHandle.BottomRight -> {
                val newOffset = cropRect.bottomRight + dragAmount
                val isXWithinBounds = newOffset.x in cropRect.left + minCropSize..imageRect.right
                val isYWithinBounds = newOffset.y in cropRect.top + minCropSize..imageRect.bottom
                if (isXWithinBounds && isYWithinBounds) Rect(
                    left = cropRect.left,
                    top = cropRect.top,
                    right = newOffset.x,
                    bottom = newOffset.y
                )
                else cropRect
            }

            DragHandle.Top -> {
                if (aspectRatio > 0f) {
                    val newTop = (cropRect.top + dragAmount.y).coerceIn(
                        imageRect.top, cropRect.bottom - minCropSize
                    )
                    val newHeight = cropRect.bottom - newTop
                    val newWidth = newHeight * aspectRatio
                    val centerX = cropRect.center.x
                    val newLeft = centerX - newWidth / 2
                    val newRight = centerX + newWidth / 2
                    if (newWidth >= minCropSize && newLeft >= imageRect.left && newRight <= imageRect.right) {
                        Rect(
                            left = newLeft,
                            top = newTop,
                            right = newRight,
                            bottom = cropRect.bottom
                        )
                    } else cropRect
                } else {
                    val newTop = (cropRect.top + dragAmount.y).coerceIn(
                        imageRect.top, cropRect.bottom - minCropSize
                    )
                    Rect(
                        left = cropRect.left, top = newTop,
                        right = cropRect.right, bottom = cropRect.bottom
                    )
                }
            }

            DragHandle.Bottom -> {
                if (aspectRatio > 0f) {
                    val newBottom = (cropRect.bottom + dragAmount.y).coerceIn(
                        cropRect.top + minCropSize, imageRect.bottom
                    )
                    val newHeight = newBottom - cropRect.top
                    val newWidth = newHeight * aspectRatio
                    val centerX = cropRect.center.x
                    val newLeft = centerX - newWidth / 2
                    val newRight = centerX + newWidth / 2
                    if (newWidth >= minCropSize && newLeft >= imageRect.left && newRight <= imageRect.right) {
                        Rect(
                            left = newLeft,
                            top = cropRect.top,
                            right = newRight,
                            bottom = newBottom
                        )
                    } else cropRect
                } else {
                    val newBottom = (cropRect.bottom + dragAmount.y).coerceIn(
                        cropRect.top + minCropSize, imageRect.bottom
                    )
                    Rect(
                        left = cropRect.left, top = cropRect.top,
                        right = cropRect.right, bottom = newBottom
                    )
                }
            }

            DragHandle.Left -> {
                if (aspectRatio > 0f) {
                    val newLeft = (cropRect.left + dragAmount.x).coerceIn(
                        imageRect.left, cropRect.right - minCropSize
                    )
                    val newWidth = cropRect.right - newLeft
                    val newHeight = newWidth / aspectRatio
                    val centerY = cropRect.center.y
                    val newTop = centerY - newHeight / 2
                    val newBottom = centerY + newHeight / 2
                    if (newHeight >= minCropSize && newTop >= imageRect.top && newBottom <= imageRect.bottom) {
                        Rect(
                            left = newLeft,
                            top = newTop,
                            right = cropRect.right,
                            bottom = newBottom
                        )
                    } else cropRect
                } else {
                    val newLeft = (cropRect.left + dragAmount.x).coerceIn(
                        imageRect.left, cropRect.right - minCropSize
                    )
                    Rect(
                        left = newLeft, top = cropRect.top,
                        right = cropRect.right, bottom = cropRect.bottom
                    )
                }
            }

            DragHandle.Right -> {
                if (aspectRatio > 0f) {
                    val newRight = (cropRect.right + dragAmount.x).coerceIn(
                        cropRect.left + minCropSize, imageRect.right
                    )
                    val newWidth = newRight - cropRect.left
                    val newHeight = newWidth / aspectRatio
                    val centerY = cropRect.center.y
                    val newTop = centerY - newHeight / 2
                    val newBottom = centerY + newHeight / 2
                    if (newHeight >= minCropSize && newTop >= imageRect.top && newBottom <= imageRect.bottom) {
                        Rect(
                            left = cropRect.left,
                            top = newTop,
                            right = newRight,
                            bottom = newBottom
                        )
                    } else cropRect
                } else {
                    val newRight = (cropRect.right + dragAmount.x).coerceIn(
                        cropRect.left + minCropSize, imageRect.right
                    )
                    Rect(
                        left = cropRect.left, top = cropRect.top,
                        right = newRight, bottom = cropRect.bottom
                    )
                }
            }
        }
    }

    /**
     * Calculate the new handle rectangles based on the crop rectangle.
     *
     * @param cropRect The current crop rectangle.
     * @param handleRadius The radius of the handles.
     *
     * @return The new handle positions.
     */
    fun getNewHandleMeasures(
        cropRect: Rect,
        handleRadius: Float,
    ): HandlesRect {

        val topLeftOffset = cropRect.topLeft - Offset(handleRadius, handleRadius)
        val topLeftRect = Rect(
            left = topLeftOffset.x,
            top = topLeftOffset.y,
            right = topLeftOffset.x + handleRadius * 2,
            bottom = topLeftOffset.y + handleRadius * 2
        )

        val topRightOffset = cropRect.topRight - Offset(handleRadius, handleRadius)
        val topRightRect = Rect(
            left = topRightOffset.x,
            top = topRightOffset.y,
            right = topRightOffset.x + handleRadius * 2,
            bottom = topRightOffset.y + handleRadius * 2
        )

        val bottomLeftOffset = cropRect.bottomLeft - Offset(handleRadius, handleRadius)
        val bottomLeftRect = Rect(
            left = bottomLeftOffset.x,
            top = bottomLeftOffset.y,
            right = bottomLeftOffset.x + handleRadius * 2,
            bottom = bottomLeftOffset.y + handleRadius * 2
        )

        val bottomRightOffset = cropRect.bottomRight - Offset(handleRadius, handleRadius)
        val bottomRightRect = Rect(
            left = bottomRightOffset.x,
            top = bottomRightOffset.y,
            right = bottomRightOffset.x + handleRadius * 2,
            bottom = bottomRightOffset.y + handleRadius * 2
        )

        val halfWidth = (cropRect.width) / 2
        val halfHeight = (cropRect.height) / 2

        val topOffset =
            cropRect.topLeft + Offset(halfWidth, 0f) - Offset(handleRadius, handleRadius)
        val topRect = Rect(
            left = topOffset.x,
            top = topOffset.y,
            right = topOffset.x + handleRadius * 2,
            bottom = topOffset.y + handleRadius * 2
        )

        val bottomOffset =
            cropRect.bottomLeft + Offset(halfWidth, 0f) - Offset(handleRadius, handleRadius)
        val bottomRect = Rect(
            left = bottomOffset.x,
            top = bottomOffset.y,
            right = bottomOffset.x + handleRadius * 2,
            bottom = bottomOffset.y + handleRadius * 2
        )

        val leftOffset =
            cropRect.topLeft + Offset(0f, halfHeight) - Offset(handleRadius, handleRadius)
        val leftRect = Rect(
            left = leftOffset.x,
            top = leftOffset.y,
            right = leftOffset.x + handleRadius * 2,
            bottom = leftOffset.y + handleRadius * 2
        )

        val rightOffset =
            cropRect.topRight + Offset(0f, halfHeight) - Offset(handleRadius, handleRadius)
        val rightRect = Rect(
            left = rightOffset.x,
            top = rightOffset.y,
            right = rightOffset.x + handleRadius * 2,
            bottom = rightOffset.y + handleRadius * 2
        )

        return HandlesRect(
            topLeft = topLeftRect,
            topRight = topRightRect,
            bottomLeft = bottomLeftRect,
            bottomRight = bottomRightRect,
            top = topRect,
            bottom = bottomRect,
            left = leftRect,
            right = rightRect
        )

    }

}