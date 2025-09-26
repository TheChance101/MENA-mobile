package net.thechance.mena

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.identity.presentation.screen.login.LoginScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MenaTheme {
        Navigator(screen = LoginScreen()) { navigator ->
            SetStatusBarIconsDark()
            FadeTransition(navigator)
        }
    }
}