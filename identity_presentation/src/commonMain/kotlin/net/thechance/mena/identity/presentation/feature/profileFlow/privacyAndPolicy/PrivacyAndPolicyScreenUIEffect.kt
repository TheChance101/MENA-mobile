package net.thechance.mena.identity.presentation.feature.profileFlow.privacyAndPolicy

sealed interface PrivacyAndPolicyScreenUIEffect {

    data object NavigateBack : PrivacyAndPolicyScreenUIEffect
}