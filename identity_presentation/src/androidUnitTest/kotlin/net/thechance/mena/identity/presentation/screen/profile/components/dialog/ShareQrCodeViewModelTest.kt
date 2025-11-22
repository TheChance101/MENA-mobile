package net.thechance.mena.identity.presentation.screen.profile.components.dialog

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.repository.ImagesRepository
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.helper.BaseCoroutineTest
import net.thechance.mena.identity.presentation.screen.profile.components.share.ShareDialogViewModel
import net.thechance.mena.identity.presentation.screen.profile.components.share.ShareQrCodeUIEffect
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionHandler
import net.thechance.mena.identity.presentation.util.permissionHandler.PermissionState
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShareQrCodeViewModelTest : BaseCoroutineTest() {

    private val imagesRepository = mockk<ImagesRepository>(relaxed = true)
    private val galleryPermissionHandler = mockk<PermissionHandler>(relaxed = true)
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ShareDialogViewModel
    private val byteArray = ByteArray(0)

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
    fun `onClickDownload should save image when permission is granted`() = runTest(testDispatcher) {
        every { galleryPermissionHandler.checkPermission() } returns PermissionState.GRANTED
        coEvery { imagesRepository.saveImageToGallery(any()) } returns Unit

        viewModel.onClickDownload(byteArray)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { imagesRepository.saveImageToGallery(any()) }
    }

    @Test
    fun `onClickDownload should emit OnClickDownload effect when permission is granted and save succeeds`() =
        runTest(testDispatcher) {
            every { galleryPermissionHandler.checkPermission() } returns PermissionState.GRANTED
            coEvery { imagesRepository.saveImageToGallery(any()) } returns Unit

            viewModel.effect.test {
                viewModel.onClickDownload(byteArray)
                testDispatcher.scheduler.advanceUntilIdle()

                assertThat(awaitItem()).isEqualTo(ShareQrCodeUIEffect.ShowClickDownloadSnackBar)
            }
        }

    @Test
    fun `onClickDownload should request permission when permission is not determined`() =
        runTest(testDispatcher) {
            every { galleryPermissionHandler.checkPermission() } returns PermissionState.NOT_DETERMINED

            viewModel.onClickDownload(byteArray)

            verify { galleryPermissionHandler.requestPermission() }
        }

    @Test
    fun `onClickDownload should request permission when permission is denied`() =
        runTest(testDispatcher) {
            every { galleryPermissionHandler.checkPermission() } returns PermissionState.DENIED

            viewModel.onClickDownload(byteArray)

            verify { galleryPermissionHandler.requestPermission() }
        }

    @Test
    fun `onClickDownload should open settings when permission is denied permanently`() =
        runTest(testDispatcher) {
            every { galleryPermissionHandler.checkPermission() } returns PermissionState.DENIED_PERMANENTLY

            viewModel.onClickDownload(byteArray)

            verify { galleryPermissionHandler.openSettingPage() }
        }

    @Test
    fun `onClickDownload should show snack bar with error message when save fails`() = runTest(testDispatcher) {
        every { galleryPermissionHandler.checkPermission() } returns PermissionState.GRANTED
        coEvery { imagesRepository.saveImageToGallery(any()) } throws Exception("Save failed")

        viewModel.onClickDownload(byteArray)

        viewModel.effect.test {
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isInstanceOf(ShareQrCodeUIEffect.ShowSnackBarError::class)
        }
    }

    @Test
    fun `initial state should have null qrCodeUrl`() {
        assertThat(viewModel.state.value.shareLinkUrl).isEmpty()
    }
}