package net.thechance.mena.dukan.presentation.viewModel.mainScreen

data class MainScreenUiState(
    val errorMessage: String? = null,
    val dukanState: DukanState = DukanState()
) {
    data class DukanState(
        val name: String = "",
        val status: DukanStatusUi = DukanStatusUi.None
    )

    enum class DukanStatusUi {
        Pending,
        None,
        Approved
    }
}