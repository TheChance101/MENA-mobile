package net.thechance.mena.core_chat.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data class AyahMessageArgs(
    val surahId: Int,
    val ayahNumber: Int,
    val ayahContent: String,
)