package net.thechance.mena.trends.presentation.screen.main_container

import net.thechance.mena.identity.domain.util.AppTheme
import net.thechance.mena.trends.presentation.shared.base.ErrorState

data class MainContainerState(
    val isCategoriesAlreadySelectedByUser: Boolean? = null,
    val error: ErrorState? = null,
    val currentTheme: AppTheme = AppTheme.LIGHT
)