package com.tanishranjan.cropkit.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.tanishranjan.cropkit.internal.DragHandle
import com.tanishranjan.cropkit.HandlesRect
import com.tanishranjan.cropkit.internal.DragHandle.*
import com.tanishranjan.cropkit.util.Extensions.coerceInOrderAgnostic
import kotlin.math.abs

internal object GestureUtils {
    /**
     * Get the offset of a handle based on its type and the crop rectangle.
     *
     * @param activeHandle The handle type.
     * @param cropRect The current crop rectangle.
     * @return The offset of the handle.
     */
    fun getHandleOffset(
        activeHandle: DragHandle,
        cropRect: Rect
    ): Offset =
        when (activeHandle) {
            TopLeft -> Offset(cropRect.left, cropRect.top)
            TopRight -> Offset(cropRect.right, cropRect.top)
            BottomLeft -> Offset(cropRect.left, cropRect.bottom)
            BottomRight -> Offset(cropRect.right, cropRect.bottom)
            Left -> Offset(cropRect.left, 0f)
            Right -> Offset(cropRect.right, 0f)
            Top -> Offset(0f, cropRect.top)
            Bottom -> Offset(0f, cropRect.bottom)
        }

    /**
     * Correct the drag amount based on the drag offset.
     *
     * @param dragOffset The current drag offset.
     * @param dragAmount The original drag amount.
     * @return The corrected drag amount.
     */
    fun getCorrectDragAmount(
        dragOffset: Offset,
        dragAmount: Offset
    ): Offset = Offset(dragOffset.x + dragAmount.x, dragOffset.y + dragAmount.y)

    /**
     * Main entry point to calculate the new crop rectangle.
     * Decides whether to use Free or Locked logic based on the [aspectRatio].
     *
     * @param activeHandle The handle that is currently being dragged.
     * @param handleOffset The offset by which the handle is dragged.
     * @param imageRect The current image rect.
     * @param cropRect The current crop rect.
     * @param minCropSize The minimum size of crop rectangle that should be maintained.
     * @param aspectRatio The desired aspect ratio (width / height).
     * @return The new crop rectangle if the drag is valid, null otherwise.
     */
    fun calculateNewCropRect(
        activeHandle: DragHandle,
        handleOffset: Offset,
        imageRect: Rect,
        cropRect: Rect,
        minCropSize: Float,
        aspectRatio: Float
    ): Rect =
        if (aspectRatio > 0f) getNewRectMeasuresLocked(
            activeHandle, handleOffset, imageRect, cropRect, minCropSize, aspectRatio
        )
        else getNewRectMeasures(
            activeHandle, handleOffset, imageRect, cropRect, minCropSize
        )

    /**
     * Calculate the new crop rectangle based on the drag amount and the active handle.
     *
     * @param activeHandle The handle that is currently being dragged.
     * @param dragAmount The offset by which the handle is dragged.
     * @param imageRect The current image rect.
     * @param cropRect The current crop rect.
     * @param minCropSize The minimum size of crop rectangle that should be maintained.
     * @return The new crop rectangle if the drag is valid, null otherwise.
     */
    fun getNewRectMeasures(
        activeHandle: DragHandle,
        dragAmount: Offset,
        imageRect: Rect,
        cropRect: Rect,
        minCropSize: Float
    ): Rect = when (activeHandle) {
        TopLeft -> {
            val newLeft = dragAmount.x.coerceInOrderAgnostic(
                imageRect.left,
                cropRect.right - minCropSize
            )
            val newTop = dragAmount.y.coerceInOrderAgnostic(
                imageRect.top,
                cropRect.bottom - minCropSize
            )
            Rect(
                left = newLeft,
                top = newTop,
                right = cropRect.right,
                bottom = cropRect.bottom
            )
        }

        TopRight -> {
            val newRight = dragAmount.x.coerceInOrderAgnostic(
                cropRect.left + minCropSize,
                imageRect.right
            )
            val newTop = dragAmount.y.coerceInOrderAgnostic(
                imageRect.top,
                cropRect.bottom - minCropSize
            )
            Rect(
                left = cropRect.left,
                top = newTop,
                right = newRight,
                bottom = cropRect.bottom
            )
        }

        BottomLeft -> {
            val newLeft = dragAmount.x.coerceInOrderAgnostic(
                imageRect.left,
                cropRect.right - minCropSize
            )
            val newBottom = dragAmount.y.coerceInOrderAgnostic(
                cropRect.top + minCropSize,
                imageRect.bottom
            )
            Rect(
                left = newLeft,
                top = cropRect.top,
                right = cropRect.right,
                bottom = newBottom
            )
        }

        BottomRight -> {
            val newRight = dragAmount.x.coerceInOrderAgnostic(
                cropRect.left + minCropSize,
                imageRect.right
            )
            val newBottom = dragAmount.y.coerceInOrderAgnostic(
                cropRect.top + minCropSize,
                imageRect.bottom
            )
            Rect(
                left = cropRect.left,
                top = cropRect.top,
                right = newRight,
                bottom = newBottom
            )
        }

        Top -> {
            val newTop = dragAmount.y.coerceInOrderAgnostic(
                imageRect.top,
                cropRect.bottom - minCropSize
            )
            Rect(
                left = cropRect.left,
                top = newTop,
                right = cropRect.right,
                bottom = cropRect.bottom
            )
        }

        Bottom -> {
            val newBottom = dragAmount.y.coerceInOrderAgnostic(
                cropRect.top + minCropSize,
                imageRect.bottom
            )
            Rect(
                left = cropRect.left,
                top = cropRect.top,
                right = cropRect.right,
                bottom = newBottom
            )
        }

        Left -> {
            val newLeft = dragAmount.x.coerceInOrderAgnostic(
                imageRect.left,
                cropRect.right - minCropSize
            )
            Rect(
                left = newLeft,
                top = cropRect.top,
                right = cropRect.right,
                bottom = cropRect.bottom
            )
        }

        Right -> {
            val newRight = dragAmount.x.coerceInOrderAgnostic(
                cropRect.left + minCropSize,
                imageRect.right
            )
            Rect(
                left = cropRect.left,
                top = cropRect.top,
                right = newRight,
                bottom = cropRect.bottom
            )
        }
    }

