package net.thechance.mena.identity.data.datasource

import com.russhwolf.settings.Settings

class LocalDataSourceImpl(
    private val settings: Settings
) : LocalDataSource {
    override fun saveAccessToken(accessToken: String) {
        settings.putString(ACCESS_TOKEN, accessToken)
    }

    override fun getAccessToken(): String {
        return settings.getString(ACCESS_TOKEN, "")
    }

    override fun saveRefreshToken(refreshToken: String) {
        settings.putString(REFRESH_TOKEN, refreshToken)
    }

    override fun getRefreshToken(): String {
        return settings.getString(REFRESH_TOKEN, "")
    }

    companion object {
        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
    }
}
