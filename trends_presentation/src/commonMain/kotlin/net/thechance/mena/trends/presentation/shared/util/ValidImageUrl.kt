package net.thechance.mena.trends.presentation.shared.util

fun isValidImageUrl(url: String?): Boolean {
    return url?.startsWith("http") == true
}