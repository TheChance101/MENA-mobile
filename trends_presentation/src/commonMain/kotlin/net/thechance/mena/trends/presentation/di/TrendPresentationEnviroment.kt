package net.thechance.mena.trends.presentation.di

expect object TrendPresentationEnvironment{
    val trendStorageAccessSecret: String
    val trendStorageBaseUrl: String
}