//@file:OptIn(ExperimentalForeignApi::class)
//
//package net.thechance.mena.core_chat.presentation.utils
//
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.interop.UIKitView
//import androidx.compose.ui.unit.dp
//import kotlinx.cinterop.*
//import net.thechance.mena.core_chat.presentation.screen.chat.components.CancelDialogIcon
//import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
//import net.thechance.mena.designsystem.presentation.theme.theme.Theme
//import platform.AVFoundation.*
//import platform.CoreGraphics.CGRectMake
//import platform.Foundation.NSData
//import platform.Foundation.NSError
//import platform.Foundation.getBytes
//import platform.UIKit.UIView
//import platform.darwin.NSObject
//
//@Composable
//actual fun CameraView(
//    onImageCaptured: (ByteArray) -> Unit,
//    onClose: () -> Unit
//) {
//    var hasPermission by remember { mutableStateOf(false) }
//    val captureSession = remember { AVCaptureSession() }
//    val photoOutput = remember { AVCapturePhotoOutput() }
//    val previewLayer = remember { AVCaptureVideoPreviewLayer(session = captureSession) }
//
//    LaunchedEffect(Unit) {
//        AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
//            hasPermission = granted
//        }
//    }
//
//    DisposableEffect(hasPermission) {
//        if (hasPermission) {
//            val device = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
//            if (device != null) {
//                val input = AVCaptureDeviceInput.deviceInputWithDevice(device, error = null)
//                if (input != null && captureSession.canAddInput(input) && captureSession.canAddOutput(photoOutput)) {
//                    captureSession.addInput(input as AVCaptureInput)
//                    captureSession.addOutput(photoOutput)
//                    captureSession.startRunning()
//                }
//            }
//        }
//        onDispose {
//            if (captureSession.isRunning()) {
//                captureSession.stopRunning()
//            }
//        }
//    }
//
//    if (!hasPermission) {
//        return
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        UIKitView(
//            modifier = Modifier.fillMaxSize(),
//            factory = {
//                val container = UIView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0))
//                previewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
//                container.layer.addSublayer(previewLayer)
//                container
//            },
//            update = { view ->
//                previewLayer.frame = view.bounds
//            }
//        )
//
//        Column(
//            modifier = Modifier
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
//                modifier = Modifier.fillMaxWidth(),
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
//                            val settings = AVCapturePhotoSettings.photoSettings()
//                            photoOutput.capturePhotoWithSettings(
//                                settings,
//                                object : NSObject(), AVCapturePhotoCaptureDelegateProtocol {
//                                    override fun captureOutput(
//                                        output: AVCapturePhotoOutput,
//                                        didFinishProcessingPhoto: AVCapturePhoto,
//                                        error: NSError?
//                                    ) {
//                                        if (error != null) {
//                                            println("Error: $error")
//                                            return
//                                        }
//
//                                        val photoData: NSData? =
//                                            didFinishProcessingPhoto.fileDataRepresentation()
//                                        if (photoData != null) {
//                                            onImageCaptured(photoData.toByteArray())
//                                        }
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
//
//fun NSData.toByteArray(): ByteArray {
//    val length = this.length.toInt()
//    if (length == 0) return ByteArray(0)
//    val byteArray = ByteArray(length)
//    byteArray.usePinned { pinnedArray ->
//        this.getBytes(pinnedArray.addressOf(0), length.toULong())
//    }
//    return byteArray
//}