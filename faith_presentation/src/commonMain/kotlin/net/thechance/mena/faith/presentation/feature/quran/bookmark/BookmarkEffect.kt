package net.thechance.mena.faith.presentation.feature.quran.bookmark

sealed interface BookmarkEffect {
    data object NavigateBack : BookmarkEffect
    data object NavigateToSur: BookmarkEffect
}
