package com.faatikhriziq.paru.ui.screens.capture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faatikhriziq.paru.ui.theme.primaryColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun createImageFile(outputDirectory: File): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return File(outputDirectory, "IMG_${timeStamp}.jpg")
}

fun getOutputDirectory(context: android.content.Context): File {
    val appContext = context.applicationContext
    val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
        File(
            it,
            appContext.resources.getString(com.faatikhriziq.paru.R.string.app_name)
        ).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else appContext.filesDir
}


suspend fun loadImageBitmap(uri: android.net.Uri, context: Context): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


suspend fun performAQIAnalysis(bitmap: Bitmap?, context: Context): Float {
    return withContext(Dispatchers.Default) {
        val tflite = Interpreter(loadModelFile(context))
        val inputImageBuffer = convertBitmapToByteBuffer(bitmap!!)
        val outputScoreBuffer = Array(1) { FloatArray(1) }
        tflite.run(inputImageBuffer, outputScoreBuffer)
        outputScoreBuffer[0][0]
    }
}

fun loadModelFile(context: Context): File {
    val modelFilename = "VGG16.tflite"

    try {
        val modelFile = File(context.filesDir, modelFilename)
        if (!modelFile.exists()) {
            val assetManager = context.assets
            val inputStream = assetManager.open(modelFilename)
            val outputStream = FileOutputStream(modelFile)

            inputStream.copyTo(outputStream)

            inputStream.close()
            outputStream.close()
        }

        return modelFile
    } catch (e: IOException) {
        throw RuntimeException("Error loading model file: $modelFilename", e)
    }
}

fun convertBitmapToByteBuffer(bitmap: android.graphics.Bitmap): ByteBuffer {
    val modelInputSize = 244 * 244 * 3 * 4
    val inputImageBuffer = ByteBuffer.allocateDirect(modelInputSize)
    inputImageBuffer.order(ByteOrder.nativeOrder())

    val pixels = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

    for (pixelValue in pixels) {
        val r = (pixelValue shr 16 and 0xFF) / 255.0f
        val g = (pixelValue shr 8 and 0xFF) / 255.0f
        val b = (pixelValue and 0xFF) / 255.0f

        inputImageBuffer.putFloat(r)
        inputImageBuffer.putFloat(g)
        inputImageBuffer.putFloat(b)
    }

    return inputImageBuffer
}


@Composable
fun CaptureScreen() {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember { mutableStateOf<ImageCapture?>(null) }
    val imagePreview = remember { mutableStateOf<androidx.camera.core.Preview?>(null) }
    val imageBitmap = remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val aqiResult = remember { mutableStateOf("") }

    val outputDirectory = remember { getOutputDirectory(context) }
    val imageFile = remember { mutableStateOf<File?>(null) }

    val cameraSelector = remember { CameraSelector.DEFAULT_BACK_CAMERA }

    val viewModel: CaptureViewModel = viewModel()




    val previewView = remember { PreviewView(context) }
    val lifecycleOwner = LocalLifecycleOwner.current




    androidx.compose.runtime.DisposableEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build()

        preview.setSurfaceProvider(previewView.surfaceProvider)

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview
            )
        } catch (exc: Exception) {
            Log.e("CaptureScreen", "Failed to bind camera preview: ${exc.message}")
        }

        onDispose {
            cameraProvider.unbindAll()
        }
    }



    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        Column(
            modifier = Modifier.padding(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            imageBitmap.value?.asImageBitmap()?.let {
                Image(
                    bitmap = it,
                    contentDescription = "Captured Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }

            Spacer(modifier = Modifier.height(170.dp))

            Button(onClick = {
                imageFile.value = createImageFile(outputDirectory)
                val imageCaptureValue = imageCapture.value
                val imageFileValue = imageFile.value

                if (imageCaptureValue != null && imageFileValue != null) {
                    val outputFileOptions =
                        ImageCapture.OutputFileOptions.Builder(imageFileValue).build()
                    imageCaptureValue.takePicture(
                        outputFileOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                val savedUri = outputFileResults.savedUri
                                val msg = "Photo capture succeeded: $savedUri"
                                aqiResult.value = "Performing AQI analysis..."
                                viewModel.loadCapturedImage(savedUri!!, context)
                            }

                            override fun onError(exception: ImageCaptureException) {
                                val msg = "Photo capture failed: ${exception.message}"
                                aqiResult.value = "Failed to capture image"
                            }
                        })
                }
            },  shape = RoundedCornerShape(8.dp),  colors = ButtonDefaults.buttonColors(backgroundColor = primaryColor)) {
                Text(text = "Capture", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = aqiResult.value)
        }
    }

    androidx.compose.runtime.DisposableEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()
        val imageCaptureValue = imageCapture.value
        val imagePreviewValue = imagePreview.value

        val cameraSelectorValue = cameraSelector



        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            imageCapture.value = ImageCapture.Builder().build()


        }, ContextCompat.getMainExecutor(context))

        onDispose {
            cameraProvider.unbindAll()
        }
    }


}