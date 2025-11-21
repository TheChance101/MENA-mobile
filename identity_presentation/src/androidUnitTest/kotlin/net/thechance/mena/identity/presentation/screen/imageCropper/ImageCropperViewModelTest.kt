package net.thechance.mena.identity.presentation.screen.imageCropper

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.repository.ImagesRepository
import net.thechance.mena.identity.helper.BaseCoroutineTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

internal class ImageCropperViewModelTest : BaseCoroutineTest() {
    private val imageCacheManager = mockk<ImagesRepository>(relaxed = true)
    private lateinit var imageCropperViewModel: ImageCropperViewModel
    private val imageKey = "profile_image"
    private val byteArray = ByteArray(0)


    @BeforeTest
    override fun setUp() {
        super.setUp()
        coEvery { imageCacheManager.getCachedImage(any()) } returns byteArray

        imageCropperViewModel = ImageCropperViewModel(
            imageKey = imageKey,
            imagesRepository = imageCacheManager
        )
    }


    @Test
    fun `onCropImage() should send side effect navigate to edit profile screen with cropped image`() =
        runTest {
            imageCropperViewModel.effect.test {
                imageCropperViewModel.onCropImage(byteArray)

                val effect = awaitItem()
                assertTrue(effect is ImageCropperScreenEffect.NavigateBackToEditProfileWithImage)
            }
        }

    @Test
    fun `onChangeImage() should update imageBitmap in state`() = runTest {
        imageCropperViewModel.onChangeImage(byteArray)

        assertTrue(imageCropperViewModel.state.value.imageByteArray.contentEquals(byteArray))
    }

    @Test
    fun `onNavigateBack() should send side effect navigate to edit profile screen`() = runTest {
        imageCropperViewModel.effect.test {
            imageCropperViewModel.onNavigateBack()
            assertTrue(awaitItem() is ImageCropperScreenEffect.NavigateBackToEditProfile)
        }
    }
}