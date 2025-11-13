package net.thechance.mena.admin_panel

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import net.thechance.mena.admin_panel.di.AppModule
import net.thechance.mena.admin_panel.di.networkModule
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.app_name
import net.thechance.mena.admin_panel.resources.mena_logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

fun main() = application {
    startKoin {
        modules(
            AppModule().module,
            networkModule
        )
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        alwaysOnTop = false,
        icon = painterResource(Res.drawable.mena_logo),
        state = rememberWindowState(placement = WindowPlacement.Maximized)
    ) {
        App()
    }
}