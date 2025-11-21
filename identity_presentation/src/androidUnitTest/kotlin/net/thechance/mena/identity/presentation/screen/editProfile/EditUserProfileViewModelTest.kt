package net.thechance.mena.identity.presentation.screen.editProfile

import androidx.compose.ui.graphics.ImageBitmap
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import assertk.assertions.isNull
import assertk.assertions.isTrue
import dev.icerock.moko.permissions.PermissionsController
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error_something_went_wrong
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.repository.ImagesRepository
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.domain.useCase.validation.age.AgeValidator
import net.thechance.mena.identity.helper.BaseCoroutineTest
import net.thechance.mena.identity.helper.createUser
import net.thechance.mena.identity.presentation.utils.ImageDecoder
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalCoroutinesApi::class)
class EditUserProfileViewModelTest() : BaseCoroutineTest() {

    private val userRepository = mockk<UserRepository>()
    private val permissionsController = mockk<PermissionsController>()
    private val imagesRepository = mockk<ImagesRepository>()
    private val ageValidator = mockk<AgeValidator>()
    private val imageDecoder = mockk<ImageDecoder>()
    private val authenticationRepository = mockk<AuthenticationRepository>()
    private val registrationDraftRepository = mockk<RegistrationDraftRepository>()
    private val testDispatcher = StandardTestDispatcher()

    val viewModel by lazy {
        EditUserProfileViewModel(
            userRepository = userRepository,
            permissionsController = permissionsController,
            imagesRepository = imagesRepository,
            imageDecoder = imageDecoder,
            dispatcher = testDispatcher,
            ageValidator = ageValidator,
            authenticationRepository = authenticationRepository,
            registrationDraftRepository = registrationDraftRepository
        )
    }

    override fun setUp() {
        super.setUp()
        coEvery { ageValidator.isValid(any()) } returns true
    }

