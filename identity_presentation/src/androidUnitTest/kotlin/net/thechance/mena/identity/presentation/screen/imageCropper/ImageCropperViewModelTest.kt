package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.ui.graphics.ImageBitmap
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.identity.domain.repository.CachedImageRepository
import org.jetbrains.compose.resources.decodeToImageBitmap
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
internal class ImageCropperViewModelTest {
    // Mocks remain the same
    private val imageCacheManager = mockk<CachedImageRepository>(relaxed = true) // Use relaxed mock
    private val imageKey = "profile_image"

    private lateinit var imageCropperViewModel: ImageCropperViewModel
    private lateinit var byteArray: ByteArray
    private lateinit var imageBitmap: ImageBitmap
    private val testDispatcher = StandardTestDispatcher()



    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic("org.jetbrains.compose.resources.ImageDecodersKt")

        byteArray = byteArrayOf()
        imageBitmap = mockk<ImageBitmap>()

        every { imageCacheManager.getCachedImage(imageKey) } returns byteArray
        every { byteArray.decodeToImageBitmap() } returns imageBitmap
        imageCropperViewModel = ImageCropperViewModel(imageKey, imageCacheManager)
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `onCropImage() should send side effect navigate to edit profile screen with cropped image`() =
        runTest {
            imageCropperViewModel.effect.test {
                imageCropperViewModel.onCropImage(imageBitmap)

                val effect = awaitItem()
                assertTrue(effect is ImageCropperScreenEffect.NavigateBackToEditProfileWithImage)
            }
        }

    @Test
    fun `onChangeImage() should update imageBitmap in state`() = runTest {
        val mockBitmap = mockk<ImageBitmap>()

        imageCropperViewModel.state.test {
            awaitItem()

            imageCropperViewModel.onChangeImage(mockBitmap)

            val state = awaitItem()
            assertTrue(state.imageBitmap == mockBitmap)
        }
    }

    @Test
    fun `onNavigateBack() should send side effect navigate to edit profile screen`() =
        runTest {
            imageCropperViewModel.effect.test {
                imageCropperViewModel.onNavigateBack()
                assertTrue(awaitItem() is ImageCropperScreenEffect.NavigateBackToEditProfile)
            }
        }
}