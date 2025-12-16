package net.thechance.mena.identity.presentation.screen.register

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.AuthenticationTokens
import net.thechance.mena.identity.domain.model.RegisterRequest
import net.thechance.mena.identity.domain.model.RegistrationDraft
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.repository.RegisterRepository
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.helper.BaseCoroutineTest
import net.thechance.mena.identity.presentation.screen.register.selectGender.SelectGenderScreenUIEffect
import net.thechance.mena.identity.presentation.screen.register.selectGender.SelectGenderScreenViewModel
import net.thechance.mena.identity.presentation.screen.register.shared.toAuthenticationTokens
import net.thechance.mena.identity.presentation.screen.register.shared.RegisterUIState
import net.thechance.mena.identity.presentation.screen.register.shared.toPhoneNumberUIState
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SelectGenderScreenViewModelTest: BaseCoroutineTest() {
    private lateinit var selectGenderScreenViewModel: SelectGenderScreenViewModel
    private val registerRepository = mockk<RegisterRepository>()
    private val registrationDraftRepository = mockk<RegistrationDraftRepository>()
    private val authenticationRepository = mockk<AuthenticationRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val phoneNumber = PhoneNumber("+964", "7901234567")

    @Before
    fun setup() {
        selectGenderScreenViewModel = SelectGenderScreenViewModel(
            registerRepository = registerRepository,
            registrationDraftRepository = registrationDraftRepository,
            authenticationRepository = authenticationRepository,
            registerUIState = RegisterUIState(phoneNumber.toPhoneNumberUIState()),
            dispatcher = testDispatcher
        )
    }

    @Test
    fun `onChangeGender should update gender`() {
        val gender = Gender.MALE

        selectGenderScreenViewModel.onChangeGender(gender)
    }

    @Test
    fun `onClickRegister should navigate to upload profile image screen`() = runTest {
        val expectedTokens = AuthenticationTokens(
            accessToken = "test_access_token",
            refreshToken = "test_refresh_token"
        )
        coEvery { registerRepository.register(any<RegisterRequest>()) } returns expectedTokens
        coEvery { authenticationRepository.saveAuthTokensWithoutEmit(any()) } returns Unit
        coEvery { registrationDraftRepository.clearDraft(phoneNumber) } returns Unit
        
        selectGenderScreenViewModel.onChangeGender(Gender.MALE)
        
        selectGenderScreenViewModel.effect.test {
            selectGenderScreenViewModel.onClickRegister()
            testDispatcher.scheduler.advanceUntilIdle()
            
            val effect = awaitItem()
            assert(effect is SelectGenderScreenUIEffect.NavigateToUploadProfileImage)
            val navigateEffect = effect as SelectGenderScreenUIEffect.NavigateToUploadProfileImage
            assert(navigateEffect.authUiState.authTokens?.toAuthenticationTokens() == expectedTokens)
        }
    }

    @Test
    fun `onClickRegister should save tokens temporarily`() = runTest {
        val expectedTokens = AuthenticationTokens(
            accessToken = "test_access_token",
            refreshToken = "test_refresh_token"
        )
        coEvery { registerRepository.register(any<RegisterRequest>()) } returns expectedTokens
        coEvery { authenticationRepository.saveAuthTokensWithoutEmit(any()) } returns Unit
        coEvery { registrationDraftRepository.clearDraft(phoneNumber) } returns Unit

        selectGenderScreenViewModel.onChangeGender(Gender.MALE)
        selectGenderScreenViewModel.onClickRegister()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { authenticationRepository.saveAuthTokensWithoutEmit(expectedTokens) }
    }

    @Test
    fun `onClickRegister should clear draft`() = runTest {
        val expectedTokens = AuthenticationTokens(
            accessToken = "test_access_token",
            refreshToken = "test_refresh_token"
        )
        coEvery { registerRepository.register(any<RegisterRequest>()) } returns expectedTokens
        coEvery { authenticationRepository.saveAuthTokensWithoutEmit(any()) } returns Unit
        coEvery { registrationDraftRepository.clearDraft(phoneNumber) } returns Unit

        selectGenderScreenViewModel.onChangeGender(Gender.MALE)
        selectGenderScreenViewModel.onClickRegister()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { registrationDraftRepository.clearDraft(phoneNumber) }
    }

    @Test
    fun `loadSavedData should load saved gender from draft`() = runTest {
        val savedGender = Gender.MALE
        val savedDraft = RegistrationDraft(gender = savedGender)
        coEvery { registrationDraftRepository.getDraft(phoneNumber) } returns savedDraft

        val viewModel = SelectGenderScreenViewModel(
            registerRepository = registerRepository,
            registrationDraftRepository = registrationDraftRepository,
            authenticationRepository = authenticationRepository,
            registerUIState = RegisterUIState(phoneNumber.toPhoneNumberUIState()),
            dispatcher = testDispatcher
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(savedGender, viewModel.state.value.gender)
    }

    @Test
    fun `loadSavedData should enable register button when gender is loaded`() = runTest {
        val savedGender = Gender.MALE
        val savedDraft = RegistrationDraft(gender = savedGender)
        coEvery { registrationDraftRepository.getDraft(phoneNumber) } returns savedDraft

        val viewModel = SelectGenderScreenViewModel(
            registerRepository = registerRepository,
            registrationDraftRepository = registrationDraftRepository,
            authenticationRepository = authenticationRepository,
            registerUIState = RegisterUIState(phoneNumber.toPhoneNumberUIState()),
            dispatcher = testDispatcher
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.isRegisterEnabled)
    }

    @Test
    fun `onChangeGender should save gender to draft`() = runTest {
        val gender = Gender.FEMALE
        coEvery { registrationDraftRepository.getDraft(phoneNumber) } returns RegistrationDraft()
        coEvery { registrationDraftRepository.saveDraft(phoneNumber, any()) } returns Unit

        selectGenderScreenViewModel.onChangeGender(gender)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { registrationDraftRepository.saveDraft(phoneNumber, RegistrationDraft(gender = gender)) }
    }
}
