package net.thechance.mena.admin_panel

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import net.thechance.mena.admin_panel.di.AppModule
import net.thechance.mena.admin_panel.di.networkModule
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.mena_logo
import org.jetbrains.compose.resources.painterResource
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
        title = "Mena Admin Panel",
        icon = painterResource(Res.drawable.mena_logo)
    ) {
        App()
    }
}