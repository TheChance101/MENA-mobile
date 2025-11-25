package net.thechance.mena.identity.data.dataSource.local.setting

import com.russhwolf.settings.Settings
import net.thechance.mena.identity.domain.util.AppTheme

internal var Settings.accessToken: String
    get() = getString(ACCESS_TOKEN, "")
    set(value) = putString(ACCESS_TOKEN, value)

internal var Settings.refreshToken: String
    get() = getString(REFRESH_TOKEN, "")
    set(value) = putString(REFRESH_TOKEN, value)

internal var Settings.lastRegistrationPhoneNumber: String
    get() = getString(LAST_REGISTRATION_PHONE_NUMBER, "")
    set(value) = putString(LAST_REGISTRATION_PHONE_NUMBER, value)

internal var Settings.lastRegistrationCountryCode: String
    get() = getString(LAST_REGISTRATION_COUNTRY_CODE, "")
    set(value) = putString(LAST_REGISTRATION_COUNTRY_CODE, value)

internal var Settings.lastRegistrationLocalNumber: String
    get() = getString(LAST_REGISTRATION_LOCAL_NUMBER, "")
    set(value) = putString(LAST_REGISTRATION_LOCAL_NUMBER, value)

internal var Settings.imageUploadCompleted: Boolean
    get() = getBoolean(IMAGE_UPLOAD_COMPLETED, false)
    set(value) = putBoolean(IMAGE_UPLOAD_COMPLETED, value)
internal var Settings.appLanguage: String
    get() = getString(APP_LANGUAGE, "")
    set(value) = putString(APP_LANGUAGE, value)

internal var Settings.appTheme: String
    get() = getString(APP_THEME, AppTheme.SYSTEM.name)
    set(value) = putString(APP_THEME, value)

//region keys

const val ACCESS_TOKEN = "access_token"
const val REFRESH_TOKEN = "refresh_token"
const val LAST_REGISTRATION_PHONE_NUMBER = "last_registration_phone_number"
const val LAST_REGISTRATION_COUNTRY_CODE = "last_registration_country_code"
const val LAST_REGISTRATION_LOCAL_NUMBER = "last_registration_local_number"
const val IMAGE_UPLOAD_COMPLETED = "image_upload_completed"
const val APP_LANGUAGE = "app_language"
const val APP_THEME = "app_theme"

//endregion