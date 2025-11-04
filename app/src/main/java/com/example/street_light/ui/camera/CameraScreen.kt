@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.street_light.ui.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.street_light.StreetLightApplication
import com.example.street_light.data.model.IndianScript
import com.example.street_light.ui.navigation.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import java.util.concurrent.Executors

/**
 * Camera screen with OCR and transliteration
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: CameraViewModel = viewModel(
        factory = CameraViewModelFactory(
            application = LocalContext.current.applicationContext as StreetLightApplication
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Camera permission
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    if (!cameraPermissionState.status.isGranted) {
        LaunchedEffect(Unit) {
            cameraPermissionState.launchPermissionRequest()
        }
        PermissionRequestScreen()
        return
    }

    // Camera setup
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var preview: Preview? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        val cameraProvider = cameraProviderFuture.await(context)
        preview = Preview.Builder().build()
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Street Sign Reader") },
                actions = {
                    IconButton(onClick = { onNavigate(Screen.History) }) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                    IconButton(onClick = { onNavigate(Screen.Settings) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Camera Preview
            preview?.let { previewInstance ->
                CameraPreview(
                    modifier = Modifier.weight(1f),
                    preview = previewInstance
                )
            }

            // Script selector
            ScriptSelector(
                selectedScript = uiState.targetScript,
                onScriptSelected = viewModel::setTargetScript,
                modifier = Modifier.fillMaxWidth()
            )

            // Capture button
            Button(
                onClick = {
                    imageCapture?.let { capture ->
                        capture.takePicture(
                            Executors.newSingleThreadExecutor(),
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                    val bitmap = imageProxyToBitmap(imageProxy)
                                    if (bitmap != null) {
                                        viewModel.captureAndProcess(bitmap)
                                    } else {
                                        viewModel.setError("Failed to capture image")
                                    }
                                    imageProxy.close()
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    exception.printStackTrace()
                                    viewModel.clearError()
                                }
                            }
                        )
                    }
                },
                enabled = !uiState.isProcessing && imageCapture != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (uiState.isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Processing...")
                } else {
                    Text("Capture & Transliterate")
                }
            }
        }
    }

    // Result Dialog
    uiState.lastResult?.let { result ->
        if (uiState.showResult) {
            TransliterationResultDialog(
                result = result,
                onDismiss = { viewModel.clearResult() }
            )
        }
    }

    // Error Dialog
    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    preview: Preview
) {
    AndroidView(
        factory = { context ->
            PreviewView(context).also { previewView ->
                preview.setSurfaceProvider(previewView.surfaceProvider)
            }
        },
        modifier = modifier
    )
}

@Composable
fun ScriptSelector(
    selectedScript: IndianScript,
    onScriptSelected: (IndianScript) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.padding(16.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = "${selectedScript.displayName} (${selectedScript.nativeName})",
                onValueChange = {},
                readOnly = true,
                label = { Text("Target Script") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                IndianScript.entries.forEach { script ->
                    DropdownMenuItem(
                        text = { Text("${script.displayName} (${script.nativeName})") },
                        onClick = {
                            onScriptSelected(script)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TransliterationResultDialog(
    result: com.example.street_light.data.model.TransliterationResult,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Transliteration Result") },
        text = {
            Column {
                Text(
                    text = "Original:",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = result.originalText,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Transliterated:",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = result.transliteratedText,
                    style = MaterialTheme.typography.headlineSmall
                )
                result.detectedScript?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Detected Script: ${it.displayName}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun PermissionRequestScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Camera Permission Required",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Please grant camera permission to use this app",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Helper function to convert ImageProxy to Bitmap
private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
    return try {
        val yBuffer = imageProxy.planes[0].buffer
        val uBuffer = imageProxy.planes[1].buffer
        val vBuffer = imageProxy.planes[2].buffer
        
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        
        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)
        
        val yuvImage = android.graphics.YuvImage(
            nv21,
            android.graphics.ImageFormat.NV21,
            imageProxy.width,
            imageProxy.height,
            null
        )
        val out = java.io.ByteArrayOutputStream()
        yuvImage.compressToJpeg(
            android.graphics.Rect(0, 0, imageProxy.width, imageProxy.height),
            90,
            out
        )
        val imageBytes = out.toByteArray()
        var bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        
        // Rotate if needed
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        if (rotationDegrees != 0) {
            val matrix = Matrix().apply {
                postRotate(rotationDegrees.toFloat())
            }
            bitmap = Bitmap.createBitmap(
                bitmap,
                0, 0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Helper extension for ListenableFuture from Guava
private suspend fun <T> com.google.common.util.concurrent.ListenableFuture<T>.await(context: android.content.Context): T {
    return kotlinx.coroutines.suspendCancellableCoroutine { cont ->
        addListener({
            try {
                cont.resume(get())
            } catch (e: Exception) {
                cont.resumeWithException(e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}

// Factory for CameraViewModel
class CameraViewModelFactory(
    private val application: StreetLightApplication
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraViewModel(
                application = application,
                ocrService = application.ocrService,
                transliterationRepository = application.transliterationRepository,
                preferences = application.preferences
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}