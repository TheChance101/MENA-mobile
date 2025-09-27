package net.thechance.mena.api

import cafe.adriel.voyager.core.screen.Screen
import net.thechance.mena.identity.presentation.api.ComposeAppApi
import net.thechance.mena.screen.main.MainScreen

class ComposeAppApiImpl : ComposeAppApi {
    override fun getMainScreen(): Screen {
        return MainScreen()
    }
}