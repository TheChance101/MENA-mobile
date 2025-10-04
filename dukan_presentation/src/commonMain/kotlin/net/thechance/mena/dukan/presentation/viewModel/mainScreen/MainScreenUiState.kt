package net.thechance.mena.dukan.presentation.viewModel.mainScreen

data class MainScreenUiState(
    val errorMessage: String? = null,
    val dukanState: DukanState = DukanState()
) {
    data class DukanState(
        val name: String = "",
        val status: DukanStatusUi = DukanStatusUi.Loading
    )

    enum class DukanStatusUi {
        Loading,
        Pending,
        None,
        Approved
    }
}