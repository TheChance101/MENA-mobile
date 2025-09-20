package net.thechance.mena

import platform.Foundation.NSBundle

actual object AppEnvironment {
    actual val baseUrl: String =
        NSBundle.mainBundle.objectForInfoDictionaryKey("BASE_URL") as? String
            ?: throw Exception("BASE_URL not found")
}