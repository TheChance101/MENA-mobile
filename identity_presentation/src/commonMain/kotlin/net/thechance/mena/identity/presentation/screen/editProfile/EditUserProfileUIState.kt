package net.thechance.mena.identity.presentation.screen.editProfile

import kotlinx.datetime.LocalDate
import net.thechance.mena.identity.domain.entity.Gender
import net.thechance.mena.identity.domain.util.getCurrentDate

data class EditUserProfileUIState(
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val profileImageUrl: String = "",
    val birthDate: LocalDate = getCurrentDate(),
    val gender: Gender = Gender.MALE,
    val showShareBottomSheet: Boolean = false,
    val showLanguageDialog: Boolean = false,
    val showThemeDialog: Boolean = false,
    val versionNumber: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
)
