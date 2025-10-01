package net.thechance.mena

actual object AppEnvironment {
    actual val baseUrl: String = BuildConfig.BASE_URL
    actual val versionName: String = BuildConfig.VERSION_NAME
}