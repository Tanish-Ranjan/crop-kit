package com.tanishranjan.cropkit.internal

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import com.tanishranjan.cropkit.CropShape
import com.tanishranjan.cropkit.Gridlines
import com.tanishranjan.cropkit.util.Extensions.isInsideRect
import com.tanishranjan.cropkit.util.Extensions.isSquareBounds
import com.tanishranjan.cropkit.util.GestureUtils
import com.tanishranjan.cropkit.util.MathUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

internal class CropStateManager(
    bitmap: Bitmap,
    private val cropShape: CropShape,
    private val contentScale: ContentScale,
    private val gridlines: Gridlines,
    private val handleRadius: Dp,
    private val touchPadding: Dp
) {

    private val _state = MutableStateFlow(CropState(bitmap))
    val state = _state.asStateFlow()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var dragMode: DragMode = DragMode.None
    private val density get() = Resources.getSystem().displayMetrics.density
    private val handleRadiusPx: Float get() = handleRadius.value * density

    init {
        reset(bitmap)
    }

    fun updateCanvasSize(canvasSize: Size) {
        setState(canvasSize, state.value.bitmap)
    }

    fun crop(): Bitmap {

        val state = state.value
        val bitmap = state.bitmap
        val imageRect = state.imageRect
        val cropRect = state.cropRect

        val scaleX = bitmap.width / imageRect.width
        val scaleY = bitmap.height / imageRect.height

        val cropX = ((cropRect.left - imageRect.left) * scaleX).toInt()
        val cropY = ((cropRect.top - imageRect.top) * scaleY).toInt()
        val cropWidth = (cropRect.width * scaleX).toInt()
        val cropHeight = (cropRect.height * scaleY).toInt()

        val x = cropX.coerceIn(0, bitmap.width)
        val y = cropY.coerceIn(0, bitmap.height)
        val width = cropWidth.coerceIn(0, bitmap.width - x)
        val height = cropHeight.coerceIn(0, bitmap.height - y)

        return Bitmap.createBitmap(
            bitmap,
            x, y,
            width, height
        )

    }

    fun onDragStart(offset: Offset) {

        val activeHandle = findActiveHandle(offset)

        dragMode = when {
            activeHandle != null -> DragMode.Handle(activeHandle)
            offset.isInsideRect(state.value.cropRect) -> DragMode.Move
            else -> DragMode.None
        }

        _state.update { cropState ->
            cropState.copy(
                isDragging = dragMode != DragMode.None,
                gridlinesActive = if (gridlines == Gridlines.ON_TOUCH) {
                    dragMode != DragMode.None
                } else {
                    cropState.gridlinesActive
                }
            )
        }

    }

    fun onDragEnd() {
        _state.update { cropState ->
            cropState.copy(
                isDragging = false,
                gridlinesActive = if (gridlines != Gridlines.ALWAYS) {
                    false
                } else cropState.gridlinesActive
            )
        }
        dragMode = DragMode.None
    }

    fun onDrag(dragAmount: Offset) {
        when (val dragMode = dragMode) {
            is DragMode.Handle -> dragHandles(dragMode.handle, dragAmount)

            DragMode.Move -> dragCropRect(dragAmount)

            DragMode.None -> {}
        }
    }

    fun rotateClockwise() = transformBitmap { matrix -> matrix.postRotate(90f) }

    fun rotateAntiClockwise() = transformBitmap { matrix -> matrix.postRotate(-90f) }

    fun flipHorizontally() = transformBitmap { matrix -> matrix.postScale(-1f, 1f) }

    fun flipVertically() = transformBitmap { matrix -> matrix.postScale(1f, -1f) }

    private fun transformBitmap(transformation: (Matrix) -> Unit) {
        val bitmap = state.value.bitmap
        val matrix = Matrix().apply(transformation)
        val newBitmap = Bitmap.createBitmap(
            bitmap, 0, 0,
            bitmap.width, bitmap.height,
            matrix, true
        )
        reset(newBitmap)
    }

    private fun dragCropRect(dragAmount: Offset) {

        val currentRect = state.value.cropRect
        val imageRect = state.value.imageRect

        val newLeft = (currentRect.left + dragAmount.x)
            .coerceIn(imageRect.left, imageRect.right - currentRect.width)
        val newTop = (currentRect.top + dragAmount.y)
            .coerceIn(imageRect.top, imageRect.bottom - currentRect.height)

        val newRect = Rect(
            left = newLeft,
            top = newTop,
            right = newLeft + currentRect.width,
            bottom = newTop + currentRect.height
        )

        _state.update {
            it.copy(
                cropRect = newRect,
                handles = GestureUtils.getNewHandleMeasures(newRect, handleRadiusPx)
            )
        }

    }

    private fun dragHandles(activeHandle: DragHandle, dragAmount: Offset) {
        val adjustedDragAmount = getDragAmountForShape(dragAmount, activeHandle)
        GestureUtils.getNewRectMeasures(
            activeHandle = activeHandle,
            dragAmount = adjustedDragAmount,
            imageRect = state.value.imageRect,
            cropRect = state.value.cropRect,
            minCropSize = MIN_CROP_SIZE
        )?.let { newRect ->
            _state.update {
                it.copy(
                    cropRect = newRect,
                    handles = GestureUtils.getNewHandleMeasures(
                        newRect,
                        handleRadiusPx
                    )
                )
            }
        }
    }

    private fun getDragAmountForShape(dragAmount: Offset, handle: DragHandle): Offset {
        if (!cropShape.isSquareBounds()) return dragAmount

        return when (handle) {
            // For corners, use the larger drag amount for both dimensions
            DragHandle.TopLeft, DragHandle.BottomRight -> {
                val maxDelta = max(abs(dragAmount.x), abs(dragAmount.y)) * dragAmount.x.sign
                Offset(maxDelta, maxDelta)
            }

            DragHandle.TopRight, DragHandle.BottomLeft -> {
                val maxDelta = max(abs(dragAmount.x), abs(dragAmount.y)) * dragAmount.x.sign
                Offset(maxDelta, maxDelta * -1)
            }

            else -> Offset.Zero
        }
    }

    private fun findActiveHandle(offset: Offset): DragHandle? {

        val handles = if (!cropShape.isSquareBounds()) {
            state.value.handles.getAllNamedHandles()
        } else {
            state.value.handles.getCornerNamedHandles()
        }

        handles.forEach { (handle, handleType) ->
            val padding = touchPadding.value * density
            val paddedHandle = Rect(
                Offset(handle.left - padding, handle.top - padding),
                Size(handle.width + padding * 2, handle.height + padding * 2)
            )
            if (offset.isInsideRect(paddedHandle)) {
                return handleType
            }
        }

        return null

    }

    private fun reset(bitmap: Bitmap) {
        coroutineScope.launch {
            setState(
                state.value.canvasSize,
                bitmap
            )
        }
    }

    private fun setState(
        canvasSize: Size,
        bitmap: Bitmap
    ) {

        if (canvasSize == Size.Zero) {
            return
        }

        val imageWidth = bitmap.width.toFloat()
        val imageHeight = bitmap.height.toFloat()

        val scaledSize = MathUtils.calculateScaledSize(
            srcWidth = imageWidth,
            srcHeight = imageHeight,
            dstWidth = canvasSize.width,
            dstHeight = canvasSize.height,
            contentScale = contentScale
        )

        val newBitmap = Bitmap.createScaledBitmap(
            bitmap,
            scaledSize.width.toInt(),
            scaledSize.height.toInt(),
            true
        )

        val offsetX = (canvasSize.width - scaledSize.width) / 2f
        val offsetY = (canvasSize.height - scaledSize.height) / 2f

        val cropSize = if (cropShape.isSquareBounds()) {
            Size(
                min(scaledSize.width, scaledSize.height),
                min(scaledSize.width, scaledSize.height)
            )
        } else {
            Size(scaledSize.width, scaledSize.height)
        }
        val cropOffset = if (cropShape.isSquareBounds()) {
            Offset(
                offsetX + (scaledSize.width - cropSize.width) / 2,
                offsetY + (scaledSize.height - cropSize.height) / 2
            )
        } else {
            Offset(offsetX, offsetY)
        }
        val cropRect = Rect(cropOffset, cropSize)

        _state.update {
            it.copy(
                canvasSize = canvasSize,
                bitmap = newBitmap,
                imageRect = Rect(
                    Offset(offsetX, offsetY),
                    Size(scaledSize.width, scaledSize.height)
                ),
                imageBitmap = newBitmap.asImageBitmap(),
                cropRect = cropRect,
                handles = GestureUtils.getNewHandleMeasures(cropRect, handleRadiusPx),
                gridlinesActive = gridlines == Gridlines.ALWAYS
            )
        }

    }

    companion object {
        private const val MIN_CROP_SIZE = 250f
    }

}