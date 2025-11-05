package net.thechance.mena.identity.presentation.screen.profile

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
)