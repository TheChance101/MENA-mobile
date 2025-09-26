package net.thechance.mena.identity.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import net.thechance.mena.identity.data.dto.auth.LoginRequestDto
import net.thechance.mena.identity.data.dto.auth.LoginResponseDto
import net.thechance.mena.identity.data.dto.auth.RefreshRequestDto

class AuthRemoteDataSourceImpl(
    private val client: HttpClient
) : AuthRemoteDataSource {
    override suspend fun login(loginRequest: LoginRequestDto): LoginResponseDto {
        return client.postJson(loginRequest, LOGIN)
    }

    override suspend fun refreshToken(refreshRequest: RefreshRequestDto): LoginResponseDto {
        return client.postJson(refreshRequest, REFRESH)
    }

    companion object {
        const val LOGIN = "identity/login"
        const val REFRESH = "identity/refresh"
    }
}

private suspend inline fun <reified T, reified R> HttpClient.postJson(
    requestDto: T,
    path: String
): R {
    val response = this.post {
        url(path)
        contentType(ContentType.Application.Json)
        setBody(requestDto)
    }

    if (response.status != HttpStatusCode.OK) {
        throw ClientRequestException(response, response.body())
    }

    return response.body()
}