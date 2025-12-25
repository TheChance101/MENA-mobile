package net.thechance.mena.identity.presentation.screen.profile.privacyAndPolicy

import net.thechance.mena.identity.domain.model.Section

data class PrivacyAndPolicyScreenUIState(
    val lastUpdateDate: String = "",
    val privacyAndPolicySections: List<PrivacyAndPolicySectionUIState> = emptyList(),
    val isLoading:Boolean = false
)
data class PrivacyAndPolicySectionUIState(
    val title: String,
    val content: String
)

fun Section.toUIState(): PrivacyAndPolicySectionUIState{
    return PrivacyAndPolicySectionUIState(
        title = this.title,
        content = this.content
    )
}
