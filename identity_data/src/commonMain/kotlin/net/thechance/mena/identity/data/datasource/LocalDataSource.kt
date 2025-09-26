package net.thechance.mena.identity.data.datasource

interface LocalDataSource {
    fun saveAccessToken(accessToken: String)

    fun getAccessToken(): String

    fun saveRefreshToken(refreshToken: String)

    fun getRefreshToken(): String

}