    @Test
    fun `user information should be updated, when init called`() = runTest {
        coEvery { userRepository.getUser() } returns flowOf(fakeUser)

        viewModel
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.state.value.firstName).isEqualTo("User")
        coVerify(exactly = 1) { userRepository.getUser() }
    }

    @Test
    fun `errorMessage should be updated, when init throws Exception`() = runTest {
        coEvery { userRepository.getUser() } throws Exception()

        viewModel
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().errorMessage).isEqualTo(Res.string.error_something_went_wrong)
        }

        coVerify(exactly = 1) { userRepository.getUser() }
    }

    @Test
    fun `firstName should be changed, when onChangeFirstName is called`() = runTest {
        viewModel.onChangeFirstName("new User")

        viewModel.state.test {
            assertThat(awaitItem().firstName).isEqualTo("new User")
        }
    }

    @Test
    fun `last name should be changed, when onChangeLastName is called`() = runTest {
        viewModel.onChangeLastName("the chance")

        viewModel.state.test {
            assertThat(awaitItem().lastName).isEqualTo("the chance")
        }
    }

    @Test
    fun `username should be changed, when onChangeUsername is called`() = runTest {
        viewModel.onChangeUsername("new-chance")

        viewModel.state.test {
            assertThat(awaitItem().username).isEqualTo("new-chance")
        }
    }

    @Test
    fun `onClickSaveButton should do nothing, when first name is empty`() = runTest {
        viewModel.onChangeFirstName(" ")
        viewModel.onChangeLastName("the chance")
        viewModel.onChangeUsername("new-chance")

        viewModel.onClickSaveButton()

        assertThat(viewModel.state.value.firstName).isNotEmpty()
    }

    @Test
    fun `onClickSaveButton should do nothing, when last name is empty`() = runTest {
        viewModel.onChangeFirstName("new User")
        viewModel.onChangeLastName(" ")
        viewModel.onChangeUsername("new-chance")

        viewModel.onClickSaveButton()

        assertThat(viewModel.state.value.lastName).isNotEmpty()
    }

    @Test
    fun `onClickSaveButton should do nothing, when username is empty`() = runTest {
        viewModel.onChangeFirstName("new User")
        viewModel.onChangeLastName("the chance")
        viewModel.onChangeUsername(" ")

        viewModel.onClickSaveButton()

        assertThat(viewModel.state.value.username).isNotEmpty()
    }

    @Test
    fun `onClickSaveButton should do nothing, when user name is empty`() = runTest {
        viewModel.onChangeFirstName(" ")
        viewModel.onChangeLastName("the chance")
        viewModel.onChangeUsername("new-chance")

        viewModel.onClickSaveButton()
        assertThat(viewModel.state.value.firstName).isNotEmpty()
    }

    @Test
    fun `isLoading should be true, when on onClickSaveButton is initially called`() = runTest {
        viewModel.onChangeFirstName("new User")
        viewModel.onChangeLastName("the chance")
        viewModel.onChangeUsername("new-chance")

        viewModel.onClickSaveButton()

        assertThat(viewModel.state.value.isLoading).isTrue()
        assertThat(viewModel.state.value.errorMessage).isNull()
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `should navigate back to profile, when on onClickSaveButton is successfully called`() =
        runTest {
            viewModel.userId = fakeUser.id
            coEvery { userRepository.uploadUserProfileImage(any()) } returns Unit
            coEvery { userRepository.deleteUserProfileImage() } returns Unit
            coEvery {
                userRepository.updateUser(user = any())
            } returns Unit

            viewModel.onChangeFirstName("new User")
            viewModel.onChangeLastName("the chance")
            viewModel.onChangeUsername("new-chance")

            viewModel.onClickSaveButton()

            viewModel.effect.test {
                testDispatcher.scheduler.advanceUntilIdle()
                assertThat(awaitItem()).isEqualTo(EditUserProfileUIEffect.NavigateBackToProfile)
            }

            coVerify(exactly = 1) {
                userRepository.updateUser(user = any())
            }
        }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `errorMessage should be updated, when on onClickSaveButton is failed with Exception`() =
        runTest {
            viewModel.userId = fakeUser.id
            coEvery { userRepository.uploadUserProfileImage(any()) } returns Unit
            coEvery { userRepository.deleteUserProfileImage() } returns Unit

            coEvery {
                userRepository.updateUser(user = any())
            } throws Exception()

            viewModel.onChangeFirstName("new User")
            viewModel.onChangeLastName("the chance")
            viewModel.onChangeUsername("new-chance")

            viewModel.onClickSaveButton()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.state.test {
                assertThat(awaitItem().errorMessage).isEqualTo(Res.string.error_something_went_wrong)
            }

            coVerify(exactly = 1) {
                userRepository.updateUser(user = any())
            }
        }

    @Test
    fun `onClickCancelButton should navigate back to profile when called, `() = runTest {
        viewModel.effect.test {
            viewModel.onClickCancelButton()
            assertThat(awaitItem()).isEqualTo(EditUserProfileUIEffect.NavigateBackToProfile)
        }
    }

    @Test
    fun `showLogoutDialog should be true, when onClickShowLogoutOptions is called`() = runTest {
        viewModel.onClickShowLogoutOptions()

        viewModel.state.test {
            assertThat(awaitItem().showLogoutDialog).isTrue()
        }
    }

    @Test
    fun `showLogoutDialog should be false, when onDismissLogoutDialog is called`() = runTest {
        viewModel.onClickShowLogoutOptions()
        viewModel.onDismissLogoutDialog()

        viewModel.state.test {
            assertThat(awaitItem().showLogoutDialog).isFalse()
        }
    }

    @Test
    fun `birthDate should be updated, when onChangeDate is called`() = runTest {
        viewModel.onChangeDate(1, 1, 2001)

        viewModel.state.test {
            assertThat(awaitItem().birthDate).isEqualTo(LocalDate(2001, 1, 1))
        }
    }

    @Test
    fun `showEditImageDialog should be true, when onClickEditImage is called`() = runTest {
        viewModel.onClickEditImage()

        viewModel.state.test {
            assertThat(awaitItem().showEditImageDialog).isTrue()
        }
    }

    @Test
    fun `showEditImageDialog should be false, when onDismissEditImageDialog is called`() = runTest {
        viewModel.onClickEditImage()
        viewModel.onDismissEditImageDialog()

        viewModel.state.test {
            assertThat(awaitItem().showEditImageDialog).isFalse()
        }
    }

    @Test
    fun `profileImageBitmap should be null, when onRemoveProfileImage is called`() = runTest {
        viewModel.onRemoveProfileImage()

        viewModel.state.test {
            assertThat(awaitItem().profileImageBitmap).isNull()
        }
    }

    @Test
    fun `profileImageUrl should be empty, when onRemoveProfileImage is called`() = runTest {
        viewModel.onRemoveProfileImage()

        viewModel.state.test {
            assertThat(awaitItem().profileImageUrl).isEmpty()
        }
    }

    @Test
    fun `should navigate to CropScreen, when onRequireCropImage is called`() = runTest {

        coEvery { userRepository.getUser() } returns flowOf(fakeUser)
        coEvery { imagesRepository.cacheImage(any(), any()) } returns Unit
        coEvery { imageDecoder.encodeImage(mockkImageBitmap) } returns byteArrayOf()
        coEvery { imageDecoder.decodeImage(any()) } returns mockkImageBitmap

        viewModel.effect.test {
            viewModel.onRequireCropImage(mockkImageBitmap)
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem()).isInstanceOf(EditUserProfileUIEffect.NavigateToCropScreen::class)
        }
    }

    @Test
    fun `permission Manager should be granted, when onTakeImageCamera is called`() = runTest {
        coEvery {
            permissionsController.providePermission(any())
        } just runs

        viewModel.onTakeImageFromCamera()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) {
            permissionsController.providePermission(any())
        }
    }

    @Test
    fun `errorMessage should be updated, when onTakeImageCamera is failed with Exception`() =
        runTest {
            coEvery {
                permissionsController.providePermission(any())
            } throws Exception()

            viewModel.onTakeImageFromCamera()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.state.test {
                assertThat(awaitItem().errorMessage).isEqualTo(Res.string.error_something_went_wrong)
            }

            coVerify(exactly = 1) {
                permissionsController.providePermission(any())
            }
        }

    @Test
    fun `showCamera should be true, when onOpenCamera is called`() = runTest {
        viewModel.onOpenCamera()

        viewModel.state.test {
            assertThat(awaitItem().showCamera).isFalse()
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
private val fakeUser = createUser(firstName = "User")
private val mockkImageBitmap = mockk<ImageBitmap>()