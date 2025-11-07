package net.thechance.mena.trends.presentation.di

import platform.Foundation.NSBundle


actual val trendStorageAccessSecret: String =
    NSBundle.mainBundle.objectForInfoDictionaryKey("STORAGE_TRENDS_ACCESS_SECRET") as? String
        ?: throw Exception("STORAGE_TRENDS_ACCESS_SECRET not found")
