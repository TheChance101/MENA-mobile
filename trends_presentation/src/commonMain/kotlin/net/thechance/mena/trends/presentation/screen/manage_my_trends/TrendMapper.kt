package net.thechance.mena.trends.presentation.screen.manage_my_trends

import net.thechance.mena.trends.domain.entity.Trend


internal fun Trend.toUiState(): TrendUiState {
    return TrendUiState(
        id = id,
        thumbnailUrl = thumbnailUrl
    )
}