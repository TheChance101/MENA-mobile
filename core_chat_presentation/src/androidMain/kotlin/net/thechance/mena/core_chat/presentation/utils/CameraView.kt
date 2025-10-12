//package net.thechance.mena.core_chat.presentation.utils
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.util.Log
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageCapture
//import androidx.camera.core.ImageCaptureException
//import androidx.camera.core.ImageProxy
//import androidx.camera.core.Preview
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.camera.view.PreviewView
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.systemBarsPadding
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalLifecycleOwner
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.core.content.ContextCompat
//import net.thechance.mena.core_chat.presentation.screen.chat.components.CancelDialogIcon
//import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
//import net.thechance.mena.designsystem.presentation.theme.theme.Theme
//import java.io.ByteArrayOutputStream
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//
//@Composable
//actual fun CameraView(
//    onImageCaptured: (ByteArray) -> Unit,
//    onClose: () -> Unit
//) {
//    val context = LocalContext.current
//    val lifeCycleOwner = LocalLifecycleOwner.current
//
//    var hasPermission by remember { mutableStateOf(false) }
//    val cameraPermissionLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted -> hasPermission = isGranted }
//
//    LaunchedEffect(Unit) {
//        val permission = Manifest.permission.CAMERA
//        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
//            hasPermission = true
//        } else {
//            cameraPermissionLauncher.launch(permission)
//        }
//    }
//
//    if (!hasPermission) return
//
//    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
//    val previewView = remember { PreviewView(context) }
//    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
//    val imageCapture = remember { ImageCapture.Builder().build() }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        AndroidView(
//            factory = {
//                val cameraProvider = cameraProviderFuture.get()
//                val preview = Preview.Builder().build().also {
//                    it.setSurfaceProvider(previewView.surfaceProvider)
//                }
//
//                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//                try {
//                    cameraProvider.unbindAll()
//                    cameraProvider.bindToLifecycle(
//                        lifeCycleOwner,
//                        cameraSelector,
//                        preview,
//                        imageCapture
//                    )
//                } catch (e: Exception) {
//                    Log.e("CameraView", "Camera binding failed", e)
//                }
//
//                previewView
//            },
//            modifier = Modifier.fillMaxSize()
//        )
//
//        Column(
//            modifier = Modifier
//                .systemBarsPadding()
//                .fillMaxSize()
//                .padding(Theme.spacing._16),
//            verticalArrangement = Arrangement.SpaceBetween,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            CancelDialogIcon(
//                onClick = onClose,
//                modifier = Modifier.align(Alignment.Start),
//            )
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(90.dp)
//                        .border(
//                            width = 4.dp,
//                            color = Theme.colorScheme.primary.onPrimary,
//                            shape = CircleShape
//                        ),
//                    contentAlignment = Alignment.Center
//                ) {
//                    PrimaryButton(
//                        modifier = Modifier.size(70.dp),
//                        shape = CircleShape,
//                        containerColor = Theme.colorScheme.primary.onPrimary,
//                        onClick = {
//                            imageCapture.takePicture(
//                                cameraExecutor,
//                                object : ImageCapture.OnImageCapturedCallback() {
//                                    override fun onCaptureSuccess(image: ImageProxy) {
//                                        val bitmap = image.toBitmap()
//                                        val outputStream = ByteArrayOutputStream()
//                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
//                                        onImageCaptured(outputStream.toByteArray())
//                                        image.close()
//                                    }
//
//                                    override fun onError(exception: ImageCaptureException) {
//                                        Log.e("CameraView", "Image capture failed: ${exception.message}", exception)
//                                    }
//                                }
//                            )
//                        },
//                        text = ""
//                    )
//                }
//            }
//        }
//    }
//}