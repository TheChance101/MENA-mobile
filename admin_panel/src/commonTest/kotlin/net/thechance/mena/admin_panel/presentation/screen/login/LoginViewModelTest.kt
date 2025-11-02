package net.thechance.mena.admin_panel.presentation.screen.login

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.thechance.mena.admin_panel.domain.use_case.auth.LoginUseCase
import net.thechance.mena.admin_panel.presentation.model.SnackBarState
import net.thechance.mena.admin_panel.presentation.utils.StringProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val loginUseCase: LoginUseCase = mockk(relaxed = true)
    private val stringProvider: StringProvider = mockk(relaxed = true)
    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should update username when onUsernameChanged called`() = runTest {
        val expectedUsername = "username1"
        viewModel = createViewModel()

        viewModel.onUsernameChanged(expectedUsername)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(expectedUsername, state.username)
        }
    }

    @Test
    fun `should update password when onPasswordChanged called`() = runTest {
        val expectedPassword = "password1"
        viewModel = createViewModel()

        viewModel.onPasswordChanged(expectedPassword)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(expectedPassword, state.password)
        }
    }

    @Test
    fun `should update isPasswordVisible to true when onPasswordVisibilityToggled called first`() = runTest {
        val expectedIsPasswordVisible = true
        viewModel = createViewModel()

        viewModel.onPasswordVisibilityToggled()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(expectedIsPasswordVisible, state.isPasswordVisible)
        }
    }

    @Test
    fun `should set login button loading to true when first called`() = runTest {
        coEvery { loginUseCase.login(any(), any()) } returns Unit
        viewModel = createViewModel()

        viewModel.onLoginButtonClicked()

        viewModel.state.test {
            skipItems(1)
            val state = awaitItem()
            assertEquals(true, state.isLoginButtonLoading)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should set login button loading to false when login use case runs`() = runTest {
        coEvery { loginUseCase.login(any(), any()) } returns Unit
        viewModel = createViewModel()

        viewModel.onLoginButtonClicked()

        viewModel.state.test {
            skipItems(2)
            val state = awaitItem()
            assertEquals(false, state.isLoginButtonLoading)
        }
    }

    @Test
    fun `should send navigate to admin panel effect when login use case runs`() = runTest {
        coEvery { loginUseCase.login(any(), any()) } returns Unit
        viewModel = createViewModel()

        viewModel.onLoginButtonClicked()

        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertEquals(LoginEffect.NavigateToAdminPanel, effect)
        }
    }

    @Test
    fun `should shows snackbar when login use case crashes`() = runTest {
        coEvery { loginUseCase.login(any(), any()) } throws Exception()
        viewModel = createViewModel()

        viewModel.onLoginButtonClicked()

        viewModel.state.test {
            var stateItem = awaitItem()
            while (!stateItem.snackBar.isVisible) {
                stateItem = awaitItem()
            }
            assertSnackBarState(isVisible = true, isSuccess = false, snackBarState = stateItem.snackBar)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun assertSnackBarState(
        isVisible: Boolean,
        isSuccess: Boolean,
        snackBarState: SnackBarState
    ) {
        assertEquals(isVisible, snackBarState.isVisible)
        assertEquals(isSuccess, snackBarState.isSuccess)
    }

    private fun createViewModel() = LoginViewModel(
        loginUseCase = loginUseCase,
        stringProvider = stringProvider,
        dispatcher = testDispatcher
    )
}