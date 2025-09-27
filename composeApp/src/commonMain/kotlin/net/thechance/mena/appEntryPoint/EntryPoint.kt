package net.thechance.mena.appEntryPoint

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
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.core_chat.api.CoreChatApi
import net.thechance.mena.designsystem.presentation.component.bottomNavigation.BottomNavigationBar
import net.thechance.mena.designsystem.presentation.component.bottomNavigation.BottomNavigationItem
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.api.DukanApi
import net.thechance.mena.faith.api.FaithApi
import net.thechance.mena.identity.api.IdentityFeatureApi
import net.thechance.mena.identity.domain.service.AuthorizationService
import net.thechance.mena.trends.api.TrendsApi
import net.thechance.mena.wallet.api.WalletApi
import org.koin.compose.koinInject


@Composable
fun EntryPoint(){
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
private fun LoggedInContainer(){
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
        BottomNavigationBar(onItemClick = { navBarItem ->
            //TODO: refactor this work around with refactoring bottom navbar
            activeFeature = navBarItem.toFeature()
        })
    }
}

@Composable
private fun ColumnScope.FeatureContent(activeFeature: Feature){
    val identityApi = koinInject<IdentityFeatureApi>()
    val dukanApi = koinInject<DukanApi>()
    val trendsApi = koinInject<TrendsApi>()
    val faithApi = koinInject<FaithApi>()
    val chatApi = koinInject<CoreChatApi>()
    val walletApi = koinInject<WalletApi>()

    Box(Modifier.weight(1f)) {
        when(activeFeature){
            Feature.CHAT -> chatApi.TabEntry()
            Feature.DUKAN -> dukanApi.TabEntry()
            Feature.TREND -> trendsApi.TabEntry()
            Feature.FAITH -> faithApi.TabEntry()
            Feature.PROFILE -> identityApi.ProfileTabEntry()
            Feature.WALLET -> walletApi.WalletEntry()
        }
    }
}

private enum class Feature{
    CHAT, DUKAN, TREND, FAITH, PROFILE, WALLET
}

//TODO: refactor this work around with refactoring bottom navbar
private fun BottomNavigationItem.toFeature(): Feature{
    return when(this.title) {
        "Home" -> Feature.CHAT
        "Dukan" -> Feature.DUKAN
        "Trends" -> Feature.TREND
        "Faith" -> Feature.FAITH
        "Profile" -> Feature.PROFILE
        "Wallet" -> Feature.WALLET
        else -> throw Exception("Unsupported feature, or invalid tab name")
    }
}