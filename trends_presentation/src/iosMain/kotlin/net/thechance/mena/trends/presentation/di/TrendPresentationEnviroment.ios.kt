package net.thechance.mena.trends.presentation.di

import platform.Foundation.NSBundle


actual val trendStorageAccessSecret: String =
    NSBundle.mainBundle.objectForInfoDictionaryKey("TRENDS_STORAGE_ACCESS_SECRET") as? String
        ?: throw Exception("TRENDS_STORAGE_ACCESS_SECRET not found")
