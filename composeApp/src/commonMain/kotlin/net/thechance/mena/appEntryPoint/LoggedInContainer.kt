package net.thechance.mena.appEntryPoint

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import net.thechance.mena.trends.api.TrendsApi
import net.thechance.mena.wallet.api.WalletApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject


@Composable
fun LoggedInContainer(
    activeFeature: Feature,
    listener: MainEntryInteractionListener
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surfaceLow)
            .navigationBarsPadding()
    ) {
        FeatureContent(activeFeature = activeFeature)

        BottomNavigationBar(
            selectedItemIndex = getSelectedNavigationIndex(activeFeature),
        ) {
            bottomNavigationItem(
                selectedIcon = painterResource(Res.drawable.ic_home_selected),
                notSelectedIcon = painterResource(Res.drawable.ic_home),
                title = stringResource(Res.string.home),
                entry = { listener.setActiveFeature(Feature.CHAT) }
            )

            bottomNavigationItem(
                selectedIcon = painterResource(Res.drawable.ic_dukan_selected),
                notSelectedIcon = painterResource(Res.drawable.ic_dukan),
                title = stringResource(Res.string.dukan),
                entry = { listener.setActiveFeature(Feature.DUKAN) }
            )

            bottomNavigationItem(
                selectedIcon = painterResource(Res.drawable.ic_trends_selected),
                notSelectedIcon = painterResource(Res.drawable.ic_trends),
                title = stringResource(Res.string.trends),
                entry = { listener.setActiveFeature(Feature.TREND) }
            )

            bottomNavigationItem(
                selectedIcon = painterResource(Res.drawable.ic_faith_selected),
                notSelectedIcon = painterResource(Res.drawable.ic_faith),
                title = stringResource(Res.string.faith),
                entry = { listener.setActiveFeature(Feature.FAITH) }
            )

            bottomNavigationItem(
                selectedIcon = painterResource(Res.drawable.ic_profile_selected),
                notSelectedIcon = painterResource(Res.drawable.ic_profile),
                title = stringResource(Res.string.profile),
                entry = { listener.setActiveFeature(Feature.PROFILE) }
            )
        }
    }
}

@Composable
private fun ColumnScope.FeatureContent(
    activeFeature: Feature,
    identityApi: IdentityFeatureApi = koinInject(),
    dukanApi: DukanApi = koinInject(),
    trendsApi: TrendsApi = koinInject(),
    faithApi: FaithApi = koinInject(),
    chatApi: CoreChatApi = koinInject(),
    walletApi: WalletApi = koinInject(),
) {
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

private fun getSelectedNavigationIndex(activeFeature: Feature): Int {
    return when (activeFeature) {
        Feature.CHAT -> 0
        Feature.DUKAN -> 1
        Feature.TREND -> 2
        Feature.FAITH -> 3
        Feature.PROFILE -> 4
        Feature.WALLET -> -1 // Not in bottom nav
    }
}