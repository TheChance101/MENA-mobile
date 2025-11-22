package net.thechance.mena.identity.presentation.screen.register

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import net.thechance.mena.identity.domain.repository.RegisterRepository
import net.thechance.mena.identity.domain.repository.RegistrationDraftRepository
import net.thechance.mena.identity.domain.useCase.LoginUseCase
import net.thechance.mena.identity.helper.BaseCoroutineTest
import net.thechance.mena.identity.presentation.screen.countryPicker.menaCountries.MenaCountry
import net.thechance.mena.identity.presentation.screen.register.phoneEntry.RegisterPhoneEntryUIEffect
import net.thechance.mena.identity.presentation.screen.register.phoneEntry.RegisterPhoneEntryViewModel
import org.junit.Before
import org.junit.Test

class RegisterPhoneEntryViewModelTest : BaseCoroutineTest() {
    private lateinit var registerPhoneEntryViewModel: RegisterPhoneEntryViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val registerRepository = mockk<RegisterRepository>()
    private val loginUseCase = mockk<LoginUseCase>()
    private val registrationDraftRepository = mockk<RegistrationDraftRepository>()


    @Before
    fun setup() {
        registerPhoneEntryViewModel = RegisterPhoneEntryViewModel(
            loginUseCase = loginUseCase,
            registerRepository = registerRepository,
            registrationDraftRepository = registrationDraftRepository,
            dispatcher = testDispatcher
        )
    }


    @Test
    fun `onChangePhone should update phone number`() {
        val phone = "123456789"

        every { loginUseCase.isMobileNumberValid(any(), any()) } returns true
        registerPhoneEntryViewModel.onChangePhone(phone)
        testDispatcher.scheduler.advanceUntilIdle()

        assert(registerPhoneEntryViewModel.state.value.phoneNumber == phone)
    }

    @Test
    fun `onClickCountry should show country picker`() {
        registerPhoneEntryViewModel.onClickCountry()

        assert(registerPhoneEntryViewModel.state.value.showCountryBottomSheet)
    }

    @Test
    fun `onSelectCountryItem should update current country and hide country picker`() {
        val country = MenaCountry.IRAQ
        every { loginUseCase.isMobileNumberValid(any(), any()) } returns true

        registerPhoneEntryViewModel.onSelectCountryItem(country)

        assert(registerPhoneEntryViewModel.state.value.currentCountry == country)
    }

    @Test
    fun `onDismissBottomSheet should hide country picker`() {
        registerPhoneEntryViewModel.onDismissBottomSheet()

        assert(!registerPhoneEntryViewModel.state.value.showCountryBottomSheet)
    }

    @Test
    fun `onClickLogin should navigate to login screen`() = runTest {
        registerPhoneEntryViewModel.effect.test {
            registerPhoneEntryViewModel.onClickLogin()

            assert(awaitItem() == RegisterPhoneEntryUIEffect.NavigateToLogin)
        }
    }
}
