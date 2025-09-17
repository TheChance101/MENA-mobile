package net.thechance.mena.faith.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data object SurRoute

@Serializable
data class SurahDetailsRoute(
    val surahId: Int,
    val surahName: String
)

@Serializable
data object BookmarksRoute
