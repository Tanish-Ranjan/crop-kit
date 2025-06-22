# Crop Kit 🏜️

Crop Kit is a Jetpack Compose library that allows you to easily crop images. It provides a simple
yet customizable crop composable to style embed it in your app seamlessly.

## ✨ Features

- Choose from a variety of crop shapes including:
    - Free Form: Crop to any desired rectangular size.
    - Original: Maintain image's original aspect ratio during cropping.
    - Aspect Ratio: Specify any custom aspect ratio (1:1, 16:9, 4:3, etc.) for precise and
      consistent cropping.
- Image Transformations:
    - Flip images vertically or horizontally.
    - Rotate images clockwise or anti-clockwise by 90 degrees.
- Customize GridLines:
    - Gridlines can be set to be always visible, be visible on touch or never be visible.
    - Several types of gridlines are available such as crosshair, 3x3 grid, circle, 3x3 grid with
      circle.

## Preview

https://github.com/user-attachments/assets/df19cff9-95fe-415d-bf17-0a8bb4da7a32

## 🚀 Getting Started

1. **Add the JitPack repository to your root build.gradle file:**
    <details open>
    <summary>Kotlin DSL</summary>

    ```gradle
    dependencyResolutionManagement {
        ...
        repositories {
            ...
            maven { setUrl("https://jitpack.io") }
        }
    }
    ```

    </details>

    <details>
    <summary>Groovy DSL</summary>

    ```gradle
    dependencyResolutionManagement {
        ...
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
    ```

    </details>

2. **Add the dependency:**
    <details open>
    <summary>Kotlin DSL</summary>

    ```gradle
    dependencies {
        implementation("com.github.Tanish-Ranjan:crop-kit:version")
    }
    ```

    </details>

    <details>
    <summary>Groovy DSL</summary>

    ```gradle
    dependencies {
        implementation 'com.github.Tanish-Ranjan:crop-kit:version'
    }
    ```

    </details>

3. **Use the Composable:**
    ```kotlin
    @Composable
    fun ColumnScope.CropComposable() {
        // Create a crop controller and pass it your image bitmap
        val cropController = rememberCropController(bitmap = mBitmap)
   
        // Pass the controller to the ImageCropper composable
        ImageCropper(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(24.dp),
            cropController = cropController
        )
        
        Button(
            onClick = {
                val croppedBitmap = cropController.crop()
                // Handle the croppedBitmap
            }
        ) {
            Text("Crop")
        }  
    }
    ```

See [demo app](/app) for full implementation.

## 🤝 Contributing

We welcome contributions! Please read our [Contributing Guide](CONTRIBUTING.md) for more information
on how to get involved.

## 📄 License

This project is licensed under the [MIT License](LICENSE).
