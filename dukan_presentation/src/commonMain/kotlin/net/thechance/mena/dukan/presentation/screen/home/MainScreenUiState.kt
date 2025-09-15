package net.thechance.mena.dukan.presentation.screen.home

data class MainScreenUiState(
    val errorMessage:String? = null,
    val dukanStatus: DukanStatusUi = DukanStatusUi.None
){
    enum class DukanStatusUi{
        Pending,
        None
    }
}