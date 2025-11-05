package net.thechance.mena.identity.data.dataSource.local.setting

import com.russhwolf.settings.Settings

internal var Settings.accessToken: String
    get() = getString(ACCESS_TOKEN, "")
    set(value) = putString(ACCESS_TOKEN, value)

internal var Settings.refreshToken: String
    get() = getString(REFRESH_TOKEN, "")
    set(value) = putString(REFRESH_TOKEN, value)

internal var Settings.appLanguage: String
    get() = getString(APP_LANGUAGE, "")
    set(value) = putString(APP_LANGUAGE, value)


//region keys

const val ACCESS_TOKEN = "access_token"
const val REFRESH_TOKEN = "refresh_token"
const val APP_LANGUAGE = "app_language"

//endregion