# Crop Kit 🏜️

Crop Kit is a Jetpack Compose library that allows you to easily crop images. It provides a simple
yet customizable crop composable to style embed it in your app seamlessly.

## ✨ Features

- Provides three crop shapes: Rectangle, Square, and Circle.
- Allows flipping the image vertically or horizontally.
- Image can be rotated clockwise and anti-clockwise by 90 degrees.
- Gridlines can be enabled or disabled.

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
        implementation("com.github.Tanish-Ranjan:crop_kit:version")
    }
    ```

    </details>

    <details>
    <summary>Groovy DSL</summary>

    ```gradle
    dependencies {
        implementation 'com.github.Tanish-Ranjan:crop_kit:version'
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
