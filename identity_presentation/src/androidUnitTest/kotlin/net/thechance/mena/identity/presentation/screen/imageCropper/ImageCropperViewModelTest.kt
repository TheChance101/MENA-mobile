package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.ui.graphics.ImageBitmap
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.repository.CachedImageRepository
import net.thechance.mena.identity.helper.BaseCoroutineTest
import net.thechance.mena.identity.presentation.utils.ImageDecoder
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
internal class ImageCropperViewModelTest : BaseCoroutineTest() {
    private val imageCacheManager = mockk<CachedImageRepository>()
    private val imageDecoder = mockk<ImageDecoder>()
    private val imageKey = "profile_image"
    private lateinit var imageCropperViewModel: ImageCropperViewModel
    private val imageBitmap: ImageBitmap = mockk<ImageBitmap>()


    @BeforeTest
    override fun setUp() {
        super.setUp()
        every { imageCacheManager.getCachedImage(imageKey) } returns byteArrayOf()
        every { imageDecoder.decodeImage(any()) } returns imageBitmap
        imageCropperViewModel = ImageCropperViewModel(
            imageKey = imageKey,
            cachedImageRepository = imageCacheManager,
            imageDecoder = imageDecoder,
        )
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

        imageCropperViewModel.onChangeImage(imageBitmap)

        assertTrue(imageCropperViewModel.state.value.imageBitmap == imageBitmap)
    }

    @Test
    fun `onNavigateBack() should send side effect navigate to edit profile screen`() = runTest {
        imageCropperViewModel.effect.test {
            imageCropperViewModel.onNavigateBack()
            assertTrue(awaitItem() is ImageCropperScreenEffect.NavigateBackToEditProfile)
        }
    }
}