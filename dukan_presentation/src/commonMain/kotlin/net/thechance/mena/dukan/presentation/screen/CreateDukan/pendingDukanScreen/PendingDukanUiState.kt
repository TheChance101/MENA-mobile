package net.thechance.mena.dukan.presentation.screen.createDukan.pendingDukanScreen

data class PendingDukanUiState(
    val appBarTitle: String = "My Dukan",
    val brandName: String = "",
    val titleTemplate: String = "Creating your dukan %s request is pending now",
    val subtitle: String = "Waiting until it be approved and add your shelves directly!"
)
