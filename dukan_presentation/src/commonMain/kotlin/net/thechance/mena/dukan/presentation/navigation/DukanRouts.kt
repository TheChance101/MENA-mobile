package net.thechance.mena.dukan.presentation.navigation

import kotlinx.serialization.Serializable

interface DukanRoute

@Serializable
object MainScreenRoute : DukanRoute

@Serializable
object CreateDukanRoute : DukanRoute

@Serializable
object MyDukanScreenRoute : DukanRoute

@Serializable
object PendingDukanScreen : DukanRoute
