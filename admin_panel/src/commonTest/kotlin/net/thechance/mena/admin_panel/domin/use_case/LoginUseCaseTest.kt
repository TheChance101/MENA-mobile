package net.thechance.mena.admin_panel.domin.use_case

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import net.thechance.mena.admin_panel.domain.exceptions.InvalidPasswordException
import net.thechance.mena.admin_panel.domain.repository.authentication.AdminAuthenticationRepository
import net.thechance.mena.admin_panel.domain.use_case.auth.LoginUseCase
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class LoginUseCaseTest {
    private lateinit var adminAuthenticationRepository: AdminAuthenticationRepository
    private lateinit var loginUseCase: LoginUseCase

    @BeforeTest
    fun setup() {
        adminAuthenticationRepository = mock(mode = MockMode.autofill)
        loginUseCase = LoginUseCase(adminAuthenticationRepository)
    }

    @Test
    fun `login should throw InvalidPasswordException if password is too short`() = runTest {
        val shortPassword = "12345"

        assertFailsWith<InvalidPasswordException> {
            loginUseCase.login("testUser", shortPassword)
        }
    }

    @Test
    fun `login should call repository login if password is valid`() = runTest {
        val validPassword = "validPass123"
        everySuspend {
            adminAuthenticationRepository.login("testUser", validPassword)
        } returns Unit

        loginUseCase.login("testUser", validPassword)

        verifySuspend { adminAuthenticationRepository.login("testUser", validPassword) }
    }
}
