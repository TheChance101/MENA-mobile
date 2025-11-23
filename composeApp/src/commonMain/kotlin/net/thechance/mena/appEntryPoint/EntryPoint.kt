package net.thechance.mena.appEntryPoint

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.core_chat.api.CoreChatApi
import net.thechance.mena.identity.api.IdentityFeatureApi
import net.thechance.mena.identity.domain.service.AuthorizationService
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EntryPoint(
    identityApi: IdentityFeatureApi = koinInject(),
    viewModel: MainEntryViewModel = koinViewModel(),
    authorizationService: AuthorizationService = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val accessToken by authorizationService.observeAccessToken().collectAsStateWithLifecycle()

    if (accessToken.isBlank()) {
        identityApi.LoginFlow()
        return
    }

    if (state.deepLink?.userId != null) {
        ChatEntry(
            deepLink = state.deepLink,
            onNavigateBack = viewModel::clearDeepLink
        )
        return
    }

    LoggedInContainer(
        state = state,
        listener = viewModel,
    )
}

@Composable
private fun ChatEntry(deepLink: DeepLink?, onNavigateBack: () -> Unit) {
    val chatApi = koinInject<CoreChatApi>()
    chatApi.ChatEntry(
        userId = deepLink?.userId.orEmpty(),
        onNavigateBack = onNavigateBack
    )
}