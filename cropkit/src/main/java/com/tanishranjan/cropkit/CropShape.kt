package com.tanishranjan.cropkit

/**
 * Enum class representing the shape of the Crop Rectangle.
 */
sealed class CropShape {
    data object FreeForm: CropShape()
    data object Original: CropShape()
    data class AspectRatio(val ratio: Float): CropShape()
}