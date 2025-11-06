package net.thechance.mena.admin_panel.data.utils

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow

internal var Settings.accessToken: String
    get() = getString(ACCESS_TOKEN, "")
    set(value) = putString(ACCESS_TOKEN, value)

internal var Settings.refreshToken: String
    get() = getString(REFRESH_TOKEN, "")
    set(value) = putString(REFRESH_TOKEN, value)

private const val ACCESS_TOKEN = "access_token"
private const val REFRESH_TOKEN = "refresh_token"

internal val observableToken: MutableStateFlow<String> = MutableStateFlow(Settings().accessToken)