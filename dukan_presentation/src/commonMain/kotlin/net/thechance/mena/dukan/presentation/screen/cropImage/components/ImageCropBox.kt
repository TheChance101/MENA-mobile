package net.thechance.mena.dukan.presentation.screen.cropImage.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.attafitamim.krop.core.crop.CropState
import com.attafitamim.krop.core.crop.DragHandle
import com.attafitamim.krop.core.crop.animateImgTransform
import com.attafitamim.krop.core.crop.asMatrix
import com.attafitamim.krop.core.crop.cropperTouch
import com.attafitamim.krop.core.images.rememberLoadedImage
import com.attafitamim.krop.core.utils.ZoomLimits
import com.attafitamim.krop.core.utils.constrainOffset
import com.attafitamim.krop.core.utils.times
import com.attafitamim.krop.core.utils.viewMat
import com.attafitamim.krop.ui.disabledSystemGestureArea
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.imageCrop.computeImageBounds
import net.thechance.mena.dukan.presentation.util.imageCrop.createCropperStyle
import net.thechance.mena.dukan.presentation.util.imageCrop.toAspectRatio
import sv.lib.squircleshape.SquircleShape


@Composable
fun ImageCropBox(
    cropState: CropState,
    aspectRatio: Float ,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Black,
    overlayColor: Color = Color.Black.copy(.44f),
    cornerRadius: Dp = Theme.radius.lg,
    strokeWidth: Dp = 4.dp,
    cornerSize: Float = 0.1f,
    handleColor: Color = Theme.colorScheme.primary.onPrimary,
    enableZoomGestures: Boolean = true,
    imageKey: Any = cropState.src,
) {
    val strokeWidthPx = LocalDensity.current.run { strokeWidth.toPx() }
    val style = remember(backgroundColor, overlayColor, strokeWidthPx) {
        createCropperStyle(
            backgroundColor = backgroundColor,
            overlayColor = overlayColor,
            strokeWidth = strokeWidthPx,
            cornerSize = cornerSize,
            handleColor = handleColor
        )
    }

    val imgTransform by animateImgTransform(cropState.transform)

    val imgMat = remember(imgTransform, cropState.src.size) {
        imgTransform.asMatrix(cropState.src.size)
    }
    val viewMat = remember(imageKey) { viewMat() }
    var view by remember { mutableStateOf(IntSize.Zero) }
    var pendingDrag by remember { mutableStateOf<DragHandle?>(null) }
    val zooming = remember { mutableStateOf(false) }

    val totalMat = remember(viewMat.matrix, imgMat) {
        imgMat * viewMat.matrix
    }
    val image = rememberLoadedImage(cropState.src, view, totalMat)

    val canvasBounds = remember(view) {
        if (view.width > 0 && view.height > 0) {
            Rect(
                left = 0f,
                top = 0f,
                right = view.width.toFloat(),
                bottom = view.height.toFloat()
            )
        } else {
            Rect.Zero
        }
    }

    val cropRect = remember(cropState.region, viewMat.matrix, canvasBounds, aspectRatio) {
        val mapped = viewMat.matrix.map(cropState.region)
        val aspectCorrected = mapped.toAspectRatio(aspectRatio)
        aspectCorrected.constrainOffset(canvasBounds)
    }

    val cropPath = remember(cropState.shape, cropRect) {
        cropState.shape.asPath(cropRect)
    }

    val touchRect = remember(cropRect, strokeWidthPx) {
        Rect(
            left = cropRect.left - strokeWidthPx,
            top = cropRect.top - strokeWidthPx,
            right = cropRect.right + strokeWidthPx,
            bottom = cropRect.bottom + strokeWidthPx
        )
    }

    val zoomLimits = remember(cropState.src.size, view) {
        ZoomLimits(cropState.src.size, view)
    }

    LaunchedEffect(view, aspectRatio, cropState.src.size) {
        if (view.width > 0 && view.height > 0) {
            val outer = view.toSize().toRect()
            viewMat.fit(cropState.src.size.toSize().toRect(), outer)

            val imageRect = cropState.src.size.toSize().toRect()
            val centerRegion = imageRect.toAspectRatio(aspectRatio)
            cropState.region = centerRegion
        }
    }
    Canvas(
        modifier = modifier.fillMaxWidth()
            .clip(SquircleShape(cornerRadius))
            .onGloballyPositioned { view = it.size }
            .background(backgroundColor)
            .disabledSystemGestureArea { touchRect }
            .let { mod ->
                if (enableZoomGestures) {
                    mod.cropperTouch(
                        region = cropState.region,
                        onRegion = { newRegion ->
                            val aspectCorrected = newRegion.toAspectRatio(aspectRatio)

                            val imageBoundsInCanvas = computeImageBounds(
                                cropState.src.size.toSize().toRect(),
                                cropState.transform
                            )
                            val constrainedRegion =
                                aspectCorrected.constrainOffset(imageBoundsInCanvas)
                            cropState.region = constrainedRegion
                        },
                        touchRad = 20.dp,
                        handles = style.handles,
                        viewMat = viewMat,
                        pending = pendingDrag,
                        onPending = { pendingDrag = it },
                        zooming = zooming,
                        zoomLimits = zoomLimits,
                    )
                } else mod
            }
    ) {
        image?.let { (params, bitmap) ->
            withTransform({ transform(totalMat) }) {
                drawImage(
                    bitmap,
                    dstOffset = params.subset.topLeft,
                    dstSize = params.subset.size,
                    filterQuality = FilterQuality.Low
                )
            }
            with(style) {
                clipPath(cropPath, ClipOp.Difference) {
                    drawRect(color = overlayColor)
                }
                drawCropRect(cropRect)
            }
        }
    }
}