    /**
     * Calculates the new crop rectangle with locked Aspect Ratio.
     * It projects the user's gesture onto the aspect ratio diagonal, ensuring the
     * rectangle grows/shrinks naturally while staying within image bounds.
     *
     * @param activeHandle The handle that is currently being dragged.
     * @param handleOffset The offset by which the handle is dragged.
     * @param imageRect The current image rect.
     * @param cropRect The current crop rect.
     * @param minCropSize The minimum size of crop rectangle that should be maintained.
     * @param aspectRatio The desired aspect ratio (width / height).
     * @return The new crop rectangle if the drag is valid, null otherwise.
     */
    fun getNewRectMeasuresLocked(
        activeHandle: DragHandle,
        handleOffset: Offset,
        imageRect: Rect,
        cropRect: Rect,
        minCropSize: Float,
        aspectRatio: Float
    ): Rect {
        val pivot = when (activeHandle) {
            TopLeft -> Offset(cropRect.right, cropRect.bottom)
            TopRight -> Offset(cropRect.left, cropRect.bottom)
            BottomLeft -> Offset(cropRect.right, cropRect.top)
            BottomRight -> Offset(cropRect.left, cropRect.top)
            else -> return cropRect
        }

        // Pre-calculate minimum limits based on the Aspect Ratio
        val minWidth: Float
        val minHeight: Float

        if (aspectRatio >= 1f) {
            minHeight = minCropSize
            minWidth = minHeight * aspectRatio
        } else {
            minWidth = minCropSize
            minHeight = minWidth / aspectRatio
        }

        // Constrain the Handle Offset
        val constrainedX = when (activeHandle) {
            TopLeft, BottomLeft -> handleOffset.x.coerceAtMost(pivot.x - minWidth)
            TopRight, BottomRight -> handleOffset.x.coerceAtLeast(pivot.x + minWidth)
            else -> handleOffset.x
        }

        val constrainedY = when (activeHandle) {
            TopLeft, TopRight -> handleOffset.y.coerceAtMost(pivot.y - minHeight)
            BottomLeft, BottomRight -> handleOffset.y.coerceAtLeast(pivot.y + minHeight)
            else -> handleOffset.y
        }

        // Calculate distances using the CONSTRAINED position
        val distanceX = abs(constrainedX - pivot.x)
        val distanceY = abs(constrainedY - pivot.y)

        // Generate Candidates (Projections)
        val widthBasedOnX = distanceX.coerceAtLeast(minWidth)
        val heightDerivedFromX = widthBasedOnX / aspectRatio

        val heightBasedOnY = distanceY.coerceAtLeast(minHeight)
        val widthDerivedFromY = heightBasedOnY * aspectRatio

        // We pick the candidate that produces the smallest rectangle
        var finalWidth = if (widthBasedOnX < widthDerivedFromY)
            widthBasedOnX else widthDerivedFromY
        var finalHeight = if (widthBasedOnX < widthDerivedFromY)
            heightDerivedFromX else heightBasedOnY

        // Bounds Constraint
        // Determine the direction of growth relative to the pivot (-1 for Left/Up, 1 for Right/Down)
        val directionX = if (activeHandle == TopRight || activeHandle == BottomRight) 1f else -1f
        val directionY = if (activeHandle == BottomLeft || activeHandle == BottomRight) 1f else -1f

        // Check Horizontal Bounds
        val proposedLeft = if (directionX < 0) pivot.x - finalWidth else pivot.x
        val proposedRight = if (directionX < 0) pivot.x else pivot.x + finalWidth

        if (proposedLeft < imageRect.left || proposedRight > imageRect.right) {
            val maxWidthAvailable =
                if (directionX < 0) (pivot.x - imageRect.left)
                else (imageRect.right - pivot.x)

            finalWidth = maxWidthAvailable
            finalHeight = finalWidth / aspectRatio
        }

        // Check Vertical Bounds
        val proposedTop = if (directionY < 0) pivot.y - finalHeight else pivot.y
        val proposedBottom = if (directionY < 0) pivot.y else pivot.y + finalHeight

        if (proposedTop < imageRect.top || proposedBottom > imageRect.bottom) {
            val maxHeightAvailable = if (directionY < 0) (pivot.y - imageRect.top)
            else (imageRect.bottom - pivot.y)

            finalHeight = maxHeightAvailable
            finalWidth = finalHeight * aspectRatio
        }

        return Rect(
            left = if (directionX < 0) pivot.x - finalWidth else pivot.x,
            top = if (directionY < 0) pivot.y - finalHeight else pivot.y,
            right = if (directionX < 0) pivot.x else pivot.x + finalWidth,
            bottom = if (directionY < 0) pivot.y else pivot.y + finalHeight
        )
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