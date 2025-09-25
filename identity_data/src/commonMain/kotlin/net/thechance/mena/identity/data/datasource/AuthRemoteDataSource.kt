package net.thechance.mena.identity.data.datasource

import net.thechance.mena.identity.data.dto.auth.LoginRequestDto
import net.thechance.mena.identity.data.dto.auth.LoginResponseDto
import net.thechance.mena.identity.data.dto.auth.RefreshRequestDto

interface AuthRemoteDataSource {
    suspend fun login(loginRequest: LoginRequestDto): LoginResponseDto
    suspend fun refreshToken(refreshRequest: RefreshRequestDto): LoginResponseDto

}