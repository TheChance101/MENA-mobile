package net.thechance.mena.trends.presentation.di

import net.thechance.mena.trends.presentation.BuildConfig

actual object TrendPresentationEnvironment {
    actual val trendStorageAccessSecret = BuildConfig.TRENDS_ACCESS_SECRET
    actual val trendStorageBaseUrl: String = ""
}