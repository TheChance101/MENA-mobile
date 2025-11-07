
package net.thechance.mena.identity.presentation.screen.profile

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.exception.UnknownException
import net.thechance.mena.identity.domain.repository.SettingsRepository
import net.thechance.mena.identity.domain.repository.UserRepository
import net.thechance.mena.identity.domain.util.AppLanguage
import net.thechance.mena.identity.helper.BaseCoroutineTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest : BaseCoroutineTest() {

    private val testDispatcher = StandardTestDispatcher()

    private val userRepository: UserRepository = mockk()
    private val settingsRepository: SettingsRepository = mockk()
    private lateinit var viewModel: ProfileScreenViewModel

    @BeforeTest
    override fun setUp() {
        super.setUp()
        coEvery { userRepository.getUser() } returns flowOf(fakeUser)
        coEvery { settingsRepository.getCurrentAppLanguage() } returns AppLanguage.ENGLISH
        viewModel = ProfileScreenViewModel(
            userRepository,
            settingsRepository,
            "",
            testDispatcher
        )
    }


    @Test
    fun `getUserInfo() updates state on success`() = runTest {
        viewModel = ProfileScreenViewModel(userRepository, settingsRepository,"", testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(fakeUser.firstName + " " + fakeUser.lastName, viewModel.state.value.fullName)
        assertEquals(fakeUser.username, viewModel.state.value.userName)
        assertEquals(fakeUser.profileImageUrl, viewModel.state.value.profileImageUrl)
        assertTrue { viewModel.state.value.isSuccess }
    }

    @Test
    fun `should update state with error when repository throws`() = runTest {

        coEvery { userRepository.getUser() } throws UnknownException()

        viewModel = ProfileScreenViewModel(userRepository, settingsRepository,"", testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(null, viewModel.state.value.errorMessage)
    }


    @Test
    fun `should emit NavigateEditProfileScreen effect when onEditProfileInfoClicked`() = runTest {

        viewModel.effect.test {
            viewModel.onEditProfileInfoClicked()
            val emittedEffect = awaitItem()
            assertTrue { emittedEffect is ProfileScreenUIEffect.NavigateToEditProfileScreen }
            cancelAndConsumeRemainingEvents()

        }
    }

    @Test
    fun `showShareProfileDialog should updated to true, when onShareClicked called`() = runTest {
        viewModel.onShareClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            assertThat(awaitItem().showShareProfileDialog).isTrue()
        }
    }

    @Test
    fun `showShareProfileDialog should updated to false, when onDismissShareDialog called`() =
        runTest {
            viewModel.onDismissShareDialog()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.state.test {
                assertThat(awaitItem().showShareProfileDialog).isFalse()
            }
        }

    @Test
    fun `should update state to show share bottom sheet when onInviteFriendsClicked`() = runTest {
        viewModel.onInviteFriendsClicked()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(true, viewModel.state.value.showShareBottomSheet)

    }

    @Test
    fun `should emit NavigateToChangePasswordScreen effect when onChangePasswordClicked`() =
        runTest {
            viewModel.effect.test {
                viewModel.onChangePasswordClicked()
                val emittedEffect = awaitItem()
                assertTrue { emittedEffect is ProfileScreenUIEffect.NavigateToChangePasswordScreen }
            }
        }

    @Test
    fun `should emit NavigateToLocationPickerScreen effect when onAddressesClicked`() = runTest {
        viewModel.effect.test {
            viewModel.onAddressesClicked()
            val emittedEffect = awaitItem()
            assertTrue { emittedEffect is ProfileScreenUIEffect.NavigateToLocationPickerScreen }
        }
    }

    @Test
    fun `should emit NavigateToPrivacyAndPolicyScreen effect when onPrivacySettingsClicked`() =
        runTest {
            viewModel.effect.test {
                viewModel.onPrivacySettingsClicked()
                val emittedEffect = awaitItem()
                assertTrue { emittedEffect is ProfileScreenUIEffect.NavigateToPrivacyAndPolicyScreen }
            }
        }

    @Test
    fun `should update state to show language dialog when onLanguageClicked`() = runTest {
        viewModel.onLanguageClicked()
        viewModel.state.test {
            val updatedState = awaitItem()
            assertTrue(updatedState.languageDialogUiState.isVisible)
        }
    }

    @Test
    fun `should update state to show theme dialog when onThemeClicked`() = runTest {
        viewModel.onThemeClicked()

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.showThemeDialog)

    }

    @Test
    fun `should emit NavigateToPrivacyAndPolicyScreen effect when onPrivacyAndPolicyClicked`() =
        runTest {
            viewModel.effect.test {
                viewModel.onPrivacyAndPolicyClicked()
                val emittedEffect = awaitItem()
                assertTrue { emittedEffect is ProfileScreenUIEffect.NavigateToPrivacyAndPolicyScreen }
            }
        }

    @Test
    fun `should emit NavigateContactUsScreen effect when onContactUsClicked`() = runTest {
        viewModel.effect.test {
            viewModel.onContactUsClicked()
            val emittedEffect = awaitItem()
            assertTrue { emittedEffect is ProfileScreenUIEffect.NavigateContactUsScreen }
        }
    }

    @Test
    fun `onConfirmLanguageSelection should save language and hide dialog`() = runTest {
        val newAppLanguage = AppLanguage.ARABIC
        coEvery { settingsRepository.applyLanguage(newAppLanguage) } returns Unit

        viewModel.onConfirmLanguageSelection(newAppLanguage)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { settingsRepository.applyLanguage(newAppLanguage) }
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.languageDialogUiState.isVisible)
            assertEquals(newAppLanguage, state.languageDialogUiState.selectedAppLanguage)
        }
    }

    @Test
    fun `should update state to hide language dialog when onDismissLanguageDialog`() = runTest {
        viewModel.onDismissLanguageDialog()

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.state.value.languageDialogUiState.isVisible)

    }

    @Test
    fun `should update state to hide share bottom sheet when onDismissBottomSheet`() = runTest {

        viewModel.onDismissBottomSheet()

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.state.value.showShareBottomSheet)

    }


    @Test
    fun `should update state to hide theme dialog when onDismissThemeDialog`() = runTest {
        viewModel.onDismissThemeDialog()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.state.value.showThemeDialog)
    }

    @Test
    fun `clearErrorMessage() should update error message to null`() {
        viewModel.clearErrorMessage()
        assertNull(viewModel.state.value.errorMessage)
    }

    @OptIn(ExperimentalUuidApi::class)
    private val fakeUser = User(
        id = Uuid.parse("1bfbf5d8-145d-40e9-abae-8335df3f0a81"),
        firstName = "The ",
        lastName = "Chance",
        username = "the-chance",
        profileImageUrl = "",
        birthDate = LocalDate(1900, 1, 1),
        gender = Gender.MALE,
    )
}
