package net.thechance.mena.identity.presentation.screen.register

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import net.thechance.mena.identity.domain.repository.RegisterRepository
import net.thechance.mena.identity.domain.useCase.validation.mobileNumber.PasswordValidator
import net.thechance.mena.identity.presentation.screen.register.createPassword.CreatePasswordViewModel
import org.junit.Before
import org.junit.Test

class CreatePasswordViewModelTest {
    private val passwordValidator = mockk<PasswordValidator>()
    private val registerRepository = mockk<RegisterRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var createPasswordViewModel: CreatePasswordViewModel

    @Before
    fun setup(){
        createPasswordViewModel = CreatePasswordViewModel(
            passwordValidator = passwordValidator,
            registerRepository = registerRepository,
            dispatcher = testDispatcher
        )
    }

    @Test
    fun `onChangeNewPassword should update state with new password`(){
        val newPassword = "password123"

        every { passwordValidator.isValid(newPassword)  } returns true
        createPasswordViewModel.onChangeNewPassword(newPassword)

        assert(createPasswordViewModel.state.value.newPassword == newPassword)
    }

    @Test
    fun `onChangeConfirmPassword should update state with new password`(){
        val newPassword = "password123"

        every { passwordValidator.isValid(newPassword)  } returns true
        createPasswordViewModel.onChangeNewPassword(newPassword)
        createPasswordViewModel.onChangeConfirmPassword(newPassword)

        assert(createPasswordViewModel.state.value.confirmPassword == newPassword)
    }

    @Test
    fun `onToggleNewPasswordVisibility should toggle new password visibility`(){
        createPasswordViewModel.onToggleNewPasswordVisibility()

        assert(createPasswordViewModel.state.value.isNewPasswordVisible)
    }

    @Test
    fun `onToggleConfirmPasswordVisibility should toggle confirm password visibility`(){
        createPasswordViewModel.onToggleConfirmPasswordVisibility()

        assert(createPasswordViewModel.state.value.isConfirmPasswordVisible)
    }

    @Test
    fun `onClearErrorMessage should clear error message`(){
        createPasswordViewModel.onClearErrorMessage()

        assert(createPasswordViewModel.state.value.errorMessage == null)
    }
}