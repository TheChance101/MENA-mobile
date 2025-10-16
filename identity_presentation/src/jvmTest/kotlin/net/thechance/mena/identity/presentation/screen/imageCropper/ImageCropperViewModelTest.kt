package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.ui.graphics.ImageBitmap
import app.cash.turbine.test
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
internal class ImageCropperViewModelTest {
    private val imageBitmap = mockk<ImageBitmap>()
    private lateinit var imageCropperViewModel: ImageCropperViewModel
    private val testDispatcher = StandardTestDispatcher()


    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        imageCropperViewModel = ImageCropperViewModel(imageBitmap)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onCropImage should send side effect navigate to edit profile screen with cropped image`() =
        runTest {
            imageCropperViewModel.effect.test {
                val imageBitmap = mockk<ImageBitmap>()

                imageCropperViewModel.onCropImage(imageBitmap)

                val effect = awaitItem()
                assertTrue(effect is ImageCropperScreenEffect.NavigateBackToEditProfileWithImage)
            }
        }

    @Test
    fun `onChangeImage should update imageBitmap in state`() = runTest {
        val imageBitmap = mockk<ImageBitmap>(relaxed = true)

        imageCropperViewModel.onChangeImage(imageBitmap)

        imageCropperViewModel.state.test {
            val state = awaitItem()
            assertTrue(state.imageBitmap.width == imageBitmap.width)
        }

    }

    @Test
    fun `onNavigateBack should send side effect navigate to edit profile screen`() =
        runTest {
            imageCropperViewModel.effect.test {
                imageCropperViewModel.onNavigateBack()

                assertTrue(awaitItem() is ImageCropperScreenEffect.NavigateBackToEditProfile)
            }
        }
}