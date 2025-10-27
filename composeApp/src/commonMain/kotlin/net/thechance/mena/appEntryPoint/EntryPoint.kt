package net.thechance.mena.appEntryPoint

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.composeapp.generated.resources.Res
import mena.composeapp.generated.resources.dukan
import mena.composeapp.generated.resources.faith
import mena.composeapp.generated.resources.home
import mena.composeapp.generated.resources.ic_dukan
import mena.composeapp.generated.resources.ic_dukan_selected
import mena.composeapp.generated.resources.ic_faith
import mena.composeapp.generated.resources.ic_faith_selected
import mena.composeapp.generated.resources.ic_home
import mena.composeapp.generated.resources.ic_home_selected
import mena.composeapp.generated.resources.ic_profile
import mena.composeapp.generated.resources.ic_profile_selected
import mena.composeapp.generated.resources.ic_trends
import mena.composeapp.generated.resources.ic_trends_selected
import mena.composeapp.generated.resources.profile
import mena.composeapp.generated.resources.trends
import net.thechance.mena.core_chat.api.CoreChatApi
import net.thechance.mena.designsystem.presentation.component.bottomNavigation.BottomNavigationBar
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.api.DukanApi
import net.thechance.mena.faith.api.FaithApi
import net.thechance.mena.identity.api.IdentityFeatureApi
import net.thechance.mena.identity.domain.service.AuthorizationService
import net.thechance.mena.trends.api.TrendsApi
import net.thechance.mena.wallet.api.WalletApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject


@Composable
fun EntryPoint() {
    val identityApi = koinInject<IdentityFeatureApi>()
    val authorizationService = koinInject<AuthorizationService>()

    //Temp solution to be replaced later
    val token by authorizationService.observeAccessToken().collectAsStateWithLifecycle()
    if (token.isNotBlank()) {
        LoggedInContainer()
    } else {
        identityApi.LoginFlow()
    }
}

@Composable
private fun LoggedInContainer() {
    var activeFeature: Feature by remember { mutableStateOf(Feature.CHAT) }
    Column(
        Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surfaceLow)
            .navigationBarsPadding()
            .systemBarsPadding()
            .background(Theme.colorScheme.background.surfaceHigh)
    ) {
        FeatureContent(activeFeature)
        BottomNavigationBar {
            bottomNavigationItem(
                selectedIcon = painterResource(Res.drawable.ic_home_selected),
                notSelectedIcon = painterResource(Res.drawable.ic_home),
                title = stringResource(Res.string.home),
                entry = { activeFeature = Feature.CHAT }
            )

            bottomNavigationItem(
                selectedIcon = painterResource(Res.drawable.ic_dukan_selected),
                notSelectedIcon = painterResource(Res.drawable.ic_dukan),
                title = stringResource(Res.string.dukan),
                entry = { activeFeature = Feature.DUKAN }
            )

            bottomNavigationItem(
                selectedIcon = painterResource(Res.drawable.ic_trends_selected),
                notSelectedIcon = painterResource(Res.drawable.ic_trends),
                title = stringResource(Res.string.trends),
                entry = { activeFeature = Feature.TREND }
            )

            bottomNavigationItem(
                selectedIcon = painterResource(Res.drawable.ic_faith_selected),
                notSelectedIcon = painterResource(Res.drawable.ic_faith),
                title = stringResource(Res.string.faith),
                entry = { activeFeature = Feature.FAITH }
            )

            bottomNavigationItem(
                selectedIcon = painterResource(Res.drawable.ic_profile_selected),
                notSelectedIcon = painterResource(Res.drawable.ic_profile),
                title = stringResource(Res.string.profile),
                entry = { activeFeature = Feature.PROFILE }
            )
        }
    }
}

@Composable
private fun ColumnScope.FeatureContent(activeFeature: Feature) {
    val identityApi = koinInject<IdentityFeatureApi>()
    val dukanApi = koinInject<DukanApi>()
    val trendsApi = koinInject<TrendsApi>()
    val faithApi = koinInject<FaithApi>()
    val chatApi = koinInject<CoreChatApi>()
    val walletApi = koinInject<WalletApi>()

    Box(Modifier.weight(1f)) {
        Crossfade(targetState = activeFeature) { feature ->
            when (feature) {
                Feature.CHAT -> chatApi.TabEntry()
                Feature.DUKAN -> dukanApi.TabEntry()
                Feature.TREND -> trendsApi.TabEntry()
                Feature.FAITH -> faithApi.TabEntry()
                Feature.PROFILE -> identityApi.ProfileTabEntry()
                Feature.WALLET -> walletApi.WalletEntry(navigateBack = {})
            }
        }
    }
}

private enum class Feature {
    CHAT, DUKAN, TREND, FAITH, PROFILE, WALLET
}