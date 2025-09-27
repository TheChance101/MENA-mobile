package net.thechance.mena

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.identity.presentation.api.ComposeAppApi
import net.thechance.mena.identity.presentation.screen.login.LoginScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    MenaTheme {
        val appApi = koinInject<ComposeAppApi>()
        Navigator(screen = LoginScreen(appApi)) { navigator ->
            SetStatusBarIconsDark()
            FadeTransition(navigator)
        }
    }
}