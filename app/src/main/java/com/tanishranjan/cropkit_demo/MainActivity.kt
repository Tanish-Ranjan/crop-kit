package com.tanishranjan.cropkit_demo

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tanishranjan.cropkit.CropDefaults
import com.tanishranjan.cropkit.CropRatio
import com.tanishranjan.cropkit.CropShape
import com.tanishranjan.cropkit.GridLinesType
import com.tanishranjan.cropkit.ImageCropper
import com.tanishranjan.cropkit.rememberCropController
import com.tanishranjan.cropkit_demo.ui.theme.CropKitTheme
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CropKitTheme {

                var image: Bitmap? by remember { mutableStateOf(null) }
                var cropShape: CropShape by remember { mutableStateOf(CropShape.Original) }
                var gridLinesType by remember { mutableStateOf(GridLinesType.GRID) }
                val cropController = image?.let {
                    rememberCropController(
                        bitmap = it,
                        cropOptions = CropDefaults.cropOptions(
                            cropShape = cropShape,
                            gridLinesType = gridLinesType
                        )
                    )
                }

                val context = LocalContext.current
                val imagePicker = rememberLauncherForActivityResult(
                    ActivityResultContracts.PickVisualMedia()
                ) { uri ->
                    image = uri?.toBitmap(context)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("CropKit")
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        cropController?.crop()?.let {
                                            saveImage(context, it)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Save"
                                    )
                                }
                            }
                        )
                    },
                ) { innerPadding ->

                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(
                            Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {

                                if (cropController != null) {

                                    ImageCropper(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .padding(24.dp),
                                        cropController = cropController
                                    )

                                    Spacer(Modifier.height(16.dp))

                                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {

                                        SegmentedButton(
                                            selected = cropShape == CropShape.FreeForm,
                                            onClick = {
                                                cropShape = CropShape.FreeForm
                                            },
                                            shape = SegmentedButtonDefaults.itemShape(
                                                index = 0,
                                                count = 4
                                            )
                                        ) {
                                            Text("Free-Form")
                                        }

                                        SegmentedButton(
                                            selected = cropShape == CropShape.Original,
                                            onClick = {
                                                cropShape = CropShape.Original
                                            },
                                            shape = SegmentedButtonDefaults.itemShape(
                                                index = 1,
                                                count = 4
                                            )
                                        ) {
                                            Text("Original")
                                        }

                                        SegmentedButton(
                                            selected = cropShape is CropShape.AspectRatio && gridLinesType == GridLinesType.CROSSHAIR,
                                            onClick = {
                                                cropShape = CropShape.AspectRatio(CropRatio.SQUARE)
                                                gridLinesType = GridLinesType.CROSSHAIR
                                            },
                                            shape = SegmentedButtonDefaults.itemShape(
                                                index = 2,
                                                count = 4
                                            )
                                        ) {
                                            Text("Square")
                                        }

                                        SegmentedButton(
                                            selected = cropShape is CropShape.AspectRatio && gridLinesType == GridLinesType.GRID_AND_CIRCLE,
                                            onClick = {
                                                cropShape = CropShape.AspectRatio(CropRatio.SQUARE)
                                                gridLinesType = GridLinesType.GRID_AND_CIRCLE
                                            },
                                            shape = SegmentedButtonDefaults.itemShape(
                                                index = 3,
                                                count = 4
                                            )
                                        ) {
                                            Text("Circle")
                                        }

                                    }

                                    Spacer(Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {

                                        IconButton(
                                            onClick = {
                                                cropController.rotateAntiClockwise()
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_rotate_acw),
                                                contentDescription = "Rotate Anti-Clockwise"
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                cropController.rotateClockwise()
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_rotate_cw),
                                                contentDescription = "Rotate Clockwise"
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                cropController.flipVertically()
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_flip_vert),
                                                contentDescription = "Flip Vertically"
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                cropController.flipHorizontally()
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_flip_horiz),
                                                contentDescription = "Flip Horizontally"
                                            )
                                        }

                                    }

                                }

                            }

                            if (image == null) {
                                Button(
                                    onClick = {
                                        imagePicker.launch(PickVisualMediaRequest())
                                    }
                                ) {
                                    Text("Select Image")
                                }
                            }

                        }
                    }

                }
            }
        }
    }
}

@Suppress("DEPRECATION")
private fun Uri.toBitmap(context: Context): Bitmap? {

    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(context.contentResolver, this)
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, this)
        ImageDecoder.decodeBitmap(source)
    }

}

private fun saveImage(context: Context, croppedImage: Bitmap) {

    val filename = "${System.currentTimeMillis()}.jpg"
    var fos: OutputStream? = null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        context.contentResolver?.also { resolver ->
            val contentValues = ContentValues().apply {
                put(
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    filename
                )
                put(
                    MediaStore.MediaColumns.MIME_TYPE,
                    "image/jpg"
                )
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES
                )
            }

            val imageUri: Uri? = resolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

            fos = imageUri?.let { stream ->
                resolver.openOutputStream(stream)
            }
        }
    } else {
        val imagesDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        val imageFile = File(imagesDir, filename)
        fos = FileOutputStream(imageFile)
    }

    fos?.use { stream ->
        croppedImage.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            stream
        )
    }

    Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show()

}