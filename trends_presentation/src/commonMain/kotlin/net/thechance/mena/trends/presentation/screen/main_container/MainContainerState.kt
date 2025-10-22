package net.thechance.mena.trends.presentation.screen.main_container

data class MainContainerState(
    val isCategoriesAlreadySelectedByUser: Boolean? = null,
    val error: MainContainerErrorState? = null,
)

sealed class MainContainerErrorState {
    object NoInternet : MainContainerErrorState()
    data class Unknown(val message: String?) : MainContainerErrorState()
}
