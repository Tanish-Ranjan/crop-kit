package com.tanishranjan.cropkit

import android.graphics.Bitmap
import com.tanishranjan.cropkit.internal.CropStateManager
import com.tanishranjan.cropkit.internal.CropStateChangeActions

/**
 * CropController is the main class that is used to interact with the [ImageCropper].
 *
 * @param bitmap The bitmap that needs to be cropped.
 * @param cropOptions The options that are used to configure the ImageCropper.
 * @param cropColors The colors that are used to style the ImageCropper.
 */
class CropController(
    bitmap: Bitmap,
    val cropOptions: CropOptions,
    val cropColors: CropColors
) {

    private val stateManager: CropStateManager = CropStateManager(
        bitmap = bitmap,
        cropShape = cropOptions.cropShape,
        contentScale = cropOptions.contentScale,
        gridLinesVisibility = cropOptions.gridLinesVisibility,
        handleRadius = cropOptions.handleRadius,
        touchPadding = cropOptions.touchPadding
    )

    /**
     * State flow of the ImageCropper current state.
     */
    internal val state = stateManager.state

    /**
     * Returns the cropped bitmap.
     */
    fun crop(): Bitmap = stateManager.crop()

    fun cropBitmap(targetBitmap: Bitmap): Bitmap {
        return cropStateManager.cropBitmap(targetBitmap)
    }


    /**
     * Rotates the bitmap clockwise in the ImageCropper.
     */
    fun rotateClockwise() = stateManager.rotateClockwise()

    /**
     * Rotates the bitmap anti-clockwise in the ImageCropper.
     */
    fun rotateAntiClockwise() = stateManager.rotateAntiClockwise()

    /**
     * Flips the bitmap horizontally in the ImageCropper.
     */
    fun flipHorizontally() = stateManager.flipHorizontally()

    /**
     * Flips the bitmap vertically in the ImageCropper.
     */
    fun flipVertically() = stateManager.flipVertically()

    internal fun onStateChange(action: CropStateChangeActions) {
        when (action) {
            is CropStateChangeActions.DragStart -> stateManager.onDragStart(action.offset)
            is CropStateChangeActions.DragEnd -> stateManager.onDragEnd()
            is CropStateChangeActions.DragBy -> stateManager.onDrag(action.offset)
            is CropStateChangeActions.CanvasSizeChanged -> stateManager.updateCanvasSize(action.size)
        }
    }

}
