package net.thechance.mena.identity.presentation.screen.imageCropper

import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.identity.presentation.utils.ImageCacheManager
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
internal class ImageCropperViewModelTest {
    private val imageCacheManager = mockk<ImageCacheManager>()
    private val imageKey = "profile_image"

    private lateinit var imageBitmap: ImageBitmap

    private lateinit var imageCropperViewModel: ImageCropperViewModel
    private val testDispatcher = StandardTestDispatcher()


    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        imageBitmap = mockk<ImageBitmap>()
        coEvery { imageCacheManager.getCachedImage(imageKey) } returns imageBitmap
        imageCropperViewModel = ImageCropperViewModel(imageKey, imageCacheManager)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onCropImage should send side effect navigate to edit profile screen with cropped image`() =
        runTest {
            imageCropperViewModel.effect.test {

                imageCropperViewModel.onCropImage(imageBitmap)

                val effect = awaitItem()
                assertTrue(effect is ImageCropperScreenEffect.NavigateBackToEditProfileWithImage)
            }
        }

    @Test
    fun `onChangeImage should update imageBitmap in state`() = runTest {

        imageCropperViewModel.onChangeImage(imageBitmap)

        imageCropperViewModel.state.test {
            val state = awaitItem()
             assertTrue(state.imageBitmap == imageBitmap)
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