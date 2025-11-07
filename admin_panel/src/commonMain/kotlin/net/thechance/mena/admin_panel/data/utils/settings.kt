@file:OptIn(ExperimentalSettingsApi::class)

package net.thechance.mena.admin_panel.data.utils

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow

internal val FlowSettings.accessToken: Flow<String>
    get() = getStringFlow(ACCESS_TOKEN, "")

suspend fun FlowSettings.putAccessToken(value: String){
    putString(ACCESS_TOKEN, value)
}

internal val FlowSettings.refreshToken: Flow<String>
    get() = getStringFlow(REFRESH_TOKEN, "")

suspend fun FlowSettings.putRefreshToken(value: String){
    putString(REFRESH_TOKEN, value)
}

private const val ACCESS_TOKEN = "access_token"
private const val REFRESH_TOKEN = "refresh_token"