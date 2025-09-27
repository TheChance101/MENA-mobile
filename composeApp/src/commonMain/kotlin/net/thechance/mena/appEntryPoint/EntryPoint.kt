package net.thechance.mena.appEntryPoint

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import net.thechance.mena.core_chat.api.CoreChatApi
import net.thechance.mena.designsystem.presentation.component.bottomNavigation.BottomNavigationBar
import net.thechance.mena.designsystem.presentation.component.bottomNavigation.BottomNavigationItem
import net.thechance.mena.dukan.api.DukanApi
import net.thechance.mena.faith.api.FaithApi
import net.thechance.mena.identity.api.IdentityFeatureApi
import net.thechance.mena.trends.api.TrendsApi
import net.thechance.mena.wallet.api.WalletApi
import org.koin.compose.koinInject


@Composable
fun EntryPoint(){
    var activeFeature: Feature by remember { mutableStateOf(Feature.CHAT) }

    Column(Modifier.fillMaxSize()) {
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