package net.thechance.mena.admin_panel.data.repository.authentication

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.StateFlow
import net.thechance.mena.admin_panel.data.remote.dto.authentication.AdminAuthenticationResponse
import net.thechance.mena.admin_panel.data.remote.dto.authentication.LoginRequestDto
import net.thechance.mena.admin_panel.data.remote.api_service.AuthenticationApiService
import net.thechance.mena.admin_panel.data.utils.accessToken
import net.thechance.mena.admin_panel.data.utils.executeApiSafely
import net.thechance.mena.admin_panel.data.utils.observableToken
import net.thechance.mena.admin_panel.data.utils.refreshToken
import net.thechance.mena.admin_panel.domain.repository.authentication.AdminAuthenticationRepository
import org.koin.core.annotation.Single

@Single
class AdminAuthenticationRepositoryImpl(
    private val authenticationApiService: AuthenticationApiService,
    private val settings: Settings
) : AdminAuthenticationRepository {

    override suspend fun login(userName: String, password: String) {
        val loginResponse: AdminAuthenticationResponse =
            executeApiSafely<AdminAuthenticationResponse> {
                authenticationApiService.login(
                    LoginRequestDto(userName = userName, password = password)
                )
            }
        saveAuthTokens(authenticationInfo = loginResponse)
    }

    override suspend fun logout() {
        executeApiSafely<Unit> {
            authenticationApiService.logout()
        }
        clearAuthTokens()
    }

    override suspend fun isUserLoggedIn(): Boolean = settings.accessToken.isNotBlank()

    override fun observeToken(): StateFlow<String> = observableToken
    private suspend fun saveAuthTokens(authenticationInfo: AdminAuthenticationResponse) {
        settings.accessToken = authenticationInfo.accessToken.also { observableToken.emit(it) }
        settings.refreshToken = authenticationInfo.refreshToken
    }

    private suspend fun clearAuthTokens() {
        settings.accessToken = "".also { observableToken.emit(it) }
        settings.refreshToken = ""
    }
}