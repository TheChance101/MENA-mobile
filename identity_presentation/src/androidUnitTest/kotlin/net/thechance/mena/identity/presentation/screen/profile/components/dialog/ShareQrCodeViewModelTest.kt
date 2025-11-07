package net.thechance.mena.identity.presentation.screen.profile.components.dialog

import androidx.compose.ui.graphics.ImageBitmap
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.repository.ImagesRepository
import net.thechance.mena.identity.helper.BaseCoroutineTest
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionHandler
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import net.thechance.mena.identity.presentation.utils.ImageDecoder
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ShareQrCodeViewModelTest : BaseCoroutineTest() {

    private val imagesRepository = mockk<ImagesRepository>(relaxed = true)
    private val galleryPermissionHandler = mockk<PermissionHandler>(relaxed = true)
    private val imageDecoder = mockk<ImageDecoder>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ShareQrCodeViewModel
    private val imageBitmap: ImageBitmap = mockk<ImageBitmap>(relaxed = true)

    @Before
    override fun setUp() {
        super.setUp()
        viewModel = ShareQrCodeViewModel(
            imagesRepository = imagesRepository,
            galleryPermissionHandler = galleryPermissionHandler,
            imageDecoder = imageDecoder,
            dispatcher = testDispatcher
        )
    }

    @Test
    fun `onClickDownload should save image when permission is granted`() = runTest(testDispatcher) {
        every { galleryPermissionHandler.checkPermission() } returns PermissionState.GRANTED
        coEvery { imageDecoder.encodeImage(imageBitmap) } returns byteArrayOf(1, 2, 3)
        coEvery { imagesRepository.saveImageToGallery(any()) } returns Unit

        viewModel.onClickDownload(imageBitmap)
        advanceUntilIdle()

        coVerify(exactly = 1) { imagesRepository.saveImageToGallery(byteArrayOf(1, 2, 3)) }
    }

    @Test
    fun `onClickDownload should emit OnClickDownload effect when permission is granted and save succeeds`() =
        runTest(testDispatcher) {
            every { galleryPermissionHandler.checkPermission() } returns PermissionState.GRANTED
            coEvery { imageDecoder.encodeImage(imageBitmap) } returns byteArrayOf(1, 2, 3)
            coEvery { imagesRepository.saveImageToGallery(any()) } returns Unit

            viewModel.effect.test {
                viewModel.onClickDownload(imageBitmap)
                advanceUntilIdle()

                val effect = awaitItem()
                assertTrue(effect is ShareQrCodeUIEffect.OnClickDownload)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `onClickDownload should request permission when permission is not determined`() =
        runTest(testDispatcher) {
            every { galleryPermissionHandler.checkPermission() } returns PermissionState.NOT_DETERMINED

            viewModel.onClickDownload(imageBitmap)

            verify { galleryPermissionHandler.requestPermission() }
        }

    @Test
    fun `onClickDownload should request permission when permission is denied`() =
        runTest(testDispatcher) {
            every { galleryPermissionHandler.checkPermission() } returns PermissionState.DENIED

            viewModel.onClickDownload(imageBitmap)

            verify { galleryPermissionHandler.requestPermission() }
        }

    @Test
    fun `onClickDownload should open settings when permission is denied permanently`() =
        runTest(testDispatcher) {
            every { galleryPermissionHandler.checkPermission() } returns PermissionState.DENIED_PERMANENTLY

            viewModel.onClickDownload(imageBitmap)

            verify { galleryPermissionHandler.openSettingPage() }
        }

    @Test
    fun `onClickDownload should set error message when save fails`() = runTest(testDispatcher) {
        every { galleryPermissionHandler.checkPermission() } returns PermissionState.GRANTED
        coEvery { imageDecoder.encodeImage(imageBitmap) } returns byteArrayOf(1, 2, 3)
        coEvery { imagesRepository.saveImageToGallery(any()) } throws Exception("Save failed")

        viewModel.onClickDownload(imageBitmap)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.errorMessage != null)
    }

    @Test
    fun `initial state should have null error message`() {
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `initial state should have null qrCodeUrl`() {
        assertNull(viewModel.state.value.qrCodeUrl)
    }
}