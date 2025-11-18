package net.thechance.mena.identity.presentation.screen.profile

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error
import net.thechance.mena.identity.domain.util.AppLanguage
import net.thechance.mena.identity.domain.util.AppTheme
import org.jetbrains.compose.resources.StringResource

data class ProfileScreenUIState(
    val fullName: String = "",
    val userName: String = "",
    val profileImageUrl: String = "",
    val inviteLinkUrl: String = "",
    val showShareBottomSheet: Boolean = false,
    val showShareProfileDialog: Boolean = false,
    val versionNumber: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: StringResource? = null,
    val snackBarUiState: SnackBarUiState = SnackBarUiState(),
    val currentTheme: AppTheme = AppTheme.DEFAULT,
    val languageDialogUiState: LanguageDialogUiState = LanguageDialogUiState(),
    val themeDialogUiState: ThemeDialogUiState = ThemeDialogUiState()
)

data class LanguageDialogUiState(
    val isVisible: Boolean = false,
    val selectedAppLanguage: AppLanguage = AppLanguage.ENGLISH,
    val options: List<AppLanguage> = AppLanguage.entries.filterNot { it == AppLanguage.DEFAULT },
)

data class ThemeDialogUiState(
    val isVisible: Boolean = false,
    val selectedAppTheme: AppTheme = AppTheme.LIGHT,
    val options: List<AppTheme> = AppTheme.entries.filterNot { it == AppTheme.DEFAULT }
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
