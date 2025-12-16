package net.thechance.mena.identity.presentation.screen.register.accountCreated

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.AuthenticationTokens
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.helper.BaseCoroutineTest
import net.thechance.mena.identity.presentation.screen.register.shared.AuthUIState
import net.thechance.mena.identity.presentation.screen.register.shared.toPhoneNumberUIState
import org.junit.Before
import org.junit.Test

class AccountCreatedViewModelTest : BaseCoroutineTest() {
    private lateinit var accountCreatedViewModel: AccountCreatedViewModel
    private val authenticationRepository = mockk<AuthenticationRepository>()
    private val registrationDraftRepository = mockk<RegistrationDraftRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val testPhoneNumber = PhoneNumber("+964", "7701234567")

    @Before
    fun setup() {
        accountCreatedViewModel = AccountCreatedViewModel(
            authenticationRepository = authenticationRepository,
            registrationDraftRepository = registrationDraftRepository,
            AuthUIState(
                authTokens = AuthUIState.AuthenticationTokensUiState(
                    accessToken = "test_access_token",
                    refreshToken = "test_refresh_token"
                ),
                phoneNumber = testPhoneNumber.toPhoneNumberUIState()
            ),
            dispatcher = testDispatcher
        )
    }

    @Test
    fun `onClickGoToHome should call saveAuthTokensAndEmit with correct tokens`() = runTest {
        val authTokens = AuthenticationTokens(
            accessToken = "test_access_token",
            refreshToken = "test_refresh_token"
        )
        coEvery { authenticationRepository.saveAuthTokensAndEmit(any()) } returns Unit
        coEvery { registrationDraftRepository.clearLastPhoneNumber() } returns Unit

        accountCreatedViewModel.onClickGoToHome()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { authenticationRepository.saveAuthTokensAndEmit(authTokens) }
    }

    @Test
    fun `onClickGoToHome should call clearLastPhoneNumber and clearDraft`() = runTest {
        coEvery { authenticationRepository.saveAuthTokensAndEmit(any()) } returns Unit
        coEvery { registrationDraftRepository.clearLastPhoneNumber() } returns Unit
        coEvery { registrationDraftRepository.clearDraft(any()) } returns Unit

        accountCreatedViewModel.onClickGoToHome()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { registrationDraftRepository.clearLastPhoneNumber() }
        coVerify { registrationDraftRepository.clearDraft(testPhoneNumber) }
    }

    @Test
    fun `onClickGoToHome should not call saveAuthTokensAndEmit when authTokens is null`() = runTest {
        accountCreatedViewModel = AccountCreatedViewModel(
            authenticationRepository = authenticationRepository,
            registrationDraftRepository = registrationDraftRepository,
            AuthUIState(
                authTokens = null,
                phoneNumber = testPhoneNumber.toPhoneNumberUIState()
            ),
            dispatcher = testDispatcher
        )

        accountCreatedViewModel.onClickGoToHome()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { authenticationRepository.saveAuthTokensAndEmit(any()) }
    }

    @Test
    fun `onClickGoToHome should not call clearLastPhoneNumber when authTokens is null`() = runTest {
        accountCreatedViewModel = AccountCreatedViewModel(
            authenticationRepository = authenticationRepository,
            registrationDraftRepository = registrationDraftRepository,
            AuthUIState(
                authTokens = null,
                phoneNumber = testPhoneNumber.toPhoneNumberUIState()
            ),
            dispatcher = testDispatcher
        )

        accountCreatedViewModel.onClickGoToHome()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { registrationDraftRepository.clearLastPhoneNumber() }
        coVerify(exactly = 0) { registrationDraftRepository.clearDraft(any()) }
    }
}