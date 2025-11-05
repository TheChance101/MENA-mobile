package net.thechance.mena.identity.presentation.screen.profile

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error
import org.jetbrains.compose.resources.StringResource

data class ProfileScreenUIState(
    val fullName:String = "",
    val userName:String = "",
    val profileImageUrl:String = "",
    val shareLinkUrl: String = "",
    val inviteLinkUrl: String = "",
    val showCopiedMessage: Boolean = false,
    val showShareBottomSheet:Boolean = false,
    val showLanguageDialog:Boolean = false,
    val showShareProfileDialog:Boolean = false,
    val showThemeDialog:Boolean = false,
    val versionNumber:String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: StringResource? = null,
    val snackBarUiState: SnackBarUiState = SnackBarUiState()
)

data class SnackBarUiState(
    val isVisible: Boolean = false,
    val snackBarType: SnackBarType = SnackBarType.ERROR,
    val message: StringResource = Res.string.error,
)

enum class SnackBarType {
    ERROR,
    SUCCESS,
}
