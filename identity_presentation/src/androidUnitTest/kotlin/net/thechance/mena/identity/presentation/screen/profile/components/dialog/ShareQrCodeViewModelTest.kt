package net.thechance.mena.identity.presentation.screen.profile.components.dialog

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
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
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.helper.BaseCoroutineTest
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionHandler
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ShareQrCodeViewModelTest : BaseCoroutineTest() {

    private lateinit var viewModel: ShareDialogViewModel
    private val imagesRepository = mockk<ImagesRepository>(relaxed = true)
    private val galleryPermissionHandler = mockk<PermissionHandler>(relaxed = true)
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private val painter = mockk<Painter>()
    private val imageSize = mockk<IntSize>()
    private val density = mockk<Density>()
    private val layoutDirection = mockk<LayoutDirection>()

    @Before
    override fun setUp() {
        super.setUp()
        viewModel = ShareDialogViewModel(
            imagesRepository = imagesRepository,
            galleryPermissionHandler = galleryPermissionHandler,
            userRepository = userRepository,
            dispatcher = testDispatcher
        )
    }

    @Test
    @org.junit.Ignore("Requires Android runtime for ImageBitmap creation")
    fun `onClickDownload should save image when permission is granted`() = runTest(testDispatcher) {
        every { galleryPermissionHandler.checkPermission() } returns PermissionState.GRANTED
        coEvery { imagesRepository.saveImageToGallery(any()) } returns Unit

        viewModel.onClickDownload(painter, imageSize, density, layoutDirection)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { imagesRepository.saveImageToGallery(any()) }
    }

    @Test
    @org.junit.Ignore("Requires Android runtime for ImageBitmap creation")
    fun `onClickDownload should emit OnClickDownload effect when permission is granted and save succeeds`() =
        runTest(testDispatcher) {
            every { galleryPermissionHandler.checkPermission() } returns PermissionState.GRANTED
            coEvery { imagesRepository.saveImageToGallery(any()) } returns Unit

            viewModel.effect.test {
                viewModel.onClickDownload(painter, imageSize, density, layoutDirection)
                testDispatcher.scheduler.advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(ShareQrCodeUIEffect.OnClickDownload)
            }
        }

    @Test
    @org.junit.Ignore("Requires Android runtime for ImageBitmap creation")
    fun `onClickDownload should request permission when permission is not determined`() =
        runTest(testDispatcher) {
            every { galleryPermissionHandler.checkPermission() } returns PermissionState.NOT_DETERMINED

            viewModel.onClickDownload(painter, imageSize, density, layoutDirection)

            verify { galleryPermissionHandler.requestPermission() }
        }

    @Test
    @org.junit.Ignore("Requires Android runtime for ImageBitmap creation")
    fun `onClickDownload should request permission when permission is denied`() =
        runTest(testDispatcher) {
            every { galleryPermissionHandler.checkPermission() } returns PermissionState.DENIED

            viewModel.onClickDownload(painter, imageSize, density, layoutDirection)

            verify { galleryPermissionHandler.requestPermission() }
        }

    @Test
    @org.junit.Ignore("Requires Android runtime for ImageBitmap creation")
    fun `onClickDownload should open settings when permission is denied permanently`() =
        runTest(testDispatcher) {
            every { galleryPermissionHandler.checkPermission() } returns PermissionState.DENIED_PERMANENTLY

            viewModel.onClickDownload(painter, imageSize, density, layoutDirection)

            verify { galleryPermissionHandler.openSettingPage() }
        }

    @Test
    @org.junit.Ignore("Requires Android runtime for ImageBitmap creation")
    fun `onClickDownload should set error message when save fails`() = runTest(testDispatcher) {
        every { galleryPermissionHandler.checkPermission() } returns PermissionState.GRANTED
        coEvery { imagesRepository.saveImageToGallery(any()) } throws Exception("Save failed")

        viewModel.onClickDownload(painter, imageSize, density, layoutDirection)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.errorMessage != null)
    }

    @Test
    fun `initial state should have null error message`() {
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `initial state should have empty string qrCodeUrl`() {
        assertThat(viewModel.state.value.shareLinkUrl).isEmpty()
    }
}