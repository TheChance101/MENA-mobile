package net.thechance.mena.identity.presentation.screen.profile.profileMainScreen

import net.thechance.mena.identity.domain.util.AppLanguage
import net.thechance.mena.identity.domain.util.AppTheme
import net.thechance.mena.identity.presentation.screen.profile.shared.UserUIState
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
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
    val currentLanguage: AppLanguage = AppLanguage.DEFAULT,
    val currentTheme: AppTheme = AppTheme.SYSTEM,
    val languageDialogUiState: LanguageDialogUiState = LanguageDialogUiState(),
    val themeDialogUiState: ThemeDialogUiState = ThemeDialogUiState(),
    val userUiState: UserUIState = UserUIState()
)

data class LanguageDialogUiState(
    val isVisible: Boolean = false,
    val selectedAppLanguage: AppLanguage = AppLanguage.DEFAULT,
    val options: List<AppLanguage> = AppLanguage.entries.filterNot { it == AppLanguage.DEFAULT },
)

data class ThemeDialogUiState(
    val isVisible: Boolean = false,
    val selectedAppTheme: AppTheme = AppTheme.SYSTEM,
    val options: List<AppTheme> = AppTheme.entries
)