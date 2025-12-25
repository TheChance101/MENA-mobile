package net.thechance.mena.identity.presentation.screen.profile.privacyAndPolicy

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.model.PrivacyAndPolicy
import net.thechance.mena.identity.domain.repository.ApplicationInfoRepository
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.errorState.ErrorState
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.util.toFormattedDate
import org.jetbrains.compose.resources.StringResource

class PrivacyAndPolicyScreenViewModel(
    private val applicationInfoRepository: ApplicationInfoRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    BaseScreenModel<PrivacyAndPolicyScreenUIState, PrivacyAndPolicyScreenUIEffect>(
        PrivacyAndPolicyScreenUIState()
    ), PrivacyAndPolicyScreenInteractionListener {

    init {
        getPrivacyAndPolicy()
    }

    override fun onClickBack() {
        sendNewEffect(PrivacyAndPolicyScreenUIEffect.NavigateBack)
    }

    private fun getPrivacyAndPolicy() {
        updateState { copy(isLoading = true) }
        tryToExecute(
            function = { applicationInfoRepository.getPrivacyAndPolicy() },
            onSuccess = ::onGetPrivacyAndPolicySuccess,
            onError = ::onGetPrivacyAndPolicyError,
            dispatcher = dispatcher
        )
    }

    private fun onGetPrivacyAndPolicySuccess(privacyAndPolicy: PrivacyAndPolicy) {
        updateState {
            copy(
                isLoading = false,
                privacyAndPolicySections = privacyAndPolicy.sections.map { it.toUIState() },
                lastUpdateDate = privacyAndPolicy.updateDate.toFormattedDate()
            )
        }
    }

    private fun onGetPrivacyAndPolicyError(throwable: Throwable) {
        updateState {
            copy(isLoading = false)
        }
        sendNewEffect(
            PrivacyAndPolicyScreenUIEffect.ShowSnackBarError(
                errorStringResource = mapErrorMessage(throwable)
            )
        )
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource {
        return when (throwable) {
            is AuthenticationException -> mapAuthenticationErrorToMessage(
                handlePrivacyAndPolicyException(throwable)
            )

            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }
}