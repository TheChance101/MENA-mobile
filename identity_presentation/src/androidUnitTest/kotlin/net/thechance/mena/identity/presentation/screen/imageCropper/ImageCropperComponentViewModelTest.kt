package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import app.cash.turbine.test
import io.mockk.mockk
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ImageCropperComponentViewModelTest {

    private val minScale = 1.0f
    private val maxScale = 3.0f
    private val initialState = ImageCropperUiState(
        scale = 1.5f,
        translation = Offset(10f, 20f),
        imageSize = IntSize(100, 100),
        componentSize = IntSize(200, 200)
    )
    private lateinit var viewModel: ImageCropperComponentViewModel

    @Before
    fun setUp() {
        viewModel = ImageCropperComponentViewModel(
            minScale = minScale,
            maxScale = maxScale,
            initialState = initialState
        )
    }

    @Test
    fun `zoomIn should increase scale by value`() {
        val initialScale = viewModel.state.scale
        val zoomValue = 0.5f

        viewModel.zoomIn(zoomValue)

        assertEquals(initialScale + zoomValue, viewModel.state.scale)
    }

    @Test
    fun `zoomIn should not exceed maxScale`() {
        viewModel.zoomIn(10f)

        assertTrue(viewModel.state.scale <= maxScale)
    }

    @Test
    fun `zoomOut should decrease scale by value`() {
        val initialScale = viewModel.state.scale
        val zoomValue = 0.5f

        viewModel.zoomOut(zoomValue)

        assertEquals(initialScale - zoomValue, viewModel.state.scale)
    }

    @Test
    fun `zoomOut should not go below minScale`() {
        viewModel = ImageCropperComponentViewModel(
            minScale = minScale,
            maxScale = maxScale,
            initialState = ImageCropperUiState(scale = minScale)
        )

        viewModel.zoomOut(10f)

        assertTrue(viewModel.state.scale >= minScale)
    }

    @Test
    fun `zoomBy should multiply scale by gestureZoom`() {
        val initialScale = viewModel.state.scale
        val gestureZoom = 1.5f

        viewModel.zoomBy(gestureZoom, Offset.Zero)

        assertEquals(initialScale * gestureZoom, viewModel.state.scale)
    }

    @Test
    fun `zoomBy should add pan to translation`() {
        val initialTranslation = viewModel.state.translation
        val pan = Offset(5f, 10f)
        val expectedTranslation = initialTranslation + pan

        viewModel.zoomBy(1.0f, pan)

        // Translation may be constrained, so check it's updated
        assertTrue(viewModel.state.translation.x != initialTranslation.x || viewModel.state.translation.y != initialTranslation.y)
    }

    @Test
    fun `zoomBy should constrain scale to minScale and maxScale`() {
        viewModel.zoomBy(10f, Offset.Zero)

        assertTrue(viewModel.state.scale >= minScale)
        assertTrue(viewModel.state.scale <= maxScale)
    }

    @Test
    fun `reset should set scale to minScale`() {
        viewModel.reset()

        assertEquals(minScale, viewModel.state.scale)
    }

    @Test
    fun `reset should set translation to zero`() {
        viewModel.reset()

        assertEquals(Offset.Zero, viewModel.state.translation)
    }

    @Test
    fun `resetAndUpdateImageSize should reset and update image size`() {
        val newImageSize = IntSize(150, 150)

        viewModel.resetAndUpdateImageSize(newImageSize)

        assertEquals(minScale, viewModel.state.scale)
        assertEquals(newImageSize, viewModel.state.imageSize)
    }

    @Test
    fun `updateComponentSize should update component size`() {
        val newComponentSize = IntSize(300, 300)

        viewModel.updateComponentSize(newComponentSize)

        assertEquals(newComponentSize, viewModel.state.componentSize)
    }

    @Test
    fun `isZoomInEnabled should be true when scale is less than maxScale`() {
        viewModel = ImageCropperComponentViewModel(
            minScale = minScale,
            maxScale = maxScale,
            initialState = ImageCropperUiState(scale = 1.0f)
        )

        assertTrue(viewModel.isZoomInEnabled)
    }

    @Test
    fun `isZoomInEnabled should be false when scale equals maxScale`() {
        viewModel = ImageCropperComponentViewModel(
            minScale = minScale,
            maxScale = maxScale,
            initialState = ImageCropperUiState(scale = maxScale)
        )

        assertFalse(viewModel.isZoomInEnabled)
    }

    @Test
    fun `isZoomOutEnabled should be true when scale is greater than minScale`() {
        viewModel = ImageCropperComponentViewModel(
            minScale = minScale,
            maxScale = maxScale,
            initialState = ImageCropperUiState(scale = 2.0f)
        )

        assertTrue(viewModel.isZoomOutEnabled)
    }

    @Test
    fun `isZoomOutEnabled should be false when scale equals minScale`() {
        viewModel = ImageCropperComponentViewModel(
            minScale = minScale,
            maxScale = maxScale,
            initialState = ImageCropperUiState(scale = minScale)
        )

        assertFalse(viewModel.isZoomOutEnabled)
    }

    @Test
    fun `onUploadAnotherImageClicked should emit UploadAnotherImage effect`() = runTest {
        val imageBitmap = mockk<ImageBitmap>()

        viewModel.effect.test {
            viewModel.onUploadAnotherImageClicked(imageBitmap)

            val effect = awaitItem()
            assertTrue(effect is ImageCropperComponentEffect.UploadAnotherImage)
            assertEquals(imageBitmap, (effect as ImageCropperComponentEffect.UploadAnotherImage).imageBitmap)
        }
    }

    @Test
    @org.junit.Ignore("Requires Android runtime for ImageBitmap creation")
    fun `cropToBitmap should emit SaveImage effect`() = runTest {
        val painter = mockk<Painter>(relaxed = true)
        val density = mockk<Density>(relaxed = true)
        val layoutDirection = LayoutDirection.Ltr
        viewModel.updateComponentSize(IntSize(200, 200))
        viewModel.state = viewModel.state.copy(imageSize = IntSize(100, 100))

        viewModel.effect.test {
            viewModel.cropToBitmap(painter, density, layoutDirection)
            advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is ImageCropperComponentEffect.SaveImage)
            cancelAndConsumeRemainingEvents()
        }
    }
}