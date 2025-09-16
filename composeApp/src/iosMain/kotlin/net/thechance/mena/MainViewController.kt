package net.thechance.mena

import androidx.compose.ui.window.ComposeUIViewController
import net.thechance.mena.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }