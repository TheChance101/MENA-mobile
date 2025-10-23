package net.thechance.mena.trends.presentation.screen.main_container

import net.thechance.mena.trends.presentation.shared.base.ErrorState

data class MainContainerState(
    val isCategoriesAlreadySelectedByUser: Boolean? = null,
    val error: ErrorState? = null,
)