package net.thechance.mena.identity.data.repository

import net.thechance.mena.identity.data.datasource.AuthRemoteDataSource
import net.thechance.mena.identity.data.datasource.LocalDataSource
import net.thechance.mena.identity.data.dto.auth.LoginRequestDto
import net.thechance.mena.identity.data.dto.auth.LoginResponseDto
import net.thechance.mena.identity.data.dto.auth.RefreshRequestDto
import net.thechance.mena.identity.data.utils.safeWrapper
import net.thechance.mena.identity.domain.repository.AuthenticationRepository

class AuthenticationRepositoryImpl(
    private val remoteAuthService: AuthRemoteDataSource,
    private val localDataSource: LocalDataSource,
) : AuthenticationRepository {
    override suspend fun login(countryCode: String, number: String, password: String) {
        return safeWrapper {
            val mobileNumber = countryCode + number
            val loginResponse = remoteAuthService.login(LoginRequestDto(mobileNumber, password))
            saveAuthTokens(loginResponse)
        }
    }

    override suspend fun refreshAccessToken(): String {
        val refreshResponse = safeWrapper {
            remoteAuthService.refreshToken(RefreshRequestDto(localDataSource.getRefreshToken()))
        }
        saveAuthTokens(refreshResponse)
        return localDataSource.getAccessToken()
    }

    override suspend fun getAccessToken(): String {
        return localDataSource.getAccessToken()
    }

    private fun saveAuthTokens(loginResponseDto: LoginResponseDto) {
        localDataSource.saveAccessToken(loginResponseDto.accessToken)
        localDataSource.saveRefreshToken(loginResponseDto.refreshToken)
    }
}