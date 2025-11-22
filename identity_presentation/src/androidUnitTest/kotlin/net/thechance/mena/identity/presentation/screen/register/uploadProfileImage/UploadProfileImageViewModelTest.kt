package net.thechance.mena.identity.presentation.screen.register.uploadProfileImage

import androidx.compose.ui.graphics.ImageBitmap
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.AuthenticationTokens
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.repository.ImagesRepository
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.helper.BaseCoroutineTest
import net.thechance.mena.identity.presentation.utils.ImageDecoder
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UploadProfileImageViewModelTest : BaseCoroutineTest() {
    private lateinit var viewModel: UploadProfileImageViewModel
    private val cachedImageRepository = mockk<ImagesRepository>()
    private val userRepository = mockk<UserRepository>()
    private val imageDecoder = mockk<ImageDecoder>()
    private val authenticationRepository = mockk<AuthenticationRepository>()
    private val registrationDraftRepository = mockk<RegistrationDraftRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val phoneNumber = PhoneNumber("+964", "7901234567")
    private val authTokens = AuthenticationTokens(
        accessToken = "test_access_token",
        refreshToken = "test_refresh_token"
    )
    private val imageBitmap: ImageBitmap = mockk<ImageBitmap>()
    private val imageBytes = byteArrayOf(1, 2, 3)

    @Before
    fun setup() {
        viewModel = UploadProfileImageViewModel(
            cachedImageRepository = cachedImageRepository,
            userRepository = userRepository,
            imageDecoder = imageDecoder,
            authenticationRepository = authenticationRepository,
            registrationDraftRepository = registrationDraftRepository,
            authTokens = authTokens,
            phoneNumber = phoneNumber,
            dispatcher = testDispatcher
        )
    }

    @Test
    fun `loadSavedImage should load cached image when phoneNumber exists`() = runTest {
        coEvery { cachedImageRepository.getCachedImage(any()) } returns imageBytes
        every { imageDecoder.decodeImage(imageBytes) } returns imageBitmap

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(imageBitmap, viewModel.state.value.imageBitmap)
    }

    @Test
    fun `loadSavedImage should not load cached image when phoneNumber is null`() = runTest {
        val viewModelWithoutPhone = UploadProfileImageViewModel(
            cachedImageRepository = cachedImageRepository,
            userRepository = userRepository,
            imageDecoder = imageDecoder,
            authenticationRepository = authenticationRepository,
            registrationDraftRepository = registrationDraftRepository,
            authTokens = authTokens,
            phoneNumber = null,
            dispatcher = testDispatcher
        )

        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModelWithoutPhone.state.value.imageBitmap)
    }

    @Test
    fun `onSelectImage should update state with selected image`() {
        coEvery { imageDecoder.encodeImage(imageBitmap) } returns imageBytes
        coEvery { cachedImageRepository.cacheImage(any(), any()) } returns Unit

        viewModel.onSelectImage(imageBitmap)

        assertEquals(imageBitmap, viewModel.state.value.imageBitmap)
    }

    @Test
    fun `onSelectImage should enable upload button`() {
        coEvery { imageDecoder.encodeImage(imageBitmap) } returns imageBytes
        coEvery { cachedImageRepository.cacheImage(any(), any()) } returns Unit

        viewModel.onSelectImage(imageBitmap)

        assertTrue(viewModel.state.value.isUploadEnabled)
    }

    @Test
    fun `onSelectImage should save image to cache`() = runTest {
        coEvery { imageDecoder.encodeImage(imageBitmap) } returns imageBytes
        coEvery { cachedImageRepository.cacheImage(any(), any()) } returns Unit

        viewModel.onSelectImage(imageBitmap)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { cachedImageRepository.cacheImage(any(), imageBytes) }
    }

    @Test
    fun `onImageCropped should update state with cropped image`() {
        coEvery { imageDecoder.encodeImage(imageBitmap) } returns imageBytes
        coEvery { cachedImageRepository.cacheImage(any(), any()) } returns Unit

        viewModel.onImageCropped(imageBitmap)

        assertEquals(imageBitmap, viewModel.state.value.imageBitmap)
    }

    @Test
    fun `onImageCropped should save cropped image to cache`() = runTest {
        coEvery { imageDecoder.encodeImage(imageBitmap) } returns imageBytes
        coEvery { cachedImageRepository.cacheImage(any(), any()) } returns Unit

        viewModel.onImageCropped(imageBitmap)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { cachedImageRepository.cacheImage(any(), imageBytes) }
    }

    @Test
    fun `onClickUpload should not start upload when imageBitmap is null`() = runTest {
        viewModel.onClickUpload()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `onClickUpload should not start upload when authTokens is null`() = runTest {
        val viewModelWithoutTokens = UploadProfileImageViewModel(
            cachedImageRepository = cachedImageRepository,
            userRepository = userRepository,
            imageDecoder = imageDecoder,
            authenticationRepository = authenticationRepository,
            registrationDraftRepository = registrationDraftRepository,
            authTokens = null,
            phoneNumber = phoneNumber,
            dispatcher = testDispatcher
        )
        coEvery { imageDecoder.encodeImage(imageBitmap) } returns imageBytes
        coEvery { cachedImageRepository.cacheImage(any(), any()) } returns Unit
        viewModelWithoutTokens.onSelectImage(imageBitmap)

        viewModelWithoutTokens.onClickUpload()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModelWithoutTokens.state.value.isLoading)
    }

    @Test
    fun `onClickUpload should set isLoading to true`() = runTest {
        coEvery { imageDecoder.encodeImage(imageBitmap) } returns imageBytes
        coEvery { cachedImageRepository.cacheImage(any(), any()) } returns Unit
        coEvery { authenticationRepository.saveAuthTokensWithoutEmit(any()) } returns Unit
        coEvery { userRepository.uploadUserProfileImage(any()) } returns Unit
        coEvery { registrationDraftRepository.setImageUploadCompleted(any()) } returns Unit
        coEvery { cachedImageRepository.removeCachedImage(any()) } returns Unit

        viewModel.onSelectImage(imageBitmap)
        viewModel.onClickUpload()

        assertTrue(viewModel.state.value.isLoading)
    }

    @Test
    fun `onClickUpload should save tokens temporarily`() = runTest {
        coEvery { imageDecoder.encodeImage(imageBitmap) } returns imageBytes
        coEvery { cachedImageRepository.cacheImage(any(), any()) } returns Unit
        coEvery { authenticationRepository.saveAuthTokensWithoutEmit(any()) } returns Unit
        coEvery { userRepository.uploadUserProfileImage(any()) } returns Unit
        coEvery { registrationDraftRepository.setImageUploadCompleted(any()) } returns Unit
        coEvery { cachedImageRepository.removeCachedImage(any()) } returns Unit

        viewModel.onSelectImage(imageBitmap)
        viewModel.onClickUpload()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { authenticationRepository.saveAuthTokensWithoutEmit(authTokens) }
    }

    @Test
    fun `onClickUpload should navigate to AccountCreatedScreen on success`() = runTest {
        coEvery { imageDecoder.encodeImage(imageBitmap) } returns imageBytes
        coEvery { cachedImageRepository.cacheImage(any(), any()) } returns Unit
        coEvery { authenticationRepository.saveAuthTokensWithoutEmit(any()) } returns Unit
        coEvery { userRepository.uploadUserProfileImage(any()) } returns Unit
        coEvery { registrationDraftRepository.setImageUploadCompleted(any()) } returns Unit
        coEvery { cachedImageRepository.removeCachedImage(any()) } returns Unit

        viewModel.onSelectImage(imageBitmap)
        viewModel.effect.test {
            viewModel.onClickUpload()
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is UploadProfileImageUIEffect.NavigateToAccountCreated)
            assertEquals(
                authTokens,
                effect.authTokens
            )
        }
    }

    @Test
    fun `onClickUpload should mark image upload as completed`() = runTest {
        coEvery { imageDecoder.encodeImage(imageBitmap) } returns imageBytes
        coEvery { cachedImageRepository.cacheImage(any(), any()) } returns Unit
        coEvery { authenticationRepository.saveAuthTokensWithoutEmit(any()) } returns Unit
        coEvery { userRepository.uploadUserProfileImage(any()) } returns Unit
        coEvery { registrationDraftRepository.setImageUploadCompleted(any()) } returns Unit
        coEvery { cachedImageRepository.removeCachedImage(any()) } returns Unit

        viewModel.onSelectImage(imageBitmap)
        viewModel.onClickUpload()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { registrationDraftRepository.setImageUploadCompleted(true) }
    }

    @Test
    fun `onClickUpload should remove cached image after upload`() = runTest {
        coEvery { imageDecoder.encodeImage(imageBitmap) } returns imageBytes
        coEvery { cachedImageRepository.cacheImage(any(), any()) } returns Unit
        coEvery { authenticationRepository.saveAuthTokensWithoutEmit(any()) } returns Unit
        coEvery { userRepository.uploadUserProfileImage(any()) } returns Unit
        coEvery { registrationDraftRepository.setImageUploadCompleted(any()) } returns Unit
        coEvery { cachedImageRepository.removeCachedImage(any()) } returns Unit

        viewModel.onSelectImage(imageBitmap)
        viewModel.onClickUpload()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { cachedImageRepository.removeCachedImage("register_image_+9647901234567") }
    }

    @Test
    fun `onClickSkip should not navigate when authTokens is null`() = runTest {
        val viewModelWithoutTokens = UploadProfileImageViewModel(
            cachedImageRepository = cachedImageRepository,
            userRepository = userRepository,
            imageDecoder = imageDecoder,
            authenticationRepository = authenticationRepository,
            registrationDraftRepository = registrationDraftRepository,
            authTokens = null,
            phoneNumber = phoneNumber,
            dispatcher = testDispatcher
        )

        viewModelWithoutTokens.effect.test {
            viewModelWithoutTokens.onClickSkip()
            testDispatcher.scheduler.advanceUntilIdle()

            expectNoEvents()
        }
    }

    @Test
    fun `onClickSkip should not navigate when phoneNumber is null`() = runTest {
        val viewModelWithoutPhone = UploadProfileImageViewModel(
            cachedImageRepository = cachedImageRepository,
            userRepository = userRepository,
            imageDecoder = imageDecoder,
            authenticationRepository = authenticationRepository,
            registrationDraftRepository = registrationDraftRepository,
            authTokens = authTokens,
            phoneNumber = null,
            dispatcher = testDispatcher
        )

        viewModelWithoutPhone.effect.test {
            viewModelWithoutPhone.onClickSkip()
            testDispatcher.scheduler.advanceUntilIdle()

            expectNoEvents()
        }
    }

    @Test
    fun `onClickSkip should mark image upload as completed`() = runTest {
        coEvery { registrationDraftRepository.setImageUploadCompleted(any()) } returns Unit
        coEvery { cachedImageRepository.removeCachedImage(any()) } returns Unit

        viewModel.onClickSkip()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { registrationDraftRepository.setImageUploadCompleted(true) }
    }

    @Test
    fun `onClickSkip should navigate to AccountCreatedScreen`() = runTest {
        coEvery { registrationDraftRepository.setImageUploadCompleted(any()) } returns Unit
        coEvery { cachedImageRepository.removeCachedImage(any()) } returns Unit

        viewModel.effect.test {
            viewModel.onClickSkip()
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is UploadProfileImageUIEffect.NavigateToAccountCreated)
            assertEquals(
                authTokens,
                effect.authTokens
            )
        }
    }

    @Test
    fun `onClickSkip should remove cached image`() = runTest {
        coEvery { registrationDraftRepository.setImageUploadCompleted(any()) } returns Unit
        coEvery { cachedImageRepository.removeCachedImage(any()) } returns Unit

        viewModel.onClickSkip()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { cachedImageRepository.removeCachedImage("register_image_+9647901234567") }
    }

    @Test
    fun `onClickEdit should not cache image when phoneNumber is null`() = runTest {
        val viewModelWithoutPhone = UploadProfileImageViewModel(
            cachedImageRepository = cachedImageRepository,
            userRepository = userRepository,
            imageDecoder = imageDecoder,
            authenticationRepository = authenticationRepository,
            registrationDraftRepository = registrationDraftRepository,
            authTokens = authTokens,
            phoneNumber = null,
            dispatcher = testDispatcher
        )

        viewModelWithoutPhone.onClickEdit(imageBitmap)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { cachedImageRepository.cacheImage(any(), any()) }
    }

    @Test
    fun `onClickEdit should navigate to crop screen`() = runTest {
        coEvery { imageDecoder.encodeImage(imageBitmap) } returns imageBytes
        coEvery { cachedImageRepository.cacheImage(any(), any()) } returns Unit

        viewModel.effect.test {
            viewModel.onClickEdit(imageBitmap)
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is UploadProfileImageUIEffect.NavigateToCropScreen)
        }
    }

    @Test
    fun `onClickEdit should cache image for crop`() = runTest {
        coEvery { imageDecoder.encodeImage(imageBitmap) } returns imageBytes
        coEvery { cachedImageRepository.cacheImage(any(), any()) } returns Unit

        viewModel.onClickEdit(imageBitmap)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { cachedImageRepository.cacheImage(any(), imageBytes) }
    }

    @Test
    fun `onClickUpload should set isLoading to false on error`() = runTest {
        coEvery { imageDecoder.encodeImage(imageBitmap) } returns imageBytes
        coEvery { cachedImageRepository.cacheImage(any(), any()) } returns Unit
        coEvery { authenticationRepository.saveAuthTokensWithoutEmit(any()) } returns Unit
        coEvery { userRepository.uploadUserProfileImage(any()) } throws Exception("Upload failed")

        viewModel.onSelectImage(imageBitmap)
        viewModel.onClickUpload()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `onClickUpload should show snack bar error effect with error message`() = runTest {
        coEvery { imageDecoder.encodeImage(imageBitmap) } returns imageBytes
        coEvery { cachedImageRepository.cacheImage(any(), any()) } returns Unit
        coEvery { authenticationRepository.saveAuthTokensWithoutEmit(any()) } returns Unit
        coEvery { userRepository.uploadUserProfileImage(any()) } throws Exception("Upload failed")

        viewModel.onSelectImage(imageBitmap)
        viewModel.onClickUpload()

        viewModel.effect.test {
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isInstanceOf(UploadProfileImageUIEffect.ShowSnackBarError::class)
        }
    }
}