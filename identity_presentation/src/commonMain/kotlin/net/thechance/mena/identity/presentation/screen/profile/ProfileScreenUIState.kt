package net.thechance.mena.identity.presentation.screen.profile

import org.jetbrains.compose.resources.StringResource

data class ProfileScreenUIState(
    val fullName:String = "",
    val userName:String = "",
    val profileImageUrl:String = "",
    val showShareBottomSheet:Boolean = false,
    val showThemeDialog:Boolean = false,
    val versionNumber:String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: StringResource? = null,
    val languageDialogUiState: LanguageDialogUiState = LanguageDialogUiState()
)
data class LanguageDialogUiState(
    val isVisible:Boolean=false,
    val selectedLanguage: Language =Language.English,
    val options: List<Language> =Language.entries,
)
enum class Language(val iso: String) {
    English(iso = "en"),
    Arabic(iso = "ar")
}